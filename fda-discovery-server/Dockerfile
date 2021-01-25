###### FIRST STAGE ######
FROM maven:slim as build
MAINTAINER Martin Weise <martin.weise@tuwien.ac.at>

COPY ./src ./src
COPY ./pom.xml ./

ENV SPRING_MAIN_BANNER-MODE=off

RUN mvn -fn -B dependency:go-offline > /dev/null

# FIXME remove for production
RUN mvn -q clean package -DskipTests > /dev/null

###### SECOND STAGE ######
FROM openjdk:11-jre-slim as runtime

COPY --from=build ./target/fda-discovery-server-*.jar ./discovery.jar

ENTRYPOINT ["java", "-jar", "./discovery.jar"]