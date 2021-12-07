REGISTRY=docker.ossdip.at

all:

config-backend:
	./.rhel-prod/fda-authentication-service/install_cert

config-registry:
	./.rhel-registry/install_cert

config-frontend:
	./.rhel-prod/fda-ui/install_cert
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

build-docker: config-docker
	docker-compose build fda-metadata-db
	docker-compose build

build-docker-sandbox: config-docker
	docker-compose -f docker-compose.prod.yml build fda-metadata-db
	docker-compose -f docker-compose.prod.yml build

build-frontend:
	npm --prefix ./fda-ui clean-install
	npm --prefix ./fda-ui run build

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

test-frontend: clean build-frontend
	npm --prefix ./fda-ui install
	docker-compose up -d
	npm --prefix ./fda-ui run test

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

registry-stable-tag: config build test
	docker tag fda-metadata-db:latest ${REGISTRY}/fda-metadata-db
	docker tag fda-authentication-service:latest ${REGISTRY}/fda-authentication-service
	docker tag fda-broker-service:latest ${REGISTRY}/fda-broker-service
	docker tag fda-citation-service:latest ${REGISTRY}/fda-citation-service
	docker tag fda-container-service:latest ${REGISTRY}/fda-container-service
	docker tag fda-database-service:latest ${REGISTRY}/fda-database-service
	docker tag fda-discovery-service:latest ${REGISTRY}/fda-discovery-service
	docker tag fda-gateway-service:latest ${REGISTRY}/fda-gateway-service
	docker tag fda-query-service:latest ${REGISTRY}/fda-query-service
	docker tag fda-table-service:latest ${REGISTRY}/fda-table-service

registry-stable-push: registry-stable-tag registry-stable-tag
	docker push ${REGISTRY}/fda-metadata-db
	docker push ${REGISTRY}/fda-authentication-service
	docker push ${REGISTRY}/fda-broker-service
	docker push ${REGISTRY}/fda-citation-service
	docker push ${REGISTRY}/fda-container-service
	docker push ${REGISTRY}/fda-database-service
	docker push ${REGISTRY}/fda-discovery-service
	docker push ${REGISTRY}/fda-query-service
	docker push ${REGISTRY}/fda-table-service

registry-stable: registry-stable-tag registry-stable-push

registry-staging-tag: config build test
	docker tag fda-metadata-db:staging ${REGISTRY}/fda-metadata-db
	docker tag fda-authentication-service:staging ${REGISTRY}/fda-authentication-service
	docker tag fda-broker-service:staging ${REGISTRY}/fda-broker-service
	docker tag fda-citation-service:staging ${REGISTRY}/fda-citation-service
	docker tag fda-container-service:staging ${REGISTRY}/fda-container-service
	docker tag fda-database-service:staging ${REGISTRY}/fda-database-service
	docker tag fda-discovery-service:staging ${REGISTRY}/fda-discovery-service
	docker tag fda-gateway-service:staging ${REGISTRY}/fda-gateway-service
	docker tag fda-query-service:staging ${REGISTRY}/fda-query-service
	docker tag fda-table-service:staging ${REGISTRY}/fda-table-service

registry-staging-push: registry-staging-tag registry-staging-tag
	docker push ${REGISTRY}/fda-metadata-db
	docker push ${REGISTRY}/fda-authentication-service
	docker push ${REGISTRY}/fda-broker-service
	docker push ${REGISTRY}/fda-citation-service
	docker push ${REGISTRY}/fda-container-service
	docker push ${REGISTRY}/fda-database-service
	docker push ${REGISTRY}/fda-discovery-service
	docker push ${REGISTRY}/fda-query-service
	docker push ${REGISTRY}/fda-table-service

registry-staging: registry-staging-tag registry-staging-push

logs:
	docker-compose -f docker-compose.prod.yml logs

clean:
	docker-compose down
	docker volume rm fda-services_fda-metadata-db-data || true
	docker volume rm fda-public || true
	docker volume rm fda-userdb || true

teardown:
	./.rhel-prod/teardown

re-deploy: teardown deploy-staging

deploy-stable: registry-stable
	./.gitlab-ci/deploy

deploy-staging: registry-staging
	./.gitlab-ci/deploy
