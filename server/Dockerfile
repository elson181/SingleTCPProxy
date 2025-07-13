FROM openjdk:21-jdk-slim
LABEL author="elson"

WORKDIR /app

COPY target/server-1.0-SNAPSHOT.jar app.jar

EXPOSE 9090

CMD ["java", "-jar", "app.jar"]
