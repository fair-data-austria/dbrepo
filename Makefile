REGISTRY=docker.ossdip.at

all:

config-backend: clean-cert
	./fda-authentication-service/rest-service/src/main/resources/bin/install_cert

config-docker:
	docker image pull postgres:13.4-alpine || true
	docker image pull mysql:8.0 || true
	docker image pull mariadb:10.5 || true
	docker image pull rabbitmq:3-alpine || true

config: config-backend config-docker

build-backend-maven: config
	mvn -f ./fda-metadata-db/pom.xml clean install
	mvn -f ./fda-authentication-service/pom.xml clean package -DskipTests
	mvn -f ./fda-citation-service/pom.xml clean package -DskipTests
	mvn -f ./fda-container-service/pom.xml clean package -DskipTests
	mvn -f ./fda-database-service/pom.xml clean package -DskipTests
	mvn -f ./fda-discovery-service/pom.xml clean package -DskipTests
	mvn -f ./fda-gateway-service/pom.xml clean package -DskipTests
	mvn -f ./fda-query-service/pom.xml clean package -DskipTests
	mvn -f ./fda-table-service/pom.xml clean package -DskipTests

build-backend-docker: config
	docker-compose build fda-metadata-db
	docker-compose build

build-frontend-npm:
	npm --prefix ./fda-ui install
	npm --prefix ./fda-ui run build

build: clean build-backend-maven build-backend-docker

test-backend: test-backend-auth test-backend-citation test-backend-container test-backend-database test-backend-discovery test-backend-gateway test-backend-query test-backend-table

test-backend-auth:
	mvn -f ./fda-authentication-service/pom.xml clean test verify

test-backend-citation: config-docker
	mvn -f ./fda-citation-service/pom.xml clean test verify

test-backend-container: config-docker
	mvn -f ./fda-container-service/pom.xml clean test verify

test-backend-database: config-docker
	mvn -f ./fda-database-service/pom.xml clean test verify

test-backend-discovery:
	mvn -f ./fda-discovery-service/pom.xml clean test verify

test-backend-gateway:
	mvn -f ./fda-gateway-service/pom.xml clean test verify

test-backend-query: config-docker
	mvn -f ./fda-query-service/pom.xml clean test verify

test-backend-table: config-docker
	mvn -f ./fda-table-service/pom.xml clean test verify

test-frontend: build
	npm --prefix ./fda-ui install
	docker-compose up -d
	npm --prefix ./fda-ui run test

test: test-backend test-frontend

run-backend:
	docker-compose up -d fda-container-service fda-database-service fda-query-service fda-table-service fda-authentication-service

run-frontend:
	docker-compose up -d fda-ui

run: run-backend run-frontend

deploy-tag: config build-backend-docker
	docker tag fda-metadata-db:latest ${REGISTRY}/fda-metadata-db
	docker tag authentication-service:latest ${REGISTRY}/authentication-service
	docker tag broker-service:latest ${REGISTRY}/broker-service
	docker tag citation-service:latest ${REGISTRY}/citation-service
	docker tag container-service:latest ${REGISTRY}/container-service
	docker tag database-service:latest ${REGISTRY}/database-service
	docker tag discovery-service:latest ${REGISTRY}/discovery-service
	docker tag query-service:latest ${REGISTRY}/query-service
	docker tag table-service:latest ${REGISTRY}/table-service

deploy-push: deploy-tag
	docker push ${REGISTRY}/fda-metadata-db
	docker push ${REGISTRY}/authentication-service
	docker push ${REGISTRY}/broker-service
	docker push ${REGISTRY}/citation-service
	docker push ${REGISTRY}/container-service
	docker push ${REGISTRY}/database-service
	docker push ${REGISTRY}/discovery-service
	docker push ${REGISTRY}/query-service
	docker push ${REGISTRY}/table-service

deploy: deploy-tag deploy-push

logs:
	docker-compose logs

clean-cert:
	rm -f ./fda-authentication-service/rest-service/src/main/resources/ssl/cert.p12 ./fda-authentication-service/rest-service/src/main/resources/ssl/dbrepo.jks || true

clean: clean-cert
	docker-compose down || true
	docker container stop $(docker container ls -aq) || true
	docker container rm $(docker container ls -aq) || true
	docker volume rm $(docker volume ls -q) || true
