###### FIRST STAGE ######
FROM maven:slim as build
MAINTAINER Martin Weise <martin.weise@tuwien.ac.at>

COPY ./api ./api
COPY ./gateways ./gateways
COPY ./persistence ./persistence
COPY ./rest-service ./rest-service
COPY ./services ./services
COPY ./pom.xml ./

RUN mvn -fn -B dependency:go-offline > /dev/null

RUN mvn -q clean package > /dev/null

###### SECOND STAGE ######
FROM openjdk:11-jre-slim as runtime

COPY --from=build ./rest-service/target/fda-query-service-*.jar ./rest.jar

EXPOSE 9093

ENTRYPOINT ["java", "-jar", "./rest.jar"]
