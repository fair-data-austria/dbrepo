###### FIRST STAGE ######
FROM maven:slim as build
MAINTAINER Martin Weise <martin.weise@tuwien.ac.at>

COPY ./pom.xml ./

RUN mvn -fn -B dependency:go-offline > /dev/null

COPY ./src ./src

# Make sure it compiles
RUN mvn -q clean package -DskipTests > /dev/null

###### SECOND STAGE ######
FROM openjdk:11-jre-slim as runtime

COPY ./service_ready /usr/bin
RUN chmod +x /usr/bin/service_ready

HEALTHCHECK --interval=10s --timeout=3s --retries=6 CMD service_ready

COPY --from=build ./target/fda-discovery-service-*.jar ./discovery.jar

ENTRYPOINT ["java", "-jar", "./discovery.jar"]