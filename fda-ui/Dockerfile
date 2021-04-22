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
COPY ./pages ./pages
COPY ./plugins ./plugins
COPY ./plugins ./plugins
COPY ./static ./static
COPY ./store ./store
COPY ./nuxt.config.js ./
COPY ./.babelrc ./

RUN yarn build > /dev/null

EXPOSE 3000

ENTRYPOINT ["yarn", "start"]
