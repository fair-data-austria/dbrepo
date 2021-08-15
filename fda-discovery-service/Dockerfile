###### FIRST STAGE ######
FROM maven:slim as build
MAINTAINER Martin Weise <martin.weise@tuwien.ac.at>

COPY ./pom.xml ./

RUN mvn -fn -B dependency:go-offline > /dev/null

COPY ./src ./src

RUN mvn -q clean package -DskipTests

###### SECOND STAGE ######
FROM openjdk:11-jre-slim as runtime

COPY ./service_ready /usr/bin
RUN chmod +x /usr/bin/service_ready

COPY --from=build ./target/fda-discovery-service-*.jar ./discovery.jar

ENTRYPOINT ["java", "-jar", "./discovery.jar"]