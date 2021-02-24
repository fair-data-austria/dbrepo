###### FIRST STAGE ######
FROM maven:slim as build
MAINTAINER Martin Weise <martin.weise@tuwien.ac.at>

COPY ./pom.xml ./

RUN mvn -fn -B dependency:go-offline > /dev/null

COPY ./src ./src

RUN mvn -q clean package > /dev/null

###### SECOND STAGE ######
FROM openjdk:11-jre-slim as runtime

COPY --from=build ./target/fda-gateway-service-*.jar ./gateway.jar

EXPOSE 9095

ENTRYPOINT ["java", "-jar", "./gateway.jar"]