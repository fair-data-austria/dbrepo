###### FIRST STAGE ######
FROM maven:slim as build
MAINTAINER Martin Weise <martin.weise@tuwien.ac.at>

COPY ./pom.xml ./

RUN mvn -fn -B dependency:go-offline > /dev/null

COPY ./api ./api
COPY ./rest-service ./rest-service
COPY ./services ./services

RUN mvn -q clean package -DskipTests > /dev/null

###### SECOND STAGE ######
FROM openjdk:11-jre-slim as runtime

COPY --from=build ./fda-container-managing-service-*.jar ./rest.jar

EXPOSE 9091

ENTRYPOINT ["java", "-jar", "./rest.jar"]
