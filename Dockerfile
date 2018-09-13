FROM openjdk:8-jdk-alpine
LABEL maintainer="thomas.jaensch@noaa.gov"
VOLUME /tmp

# The application's war file
ARG JAR_FILE=build/libs/gcmd-0.0.1-SNAPSHOT.war

# Add the application's war to the container
ADD ${JAR_FILE} gcmd-0.0.1-SNAPSHOT.war

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/gcmd-0.0.1-SNAPSHOT.war"]