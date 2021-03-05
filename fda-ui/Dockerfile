###### FIRST STAGE ######
FROM node:alpine3.10 as build
MAINTAINER Martin Weise <martin.weise@tuwien.ac.at>

RUN apk add --no-cache build-base python3 > /dev/null

COPY ./package.json ./
COPY ./yarn.lock ./

RUN yarn install > /dev/null

COPY ./assets ./assets
COPY ./lang ./lang
COPY ./middleware ./middleware
COPY ./plugins ./plugins
COPY ./store ./store
COPY ./components ./components
COPY ./pages ./pages
COPY ./static ./static
COPY ./nuxt.config.js ./
COPY ./.env ./

EXPOSE 3000

ENTRYPOINT ["yarn", "docker"]
