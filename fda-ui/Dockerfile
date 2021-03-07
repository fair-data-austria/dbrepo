###### FIRST STAGE ######
FROM node:alpine3.13 as build
MAINTAINER Martin Weise <martin.weise@tuwien.ac.at>

RUN apk add --no-cache build-base python3 > /dev/null

COPY ./package.json ./
COPY ./yarn.lock ./

RUN yarn install > /dev/null

COPY ./assets ./assets
COPY ./components ./components
COPY ./lang ./lang
COPY ./layouts ./layouts
COPY ./middleware ./middleware
COPY ./plugins ./plugins
COPY ./store ./store
COPY ./pages ./pages
COPY ./static ./static
COPY ./nuxt.config.js ./
COPY ./.env ./
COPY ./.babelrc ./

RUN yarn generate > /dev/null

EXPOSE 3000

ENTRYPOINT ["npm", "start"]
