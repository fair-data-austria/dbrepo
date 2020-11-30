FROM adoptopenjdk/openjdk11:jdk-11.0.9_11-alpine
MAINTAINER Gökhan Dasdemir
ARG JAR_FILE=/target/fda-gateway-service-*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 9095