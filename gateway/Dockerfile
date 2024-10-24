# Stage 1: Build the application with Maven
FROM maven:3.9.9-amazoncorretto-21 AS build

# Set the working directory
WORKDIR /app

# Copy the parent POM file and module-specific POM file
COPY ./pom.xml ./
COPY ./gateway/pom.xml ./gateway/pom.xml

# Download dependencies first (Docker will cache this step if no POM files change)
RUN mvn -f ./gateway/pom.xml dependency:go-offline

# Now copy the source code (adjust path)
COPY ./gateway/src ./gateway/src

# Build the application
RUN mvn -f gateway/pom.xml clean install -DskipTests

# Stage 2: Use the slim version of OpenJDK 21 for running the application
FROM openjdk:21-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/gateway/target/*.jar app.jar

# Entry point for running the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
