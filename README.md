# GCMD Keyword Checker Web API for ISO Metadata XML URLs*
\* API depends on https://gcmdservices.gsfc.nasa.gov/static/kms/ so if that site is down the service is not working either

# Installation

## Requirements
Working Java 8 plus Gradle installation

## Build Executable .war File
CD into project folder and run `gradle build` which will put an executable .war file into the /build/libs folder of the project if the build succeeds

## Run Program Locally
In project folder, run `java -jar ./build/libs/gcmd-0.0.2-SNAPSHOT.war`

## Access API
Make sure to build the program and run it, then pull up e.g. `http://localhost:5000/gcmd_keywords?url=https://www1.ncdc.noaa.gov/pub/data/metadata/published/geoportal/iso/xml/C00630.xml` to get a summary assessment of that file's GCMD keywords in JSON format. 

# OR

## Run application in a Docker container (Requirement: working Docker installation)
1. Build .war file as described above
2. In project folder, run `docker build -t gcmd_keywords .` to create Docker image
3. Run `docker run -p 5000:5000 gcmd_keywords` to run container in foreground or `docker run -d -p 5000:5000 gcmd_keywords` to run in detached mode in background
4. Access running container at `http://localhost:5000/gcmd_keywords?url=https://www1.ncdc.noaa.gov/pub/data/metadata/published/geoportal/iso/xml/C00630.xml` (with same sample XML file URL as above)
