# FROM node:lts as build
FROM node:14-alpine as build
MAINTAINER Kirill Stytsenko <kirill.stytsenko@univie.ac.at>

WORKDIR /app

# Our app
COPY ./.env-docker ./.env
COPY ./assets ./assets
COPY ./ava.config.cjs ./
COPY ./babel.config.js ./
COPY ./components ./components
COPY ./lang ./lang
COPY ./layouts ./layouts
COPY ./nuxt.config.js ./
COPY ./package.json ./
COPY ./pages ./pages
COPY ./plugins ./plugins
COPY ./server-middleware ./server-middleware
COPY ./static ./static
COPY ./store ./store
COPY ./test ./test
COPY ./yarn.lock ./

# Install npm dependencies
RUN yarn install --frozen-lockfile > /dev/null 2>&1

RUN yarn build

EXPOSE 3000

ENTRYPOINT ["yarn", "start"]
