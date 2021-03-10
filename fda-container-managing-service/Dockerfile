###### FIRST STAGE ######
FROM maven:slim as build
MAINTAINER Martin Weise <martin.weise@tuwien.ac.at>

COPY ./pom.xml ./

RUN mvn -fn -B dependency:go-offline > /dev/null

COPY api/src/main/java/api ./api
COPY src/main/java/rest ./rest-service
COPY src/main/java/services ./services

RUN mvn -q clean package > /dev/null

###### SECOND STAGE ######
FROM openjdk:11-jre-slim as runtime

COPY --from=build src/main/java/rest/target/fda-container-managing-service-*.jar ./rest.jar

EXPOSE 9091

ENTRYPOINT ["java", "-jar", "./rest.jar"]
