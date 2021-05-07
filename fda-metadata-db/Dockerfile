###### FIRST STAGE ######
FROM maven:slim as build
MAINTAINER Cornelia Michlits <cornelia.michlits@tuwien.ac.at>

COPY ./pom.xml ./

RUN mvn -fn -B dependency:go-offline > /dev/null

COPY ./api ./api
COPY ./entities ./entities

# Make sure it compiles
RUN mvn clean package -DskipTests

# Install to local repository
RUN mvn clean install

###### SECOND STAGE ######
FROM postgres:latest as runtime

COPY ./create_mdb.py /docker-entrypoint-initdb.d/

COPY --from=build /root/.m2/repository/at/tuwien /root/.m2/repository/at/tuwien