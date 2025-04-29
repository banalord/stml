FROM maven:3.8.1-jdk-8-slim as builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

CMD ["java","-jar","/app/target/shuatimalou-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]