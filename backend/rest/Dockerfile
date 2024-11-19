FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/rest-0.0.5-SNAPSHOT.jar /app/rest-0.0.5-SNAPSHOT.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "rest-0.0.5-SNAPSHOT.jar"]