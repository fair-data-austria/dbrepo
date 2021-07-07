###### FIRST STAGE ######
FROM maven:slim as build
MAINTAINER Cornelia Michlits <cornelia.michlits@tuwien.ac.at>

COPY ./pom.xml ./

RUN mvn -fn -B dependency:go-offline > /dev/null

COPY ./api ./api
COPY ./entities ./entities

# Make sure it compiles
RUN mvn clean package -DskipTests > /dev/null

# Install to local repository
RUN mvn clean install

###### SECOND STAGE ######
FROM postgres:latest as runtime

# Scripts are copied to /docker-entrypoint-initdb.d/ in docker-compose from analyze service

COPY ./initi.sh /docker-entrypoint-initdb.d/
RUN chmod +x /docker-entrypoint-initdb.d/initi.sh

COPY --from=build /root/.m2/repository/at/tuwien /root/.m2/repository/at/tuwien