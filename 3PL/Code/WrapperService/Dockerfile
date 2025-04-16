#FROM openjdk:17
#VOLUME /tmp
#COPY target/*.jar almailem.jar
#ENTRYPOINT ["java","-jar","/almailem.jar"]
FROM openjdk:11-jre-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file built by Gradle into the container
COPY build/libs/*.jar app.jar

# Command to run when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]

