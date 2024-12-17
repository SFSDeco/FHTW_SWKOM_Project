FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/rest-0.1.0-Alpha.jar /app/rest-0.1.0-Alpha.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "rest-0.1.0-Alpha.jar"]