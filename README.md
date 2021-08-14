[![pipeline status](https://gitlab.phaidra.org/fair-data-austria-db-repository/fda-services/badges/master/pipeline.svg)](https://gitlab.phaidra.org/fair-data-austria-db-repository/fda-services/-/commits/master) [![coverage report](https://gitlab.phaidra.org/fair-data-austria-db-repository/fda-services/badges/master/coverage.svg)](https://gitlab.phaidra.org/fair-data-austria-db-repository/fda-services/-/commits/master)

# FAIR Data Austria Services

## Install

Pull the latest dev/master images on your client through:

```bash
docker login https://docker.martinweise.at
> Username: fda
> Password: fda-docker
```

The `dev` branch images have suffix `:latest` (=optional), to pull them execute the following

```bash
docker pull docker.martinweise.at/fda-analyse-service:latest
docker pull docker.martinweise.at/fda-discovery-server:latest
docker pull docker.martinweise.at/fda-gateway-service:latest
docker pull docker.martinweise.at/fda-database-managing-service:latest
docker pull docker.martinweise.at/fda-container-managing-service:latest
docker pull docker.martinweise.at/fda-query-service:latest
docker pull docker.martinweise.at/fda-table-service:latest
docker pull docker.martinweise.at/fda-ui:latest
```

The `master` branch images have suffix `:stable`, they are pulled similar:

```bash
docker pull docker.martinweise.at/fda-analyse-service:stable
...
```

Note: the domain martinweise.at is private and I do not own any of these images, it is just a necessary condition for Docker to pull from a (private, non-public) remote repository! The domain should of course be changed before release!

## Build

Everything is handled by compose, just build it by running:

```bash
docker-compose build
```
Local development minimum requirements:

- Ubuntu 18.04 LTS
- Apache Maven 3.0.0
- OpenJDK 11.0.0

Local deployment minimum versions:

- Docker Engine 20.10.0
- Docker Compose 1.28.0

## Deployment

The pipeline is set-up to build and test all commits. A commit to dev or master branch triggers additional jobs.

### Development

A commit to `dev` triggers the following pipeline. It deploys the docker images to the docker registry hosted on the fda-runner server and deploys it also to a test server (fda-deployment) at TU Wien. 

![pipeline dev](https://gitlab.phaidra.org/fair-data-austria-db-repository/fda-docs/-/raw/master/figures/fda-pipeline-dev.png)

### Production

A commit to `master` triggers the following pipeline. It deploys the docker images to the docker registry hosted on the fda-runner server and deploys it also to a production server tbd.

![pipeline master](https://gitlab.phaidra.org/fair-data-austria-db-repository/fda-docs/-/raw/master/figures/fda-pipeline-prod.png)
