###### FIRST STAGE ######
FROM fda-metadata-db:latest as dependency
MAINTAINER Martin Weise <martin.weise@tuwien.ac.at>

###### SECOND STAGE ######
FROM maven:slim as build

COPY ./pom.xml ./

RUN mvn -fn -B dependency:go-offline > /dev/null

RUN keytool -genkeypair -keyalg RSA -alias 'dbrepo' \
    -dname "cn=FAIR Data Austria, ou=Zentrum für Forschungsdaten, o=TU Wien, c=AT, l=Vienna, st=Austria" \
    -storetype PKCS12 -keystore /dbrepo.p12 -storepass 'dbrepo' -validity 3650 -keysize 4096

COPY --from=dependency /root/.m2/repository/at/tuwien /root/.m2/repository/at/tuwien

COPY ./rest-service ./rest-service
COPY ./services ./services
COPY ./report ./report

RUN mvn -q clean package -DskipTests

###### THIRD STAGE ######
FROM openjdk:11-jre-slim as runtime

COPY ./service_ready /usr/bin
RUN chmod +x /usr/bin/service_ready

HEALTHCHECK --interval=25s --timeout=3s --retries=2 CMD service_ready

COPY --from=build ./rest-service/target/rest-service-*.jar ./rest-service.jar
COPY --from=build /dbrepo.p12 /dbrepo.p12

EXPOSE 9091

ENTRYPOINT ["java", "-jar", "./rest-service.jar"]
