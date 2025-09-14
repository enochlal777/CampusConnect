# Stage 1: Build the Spring Boot app using OpenJDK 21 and Maven
FROM openjdk:21-jdk-slim AS build

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy Maven wrapper and configuration
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Download dependencies for caching
RUN ./mvnw dependency:go-offline -B

# Copy the rest of the source code
COPY src ./src

# Build the JAR file
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the Spring Boot app with OpenJDK 21
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java","-jar","app.jar"]
