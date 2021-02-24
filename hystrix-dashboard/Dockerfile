###### FIRST STAGE ######
FROM maven:slim as build
MAINTAINER Martin Weise <martin.weise@tuwien.ac.at>

COPY ./src ./src
COPY ./pom.xml ./

RUN mvn -fn -B dependency:go-offline > /dev/null

RUN mvn -q clean package > /dev/null

###### SECOND STAGE ######
FROM openjdk:11-jre-slim as runtime

COPY --from=build ./rest-service/target/hystrix-dashboard-*.jar ./hystrix.jar

ENTRYPOINT ["java", "-jar", "./hystrix.jar"]
