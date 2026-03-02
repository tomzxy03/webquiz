# ==============================
# Stage 1: Build
# ==============================
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /build

# Copy trước pom.xml để tận dụng cache dependency
COPY pom.xml .
RUN mvn -B dependency:go-offline -Dmaven.test.skip=true
# Copy source sau
COPY src ./src

RUN mvn -B package -DskipTests
# ==============================
# Stage 2: Runtime (NHẸ)
# ==============================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]