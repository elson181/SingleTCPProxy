FROM openjdk:21-jdk-slim
LABEL author="elson"

WORKDIR /app

# Copy your built jar file into container
COPY target/client-1.0-SNAPSHOT.jar app.jar

# Expose HTTP proxy port
EXPOSE 8080

# Run the proxy
CMD ["java", "-jar", "app.jar"]
