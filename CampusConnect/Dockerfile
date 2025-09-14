# Stage 1: Build the Spring Boot app using Maven with Java 21
FROM maven:3.9.2-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy Maven wrapper and configuration from the inner CampusConnect folder
COPY CampusConnect/pom.xml .
COPY CampusConnect/mvnw .
COPY CampusConnect/.mvn .mvn

# Download dependencies for caching
RUN ./mvnw dependency:go-offline -B

# Copy the rest of the source code from the inner CampusConnect folder
COPY CampusConnect/src ./src

# Build the JAR file
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the Spring Boot app using Java 21
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java","-jar","app.jar"]
