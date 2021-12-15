all:

config-backend:
	./.fda-deployment/fda-authentication-service/install_cert

config-registry:
	./.fda-builder/registry/install_cert

config-frontend:
	./.fda-deployment/fda-ui/install_cert
	docker-compose -f docker-compose.prod.yml config

config-docker:
	docker image pull -q postgres:13.4-alpine || true > /dev/null
	docker image pull -q mysql:8.0 || true > /dev/null
	docker image pull -q mariadb:10.5 || true > /dev/null
	docker image pull -q rabbitmq:3-alpine || true > /dev/null

config: config-backend config-docker config-frontend

build-backend-metadata:
	mvn -f ./fda-metadata-db/pom.xml clean install

build-backend-authentication:
	mvn -f ./fda-authentication-service/pom.xml clean package -DskipTests

build-backend-citation:
	mvn -f ./fda-citation-service/pom.xml clean package -DskipTests

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

build-backend: build-backend-metadata build-backend-authentication build-backend-citation build-backend-container build-backend-database build-backend-discovery build-backend-gateway build-backend-query build-backend-table

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

deploy-registry: config-registry
	docker-compose -f ./.rhel-registry/docker-compose.yml up -d

registry-stable-tag: config-registry
	docker tag fda-metadata-db:latest ${REGISTRY}/fda-metadata-db:${VERSION}
	docker tag fda-authentication-service:latest ${REGISTRY}/fda-authentication-service:${VERSION}
	docker tag fda-broker-service:latest ${REGISTRY}/fda-broker-service:${VERSION}
	docker tag fda-container-service:latest ${REGISTRY}/fda-container-service:${VERSION}
	docker tag fda-database-service:latest ${REGISTRY}/fda-database-service:${VERSION}
	docker tag fda-discovery-service:latest ${REGISTRY}/fda-discovery-service:${VERSION}
	docker tag fda-gateway-service:latest ${REGISTRY}/fda-gateway-service:${VERSION}
	docker tag fda-query-service:latest ${REGISTRY}/fda-query-service:${VERSION}
	docker tag fda-table-service:latest ${REGISTRY}/fda-table-service:${VERSION}

registry-stable-push: registry-stable-tag registry-stable-tag
	docker push ${REGISTRY}/fda-metadata-db:${VERSION}
	docker push ${REGISTRY}/fda-authentication-service:${VERSION}
	docker push ${REGISTRY}/fda-broker-service:${VERSION}
	docker push ${REGISTRY}/fda-container-service:${VERSION}
	docker push ${REGISTRY}/fda-database-service:${VERSION}
	docker push ${REGISTRY}/fda-discovery-service:${VERSION}
	docker push ${REGISTRY}/fda-query-service:${VERSION}
	docker push ${REGISTRY}/fda-table-service:${VERSION}

registry-stable: registry-stable-tag registry-stable-push

registry-staging-tag: config-registry
	docker tag fda-metadata-db:latest ${REGISTRY}/fda-metadata-db:latest
	docker tag fda-authentication-service:latest ${REGISTRY}/fda-authentication-service:latest
	docker tag fda-broker-service:latest ${REGISTRY}/fda-broker-service:latest
	docker tag fda-container-service:latest ${REGISTRY}/fda-container-service:latest
	docker tag fda-database-service:latest ${REGISTRY}/fda-database-service:latest
	docker tag fda-discovery-service:latest ${REGISTRY}/fda-discovery-service:latest
	docker tag fda-gateway-service:latest ${REGISTRY}/fda-gateway-service:latest
	docker tag fda-query-service:latest ${REGISTRY}/fda-query-service:latest
	docker tag fda-table-service:latest ${REGISTRY}/fda-table-service:latest

registry-staging-push: registry-staging-tag
	docker push ${REGISTRY}/fda-metadata-db:latest
	docker push ${REGISTRY}/fda-authentication-service:latest
	docker push ${REGISTRY}/fda-broker-service:latest
	docker push ${REGISTRY}/fda-container-service:latest
	docker push ${REGISTRY}/fda-database-service:latest
	docker push ${REGISTRY}/fda-discovery-service:latest
	docker push ${REGISTRY}/fda-query-service:latest
	docker push ${REGISTRY}/fda-table-service:latest

registry-staging: registry-staging-tag registry-staging-push

logs:
	docker-compose -f docker-compose.prod.yml logs

clean:
	docker-compose down || true
	docker volume rm fda-services_fda-metadata-db-data || true
	docker volume rm fda-public || true
	docker volume rm fda-userdb || true
	rm -f ./fda-ui/videos/*.webm

teardown:
	./.rhel-prod/teardown

re-deploy: teardown deploy-staging

deploy-stable: registry-stable
	./.gitlab-ci/deploy

deploy-staging: registry-staging
	./.gitlab-ci/deploy
