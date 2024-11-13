# Build Stage: Use an available Maven image with OpenJDK 17
FROM maven:3.8.3-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml to install dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the entire source code to build the application
COPY src ./src

# Build the application and skip tests for faster build time
RUN mvn clean package -DskipTests

# Runtime Stage: Use OpenJDK slim image for running the app
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage to the runtime stage
COPY --from=build /app/target/swagger-generator-0.0.1-SNAPSHOT.jar /app/swagger-generator.jar

# Expose port 8080
EXPOSE 8080

# Set the entrypoint for the application
ENTRYPOINT ["java", "-jar", "/app/swagger-generator.jar"]
