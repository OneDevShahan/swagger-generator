# Use the official Maven image to build the project
# Build stage
FROM maven:3.8.6-openjdk-17-slim AS build

# Set the working directory to /app
WORKDIR /app

# Copy the pom.xml and install dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the entire project and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Use an OpenJDK image to run the application
# Runtime stage
FROM openjdk:17-jdk-slim

# Set the working directory to /app
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/swagger-generator-0.0.1-SNAPSHOT.jar swagger-generator.jar

# Expose the port the application will run on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "swagger-generator.jar"]
