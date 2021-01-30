# FAIR Data Austria Services

### Build

Everything is handled by compose, just build it by running:

```bash
docker-compose build
```

### Start

Now start all services by running:

```bash
docker-compose up fda-discovery-server fda-gateway-service fda-database-managing-service fda-container-managing-service fda-query-service fda-table-service fda-analyse-service
```

Optionally, start the user interface by running:

```bash
docker-compose up fda-ui
```