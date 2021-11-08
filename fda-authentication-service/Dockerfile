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

ARG CI_JOB_STAGE

# Make sure it compiles
RUN if [ -z "$CI_JOB_STAGE" ]; then mvn -q clean package -DskipTests > /dev/null; fi
RUN if [ ! -z "$CI_JOB_STAGE" ]; then mvn clean package -DskipTests; fi # only in CI/CD pipeline

###### THIRD STAGE ######
FROM openjdk:11-jre-slim as runtime

VOLUME /mnt

COPY --from=build ./rest-service/target/rest-service-*.jar ./rest-service.jar
COPY ./service_ready /usr/bin

HEALTHCHECK --interval=10s --timeout=3s --retries=6 CMD service_ready

EXPOSE 9091

ENTRYPOINT ["java", "-jar", "./rest-service.jar"]
