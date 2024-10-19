# Use the slim version of OpenJDK 21 for running the application
FROM openjdk:21-slim

# Set the working directory
WORKDIR /app

# Copy the pre-built JAR file into the container
COPY target/*.jar app.jar

# Entry point for running the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]