[![pipeline status](https://gitlab.phaidra.org/fair-data-austria-db-repository/fda-services/badges/master/pipeline.svg)](https://gitlab.phaidra.org/fair-data-austria-db-repository/fda-services/-/commits/master)
[![coverage report](https://gitlab.phaidra.org/fair-data-austria-db-repository/fda-services/badges/master/coverage.svg)](https://gitlab.phaidra.org/fair-data-austria-db-repository/fda-services/-/commits/master)
[![license](.gitlab/license.svg)](https://opensource.org/licenses/Apache-2.0)

# FAIR Data Austria Database Repository

## Build

Local development minimum requirements:

- Ubuntu 18.04 LTS (Rocky Linux is also supported)
- Apache Maven 3.0.0
- OpenJDK 11.0.0
- Docker Engine 20.10.0
- Docker Compose 1.28.0

Everything is handled by compose, just build it by running:

```bash
docker-compose build
```

## Run

To use the citation service you need to provide a
[Zenodo API token](https://zenodo.org/account/settings/applications/tokens/new/). Create a `.env` file at the project
root. A sample file is available at `.env.example`

```bash
ZENODO_API_KEY=
API=http://fda-gateway-service:9095
```

Add to your `/etc/hosts` for executing the tests:

```bash
172.29.0.6      fda-gateway-service
```

## Development

The backend endpoints are accessible in the browser:

- [Image Endpoint](http://localhost:9091/swagger-ui/)
- [Container Endpoint](http://localhost:9091/swagger-ui/)
- [Database Endpoint](http://localhost:9092/swagger-ui/)
- [Query Endpoint](http://localhost:9093/swagger-ui/)
- [Table Endpoint](http://localhost:9094/swagger-ui/)

The frontend is accessible in the browser:

- [FAIR Portal](http://localhost:3000)
- [Query Endpoint Management Portal](http://localhost:15672) (username=guest, password=guest)

Other:

- [Discovery Endpoint](http://localhost:9090/) (Eureka)
- [Gateway Endpoint](http://localhost:9095/swagger-ui/) (Webflux)

Hosts:

```bash
# FDA PUBLIC
172.29.0.2      fda-gateway-service
172.29.0.3      fda-broker-service
172.29.0.4      fda-discovery-service
172.29.0.5      fda-metadata-db
172.29.0.6      fda-search-service
172.29.0.7      fda-units-service
172.29.0.8      fda-container-service
172.29.0.9      fda-database-service
172.29.0.10     fda-analyse-service
172.29.0.11     fda-table-service
172.29.0.12     fda-query-service

```

## Contribute

Contributions are always welcome and encouraged, simply fork the repository and
contact [Andreas Rauber](http://www.ifs.tuwien.ac.at/~andi/).

# License

This work is licensed under
a [Creative Commons Attribution 4.0 International License](http://creativecommons.org/licenses/by/4.0/)