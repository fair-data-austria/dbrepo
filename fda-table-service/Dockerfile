###### FIRST STAGE ######
FROM maven:slim as build
MAINTAINER Martin Weise <martin.weise@tuwien.ac.at>

COPY ./api ./api
COPY ./gateways ./gateways
COPY ./rest-service ./rest-service
COPY ./services ./services
COPY ./pom.xml ./

ENV SPRING_MAIN_BANNER-MODE=off

RUN mvn -fn -B dependency:go-offline > /dev/null

RUN mvn -q clean package > /dev/null

###### SECOND STAGE ######
FROM openjdk:11-jre-slim as runtime

COPY --from=build ./rest-service/target/fda-table-service-*.jar ./rest.jar

EXPOSE 9094

ENTRYPOINT ["java", "-jar", "./rest.jar"]
