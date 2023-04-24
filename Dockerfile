# Use the official OpenJDK image as a parent image
FROM openjdk:11-jre-slim

# Set the working directory to /app
WORKDIR /app

RUN apk add --no-cache maven

COPY . .

RUN mvn clean package


# Copy the Authoring Services jar file to the container
COPY target/authoring-services*.jar app.jar

# Expose the default HTTP port (8081)
EXPOSE 8081

# Run the Authoring Services jar file when the container starts
ENTRYPOINT ["java", "-Xmx1g", "-jar", "app.jar"]
