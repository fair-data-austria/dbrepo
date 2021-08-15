# FROM rabbitmq:alpine # for production
FROM rabbitmq:3-management-alpine

COPY ./service_ready /usr/bin
RUN chmod +x /usr/bin/service_ready

COPY ./rabbitmq.conf /etc/rabbitmq/