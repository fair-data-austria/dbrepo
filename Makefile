all:

config-backend:
	./.fda-deployment/fda-authentication-service/install_cert

config-frontend:
	./.fda-deployment/fda-ui/install_cert
	docker-compose -f docker-compose.prod.yml config

config-docker:
	docker image pull -q postgres:13.4-alpine || true > /dev/null
	docker image pull -q mysql:8.0 || true > /dev/null
	docker image pull -q mariadb:10.5 || true > /dev/null
	docker image pull -q rabbitmq:3-alpine || true > /dev/null
	docker image pull -q nginx:1.20-alpine || true > /dev/null

config: config-backend config-docker config-frontend

build-backend-metadata:
	mvn -f ./fda-metadata-db/pom.xml clean install

build-backend-authentication:
	mvn -f ./fda-authentication-service/pom.xml clean package -DskipTests

build-backend-identifier:
	mvn -f ./fda-identifier-service/pom.xml clean package -DskipTests

build-backend-container:
	mvn -f ./fda-container-service/pom.xml clean package -DskipTests

build-backend-database:
	mvn -f ./fda-database-service/pom.xml clean package -DskipTests

build-backend-discovery:
	mvn -f ./fda-discovery-service/pom.xml clean package -DskipTests

build-backend-gateway:
	mvn -f ./fda-gateway-service/pom.xml clean package -DskipTests

build-backend-query:
	mvn -f ./fda-query-service/pom.xml clean package -DskipTests

build-backend-table:
	mvn -f ./fda-table-service/pom.xml clean package -DskipTests

build-backend: build-backend-metadata build-backend-authentication build-backend-container build-backend-database build-backend-discovery build-backend-gateway build-backend-query build-backend-table

build-docker:
	docker-compose build fda-metadata-db
	docker-compose build

build-docker-sandbox:
	docker-compose -f docker-compose.prod.yml build fda-metadata-db
	docker-compose -f docker-compose.prod.yml build

build-frontend:
	yarn --cwd ./fda-ui install --legacy-peer-deps
	yarn --cwd ./fda-ui run build

build: clean build-backend build-frontend build-docker

build-sandbox: clean build-backend build-frontend build-docker-sandbox

doc: doc-identifier doc-container doc-database doc-discovery doc-gateway doc-query doc-table

doc-identifier:
	mvn -f ./fda-identifier-service/pom.xml clean install site -DskipTests

doc-container:
	mvn -f ./fda-container-service/pom.xml clean install site -DskipTests

doc-database:
	mvn -f ./fda-database-service/pom.xml clean install site -DskipTests

doc-discovery:
	mvn -f ./fda-discovery-service/pom.xml clean install site -DskipTests

doc-gateway:
	mvn -f ./fda-gateway-service/pom.xml clean install site -DskipTests

doc-query:
	mvn -f ./fda-query-service/pom.xml clean install site -DskipTests

doc-table:
	mvn -f ./fda-table-service/pom.xml clean install site -DskipTests

test-backend: test-backend-auth test-backend-container test-backend-database test-backend-discovery test-backend-gateway test-backend-query test-backend-table

test-backend-auth:
	mvn -f ./fda-authentication-service/pom.xml clean test verify

test-backend-identifier: config-docker
	mvn -f ./fda-identifier-service/pom.xml clean test verify

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

coverage-frontend: clean build-frontend
	yarn --cwd ./fda-ui run coverage || true

test-frontend: clean build-frontend
	yarn --cwd ./fda-ui install
	docker-compose up -d
	yarn --cwd ./fda-ui run test

test: test-backend test-frontend

run-backend:
	docker-compose up -d fda-container-service fda-database-service fda-query-service fda-table-service fda-authentication-service

run-frontend:
	docker-compose up -d fda-ui

run:
	docker-compose up -d

run-sandbox: config-frontend
	docker-compose -f docker-compose.prod.yml up -d

logs:
	docker-compose -f docker-compose.prod.yml logs

seed:
	./.fda-deployment/seed

clean-ide:
	rm -rf .idea/
	rm -rf ./fda-authentication-service/.idea/
	rm -rf ./fda-identifier-service/.idea/
	rm -rf ./fda-container-service/.idea/
	rm -rf ./fda-database-service/.idea/
	rm -rf ./fda-discovery-service/.idea/
	rm -rf ./fda-gateway-service/.idea/
	rm -rf ./fda-query-service/.idea/
	rm -rf ./fda-table-service/.idea/

clean-frontend:
	rm -f ./fda-ui/videos/*.webm

clean-docker:
	./.fda-deployment/clean

clean: clean-ide clean-frontend clean-docker

teardown:
	./.fda-deployment/teardown
