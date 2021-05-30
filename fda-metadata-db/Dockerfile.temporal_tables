FROM postgres:12.4-alpine

RUN apk add --no-cache libpq make gcc clang llvm musl-dev python3-dev git

ENV PG_CONFIG=/usr/local/bin/pg_config

RUN git clone https://github.com/arkhipov/temporal_tables.git && cd temporal_tables && \
    make && make install

WORKDIR ./temporal_tables

RUN echo "make installcheck PGUSER=postgres" > ./check.sh
RUN chmod +x ./check.sh

EXPOSE 5432

CMD ["postgres"]