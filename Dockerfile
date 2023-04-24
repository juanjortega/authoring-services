# Use the official OpenJDK image as a parent image
FROM openjdk:11-jre-slim

# Set the working directory to /app
WORKDIR /app

# Install apk package manager
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    ca-certificates \
    curl \
    gnupg \
    && curl -fsSL https://dl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    echo "deb [arch=amd64] https://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && \
    apt-get install -y --no-install-recommends google-chrome-stable && \
    rm -rf /var/lib/apt/lists/*

RUN apt-get update && apt-get install -y --no-install-recommends \
    maven \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Copy the source code to the container
COPY . .

# Build the application
RUN mvn clean package

# Copy the Authoring Services jar file to the container
COPY target/authoring-services*.jar app.jar

# Expose the default HTTP port (8081)
EXPOSE 8081

# Run the Authoring Services jar file when the container starts
ENTRYPOINT ["java", "-Xmx1g", "-jar", "app.jar"]
