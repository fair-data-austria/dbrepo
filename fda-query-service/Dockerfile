###### FIRST STAGE ######
FROM maven:slim as build
MAINTAINER Martin Weise <martin.weise@tuwien.ac.at>

COPY ./pom.xml ./

RUN mvn -fn -B dependency:go-offline > /dev/null

COPY ./api ./api
COPY ./gateways ./gateways
COPY ./persistence ./persistence
COPY ./rest-at.tuwien.service ./rest-at.tuwien.service
COPY ./services ./services

RUN mvn -q clean package > /dev/null

###### SECOND STAGE ######
FROM openjdk:11-jre-slim as runtime

COPY --from=build ./rest-at.tuwien.service/target/fda-query-at.tuwien.service-*.jar ./rest.jar

EXPOSE 9093

ENTRYPOINT ["java", "-jar", "./rest.jar"]
