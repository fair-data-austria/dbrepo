# FAIR Data Austria Services

## Install

Pull the latest dev/master images on your client through:

```bash
docker login https://docker.martinweise.at
> Username: fda
> Password: fda-docker
```

```bash
docker pull docker.martinweise.at/fda-analyse-service
docker pull docker.martinweise.at/fda-discovery-server
docker pull docker.martinweise.at/fda-gateway-service
docker pull docker.martinweise.at/fda-database-managing-service
docker pull docker.martinweise.at/fda-container-managing-service
docker pull docker.martinweise.at/fda-query-service
docker pull docker.martinweise.at/fda-table-service
docker pull docker.martinweise.at/fda-ui
```

## Build

Everything is handled by compose, just build it by running:

```bash
docker-compose build
```

## Develop

The endpoints are documented with Swagger 2.1 and OpenAPI 3.0. The current specification for the front-end is obtainable programatically from `http://localhost:<port>/swagger-resources`

For easy visualization use OpenAPI at:

- [http://fda-container-managing-service/swagger-ui/](http://localhost:9091/swagger-ui/)
- [http://fda-database-managing-service/swagger-ui/](http://localhost:9092/swagger-ui/)

## Deployment

The pipeline is set-up to build and test all commits. A commit to dev or master branch triggers additional jobs.

A commit to `dev` triggers the following pipeline. It deploys the docker images to the docker registry hosted on the fda-runner server and deploys it also to a test server (fda-deployment) at TU Wien. 

![pipeline dev](https://gitlab.phaidra.org/fair-data-austria-db-repository/fda-docs/-/raw/master/figures/fda-pipeline-dev.png)

### Production

A commit to `master` triggers the following pipeline. It deploys the docker images to the docker registry hosted on the fda-runner server and deploys it also to a production server tbd.

![pipeline master](https://gitlab.phaidra.org/fair-data-austria-db-repository/fda-docs/-/raw/master/figures/fda-pipeline-prod.png)


## Start

Now start all services by running:

```bash
docker-compose up
```

### Troubleshooting

##### FDA Runner

Hosted at TU Wien 128.130.202.89, only accessible from TU-Network

**Important**

Different MTU for HPC Cluster, edit for Docker to work with bridge mode the `/etc/docker/daemon.json`:

```bash
{
    "mtu": 1450
}
```

##### Virtual Machine

Ubuntu 20.04 LTS

Use openjdk-11...openjdk-8 does not work.

Do not use maven provided my Ubuntu 20.4 LTS. It cannot handle those injections spring-boot wants it to do.

It says: WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by com.google.inject.internal.cglib.core.$ReflectUtils$1 (file:/usr/share/maven/lib/guice.jar) to method java.lang.ClassLoader.defineClass(java.lang.String,byte[],int,int,java.security.ProtectionDomain)

Install maven from Apache Org.:

Download maven e.g. 3.6.3

```bash
wget https://www-us.apache.org/dist/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz -P /tmp
```

Untar downloaded file to /opt

```bash
sudo tar xf /tmp/apache-maven-*.tar.gz -C /opt
```

Install the alternative version for the mvn in your system

```bash
sudo update-alternatives --install /usr/bin/mvn mvn /opt/apache-maven-3.6.3/bin/mvn 363
```

Check if your configuration is ok. You may use your current or the 3.6.3 whenever you wish, running the command below.

```bash
sudo update-alternatives --config mvn
```
