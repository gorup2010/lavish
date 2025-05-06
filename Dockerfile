# Use a base image with JDK and Maven installed
FROM eclipse-temurin:21-jdk-focal AS builder
# Set the working directory in the container
WORKDIR /app
# Copy the pom.xml file to the container
COPY mvnw pom.xml ./
# Copy the Maven configuration directory (.mvn/) from the host machine to the /app/.mvn directory inside the Docker container
COPY .mvn/ .mvn

RUN chmod +x ./mvnw

# Download project dependencies and store them locally in the Docker container
RUN ./mvnw dependency:resolve

# Copy the source code of the application from the host machine to the /app/src directory inside the Docker container
COPY ./src ./src

    
RUN ./mvnw clean install
# Create a new image containing only the built application
FROM eclipse-temurin:17-jdk-focal
# Set the working directory in the container
WORKDIR /app
# Copy the built JAR file from the builder stage to the current directory in the container
COPY --from=builder /app/target/*.jar app/*.jar
# Expose the port the application runs on
EXPOSE 8080
# Command to run the application
CMD ["java", "-jar", "app/*.jar"]