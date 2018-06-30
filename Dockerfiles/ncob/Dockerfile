# Start with a base image containing Java runtime
FROM openjdk:8-jdk #can maybe pull a different base image(like alpine) depending on reqs

# Add Maintainer Info
LABEL maintainer="Dennis Moore"

# Create ncob folder inside container
RUN mkdir -p /ncob/

# Add a volume pointing to /dennis/ncob/data
#VOLUME /dennis/ncob/data

# Make port 9124 available to the world outside this container
EXPOSE 9124

# The application's jar file
ARG JAR_FILE=../../build/libs/ncob-0.1.0.jar

# Add the application's jar to the container
ADD ${JAR_FILE} /ncob/ncob-0.1.0.jar

# Run the jar file 
CMD["java", "-jar", "/ncob/ncob-0.1.0.jar"]