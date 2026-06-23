# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory to /app
WORKDIR /app

# Copy all the current directory contents into the container at /app
COPY . /app

# Compile the Java code
# The classpath (-cp) includes the current directory and all .jar files
RUN javac -cp ".:*" frontend/Main.java backend/*.java

# Render uses the PORT environment variable to assign a port to the web service.
# We've already updated WebServer.java to read this variable.
EXPOSE 8080

# Run the application when the container launches
CMD ["java", "-cp", ".:*", "frontend.Main"]
