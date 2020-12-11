# fda-services (java)

## Build Setup

Create the database container image:

    cd fda-container-managing-service/rest-service/src/main/resources
    docker build --tag rdr-postgres:1.0 .

```bash
# First generate jars for discovery,gateway,database,container and query services
$ mvn clean install

# start application
$ docker-compose up --build #(use -d for detach mode)

# stop application
$ docker-compose down
```
