# syntax=docker/dockerfile:1
FROM openjdk:17.0.1-jdk-slim
WORKDIR /opt/pricer
COPY target/parser-service.jar .
ENTRYPOINT ["java", "-jar", "/opt/pricer/parser-service.jar"]