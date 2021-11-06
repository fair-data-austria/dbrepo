# FROM node:lts as build
FROM node:14-alpine as build
MAINTAINER Kirill Stytsenko <kirill.stytsenko@univie.ac.at>

ENV NODE_ENV=production

WORKDIR /app

COPY ./package.json ./

# Install npm dependencies
RUN npm install > /dev/null 2>&1

COPY ./nuxt.config.js ./
COPY ./.env-docker ./.env
COPY ./ava.config.cjs ./
COPY ./babel.config.js ./
COPY ./assets ./assets
COPY ./components ./components
COPY ./lang ./lang
COPY ./layouts ./layouts
COPY ./pages ./pages
COPY ./plugins ./plugins
COPY ./server-middleware ./server-middleware
COPY ./static ./static
COPY ./store ./store
COPY ./yarn.lock ./
COPY ./service_ready ./

RUN yarn generate

# Minimize image & cleanup
RUN rm -rf /tmp/*

HEALTHCHECK --interval=10s --timeout=3s --retries=6 CMD service_ready

EXPOSE 3000

ENTRYPOINT ["yarn", "start"]
