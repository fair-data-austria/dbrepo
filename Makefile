REGISTRY=docker.ossdip.at

all:

config-backend:
	./.rhel-prod/install_cert

config-registry:
	./.gitlab-ci/install_cert

config-docker:
	docker image pull postgres:13.4-alpine || true
	docker image pull mysql:8.0 || true
	docker image pull mariadb:10.5 || true
	docker image pull rabbitmq:3-alpine || true

config: config-backend config-docker

build-backend-metadata:
	mvn -f ./fda-metadata-db/pom.xml -q clean install > /dev/null

build-backend-authentication:
	mvn -f ./fda-authentication-service/pom.xml -q clean package -DskipTests > /dev/null

build-backend-citation:
	mvn -f ./fda-citation-service/pom.xml -q clean package -DskipTests > /dev/null

build-backend-container:
	mvn -f ./fda-container-service/pom.xml -q clean package -DskipTests > /dev/null

build-backend-database:
	mvn -f ./fda-database-service/pom.xml -q clean package -DskipTests > /dev/null

build-backend-discovery:
	mvn -f ./fda-discovery-service/pom.xml -q clean package -DskipTests > /dev/null

build-backend-gateway:
	mvn -f ./fda-gateway-service/pom.xml -q clean package -DskipTests > /dev/null

build-backend-query:
	mvn -f ./fda-query-service/pom.xml -q clean package -DskipTests > /dev/null

build-backend-table:
	mvn -f ./fda-table-service/pom.xml -q clean package -DskipTests > /dev/null

build-backend: build-backend-metadata build-backend-authentication build-backend-citation build-backend-container build-backend-database build-backend-discovery build-backend-gateway build-backend-query build-backend-table

build-docker: config-docker
	docker-compose build fda-metadata-db
	docker-compose build

build-frontend:
	npm --prefix ./fda-ui install
	npm --prefix ./fda-ui run build

build: clean build-backend build-frontend build-docker

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

test-frontend: build-frontend
	npm --prefix ./fda-ui install
	docker-compose up -d
	npm --prefix ./fda-ui run test

test: test-backend test-frontend

run-backend:
	docker-compose up -d fda-container-service fda-database-service fda-query-service fda-table-service fda-authentication-service

run-frontend:
	docker-compose up -d fda-ui

run: run-backend run-frontend

deploy-registry: config-registry
	docker-compose -f ./.gitlab-ci/docker-compose.yml up -d

registry-tag: config
	docker tag fda-metadata-db:latest ${REGISTRY}/fda-metadata-db
	docker tag fda-authentication-service:latest ${REGISTRY}/fda-authentication-service
	docker tag fda-broker-service:latest ${REGISTRY}/fda-broker-service
	docker tag fda-citation-service:latest ${REGISTRY}/fda-citation-service
	docker tag fda-container-service:latest ${REGISTRY}/fda-container-service
	docker tag fda-database-service:latest ${REGISTRY}/fda-database-service
	docker tag fda-discovery-service:latest ${REGISTRY}/fda-discovery-service
	docker tag fda-query-service:latest ${REGISTRY}/fda-query-service
	docker tag fda-table-service:latest ${REGISTRY}/fda-table-service

registry-push: registry-tag
	docker push ${REGISTRY}/fda-metadata-db
	docker push ${REGISTRY}/fda-authentication-service
	docker push ${REGISTRY}/fda-broker-service
	docker push ${REGISTRY}/fda-citation-service
	docker push ${REGISTRY}/fda-container-service
	docker push ${REGISTRY}/fda-database-service
	docker push ${REGISTRY}/fda-discovery-service
	docker push ${REGISTRY}/fda-query-service
	docker push ${REGISTRY}/fda-table-service

registry: registry-tag registry-push

logs:
	docker-compose logs

clean:
	docker-compose down || true
	docker container stop $(docker container ls -aq) || true
	docker container rm $(docker container ls -aq) || true
	docker volume rm $(docker volume ls -q) || true

deploy:
	echo 1
