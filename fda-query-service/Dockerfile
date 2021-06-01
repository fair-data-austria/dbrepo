###### FIRST STAGE ######
FROM fda-metadata-db:latest as dependency
MAINTAINER Martin Weise <martin.weise@tuwien.ac.at>

###### SECOND STAGE ######
FROM maven:slim as build

COPY ./pom.xml ./

RUN mvn -fn -B dependency:go-offline > /dev/null

COPY --from=dependency /root/.m2/repository/at/tuwien /root/.m2/repository/at/tuwien

COPY ./rest-service ./rest-service
COPY ./services ./services
COPY ./report ./report

RUN mvn -q clean package -DskipTests

###### THIRD STAGE ######
FROM openjdk:11-jre-slim as runtime

COPY --from=build ./rest-service/target/rest-service-*.jar ./rest-service.jar

EXPOSE 9093

ENTRYPOINT ["java", "-jar", "./rest-service.jar"]
