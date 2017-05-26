package org.ihtsdo.snowowl.authoring.single.api.service;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.ihtsdo.otf.dao.s3.S3ClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class DialectConversionService {

	private final S3ClientImpl s3Client;
	private final String bucket;
	private final String usToGbTermsMapPath;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private Map<String, String> dialectUsToGbMap;

	public DialectConversionService(
			@Value("${aws.key}") String accessKey,
			@Value("${aws.secretKey}") String secretKey,
			@Value("${aws.s3.spell-check.bucket}") String bucket,
			@Value("${aws.s3.dialect.us-to-gb-map.path}") String usToGbTermsMapPath) throws ServiceException, IOException {
		this.s3Client = new S3ClientImpl(new BasicAWSCredentials(accessKey, secretKey));
		this.bucket = bucket;
		this.usToGbTermsMapPath = usToGbTermsMapPath;
		this.dialectUsToGbMap = new HashMap<>();
	}

	@PostConstruct
	public void loadList() throws ServiceException {
		doLoadList(getMapObject().getObjectContent());
	}

	private void doLoadList(InputStream objectContent) throws ServiceException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(objectContent))) {
			logger.info("Loading US to GB dialect conversion map");
			reader.readLine();// Discard header
			Map<String, String> newDialectUsToGbMap = new HashMap<>();
			String line;
			while ((line = reader.readLine()) != null) {
				String[] split = line.split("\\t");
				newDialectUsToGbMap.put(split[0], split[1]);
			}
			dialectUsToGbMap = newDialectUsToGbMap;
		} catch (IOException e) {
			throw new ServiceException("Failed to load spelling list from S3.", e);
		}
	}

	public Map<String, String > getAvailableEnUsToEnGbConversions(Set<String> words) {
		Map<String, String> conversions = new HashMap<>();
		for (String usWord : words) {
			if (!StringUtils.isEmpty(usWord)) {
				String gbWord = dialectUsToGbMap.get(usWord);
				if (!StringUtils.isEmpty(gbWord)) {
					// Preserve first letter capitalization
					if (usWord.substring(0, 1).equals(usWord.substring(0, 1).toUpperCase())) {
						gbWord = gbWord.substring(0, 1).toUpperCase() + gbWord.substring(1);
					}
					conversions.put(usWord, gbWord);
				}
			}
		}
		return conversions;
	}

	public S3Object getMapObject() {
		return s3Client.getObject(bucket, usToGbTermsMapPath);
	}

	public void replaceMap(MultipartFile file) throws IOException, ServiceException {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(file.getSize());
		try (InputStream inputStream = file.getInputStream()) {
			s3Client.putObject(bucket, usToGbTermsMapPath, inputStream, objectMetadata);
			loadList();
		}
	}

	public void addWordPair(String newUsWord, String newGbWord) throws IOException, ServiceException {
		logger.info("Adding word pair to US/GB map '{}'", newUsWord, newGbWord);
		updateList((reader, writer) -> {

			// Write header
			writer.write(reader.readLine());
			writer.newLine();

			boolean inserted = false;
			String listPair;
			while ((listPair = reader.readLine()) != null) {
				String usWord = listPair.split("\\t")[0];
				if (!inserted) {
					int comparison = newUsWord.compareToIgnoreCase(usWord);
					if (comparison == 0) {
						throw new IllegalArgumentException(String.format("US Word '%s' is already present in this dialect map.", usWord));
					} else if (comparison < 0) {
							String newPair = newUsWord + "\t" + newGbWord;
						logger.info("Inserting '{}' before '{}'", newPair, listPair);
						writer.write(newPair);
						writer.newLine();
						inserted = true;
					}
				}
				writer.write(listPair);
				writer.newLine();
			}
			return true;
		});
	}

	public boolean deleteWordPair(String usWord) throws IOException, ServiceException {
		return updateList((reader, writer) -> {

			// Write header
			writer.write(reader.readLine());
			writer.newLine();

			boolean wordFound = false;
			String line;
			while ((line = reader.readLine()) != null) {
				String listUsWord = line.split("\\t")[0];
				if (!wordFound && listUsWord.equalsIgnoreCase(usWord)) {
					// Don't write this word pair to the new file
					wordFound = true;
				} else {
					writer.write(line);
					writer.newLine();
				}
			}
			if (!wordFound) {
				throw new IllegalArgumentException(String.format("Word '%s' is not in the map.", usWord));
			}
			logger.info("Removing word pair from US/GB map '{}'", line);
			return wordFound;
		});
	}

	private boolean updateList(FileModifier fileModifier) throws IOException, ServiceException {
		try (S3ObjectInputStream inputStream = getMapObject().getObjectContent()) {
			File modifiedList = Files.createTempFile("us-gb-map", "txt").toFile();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
				boolean changes;
				try (BufferedWriter writer = new BufferedWriter(new FileWriter(modifiedList))) {
					changes = fileModifier.modifyFile(reader, writer);
				}
				if (changes) {
					s3Client.putObject(bucket, usToGbTermsMapPath, modifiedList);
					logger.info("Load US/GB map");
					doLoadList(new FileInputStream(modifiedList));
				}
				return changes;
			} finally {
				modifiedList.delete();
			}
		}
	}

	private interface FileModifier {
		boolean modifyFile(BufferedReader reader, BufferedWriter writer) throws IOException;
	}
}
