###### FIRST STAGE ######
FROM maven:slim as build
MAINTAINER Martin Weise <martin.weise@tuwien.ac.at>

COPY ./pom.xml ./

RUN mvn -fn -B dependency:go-offline > /dev/null

COPY ./gateway ./gateway
COPY ./report ./report

ARG CI_JOB_STAGE

# Make sure it compiles
RUN if [ -z "$CI_JOB_STAGE" ]; then mvn -q clean package -DskipTests > /dev/null; fi
RUN if [ ! -z "$CI_JOB_STAGE" ]; then mvn clean package -DskipTests; fi # only in CI/CD pipeline

###### SECOND STAGE ######
FROM openjdk:11-jre-slim as runtime

COPY ./service_ready /usr/bin
RUN chmod +x /usr/bin/service_ready

HEALTHCHECK --interval=10s --timeout=3s --retries=6 CMD service_ready

COPY --from=build ./gateway/target/gateway-*.jar ./gateway.jar

EXPOSE 9095

ENTRYPOINT ["java", "-jar", "./gateway.jar"]