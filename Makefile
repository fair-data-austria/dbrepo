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

config: config-backend

config-backend:
	sudo ./fda-authentication-service/rest-service/src/main/resources/bin/install_cert

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

run-backend:
	docker-compose up -d fda-container-service fda-database-service fda-query-service fda-table-service fda-authentication-service

logs:
	docker-compose logs

clean:
	docker-compose down || true
	docker volume rm $(docker volume ls -q) || true
	rm -f ./fda-authentication-service/rest-service/src/main/resources/ssl/dbrepo.p12 || true

test: test-backend test-frontend
