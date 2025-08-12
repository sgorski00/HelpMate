FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app
COPY pom.xml .
COPY user-service/pom.xml user-service/pom.xml
COPY user-service/src user-service/src
RUN mvn clean package -DskipTests -pl user-service -am

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=builder /app/user-service/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
