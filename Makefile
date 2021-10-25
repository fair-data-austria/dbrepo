all:

build-backend-maven:
	mvn -f ./fda-metadata-db/pom.xml clean install
	mvn -f ./fda-authentication-service/pom.xml clean package -DskipTests
	mvn -f ./fda-broker-service/pom.xml clean package -DskipTests
	mvn -f ./fda-citation-service/pom.xml clean package -DskipTests
	mvn -f ./fda-container-service/pom.xml clean package -DskipTests
	mvn -f ./fda-database-service/pom.xml clean package -DskipTests
	mvn -f ./fda-discovery-service/pom.xml clean package -DskipTests
	mvn -f ./fda-gateway-service/pom.xml clean package -DskipTests
	mvn -f ./fda-query-service/pom.xml clean package -DskipTests
	mvn -f ./fda-table-service/pom.xml clean package -DskipTests

build-frontend-npm:
	npm --prefix ./fda-ui install
	npm --prefix ./fda-ui run build

build:
	docker-compose build fda-metadata-db
	docker-compose build --parallel

test-backend:
	mvn -f ./fda-authentication-service/pom.xml clean test verify
	mvn -f ./fda-broker-service/pom.xml clean test verify
	mvn -f ./fda-citation-service/pom.xml clean test verify
	mvn -f ./fda-container-service/pom.xml clean test verify
	mvn -f ./fda-database-service/pom.xml clean test verify
	mvn -f ./fda-discovery-service/pom.xml clean test verify
	mvn -f ./fda-gateway-service/pom.xml clean test verify
	mvn -f ./fda-query-service/pom.xml clean test verify
	mvn -f ./fda-table-service/pom.xml clean test verify

test-frontend: build
	docker-compose up -d
	npm --prefix ./fda-ui run test

test: test-backend test-frontend

install-keystore:
	cd ./fda-authentication-service && sudo ./rest-service/src/main/resources/bin/install-keystore

install-cert:
	cd ./fda-authentication-service && sudo ./rest-service/src/main/resources/bin/install-cert
