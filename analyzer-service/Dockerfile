# syntax=docker/dockerfile:1

# ---- Build stage ------------------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Resolve the parent POM and module POMs first for better layer caching.
COPY pom.xml .
COPY analyzer-service/pom.xml analyzer-service/pom.xml
COPY report-service/pom.xml report-service/pom.xml

COPY analyzer-service/src analyzer-service/src

RUN mvn -B -q -pl analyzer-service -DskipTests package

# ---- Runtime stage -----------------------------------------------------------
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/analyzer-service/target/analyzer-service-1.0.0-SNAPSHOT.jar app.jar

ENV JAVA_OPTS="-Xmx512m -XX:MaxMetaspaceSize=256m" \
    SERVER_PORT=8081

EXPOSE 8081
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]