# fda-services (java)

## Build Setup

```bash
# First generate jars for discovery,database,container and query services
$ mvn clean package

# start application
$ docker-compose up --build

# stop application
$ docker-compose down
```