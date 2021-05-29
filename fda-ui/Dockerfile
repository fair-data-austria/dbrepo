FROM node:lts-alpine as build
MAINTAINER Kirill Stytsenko <kirill.stytsenko@univie.ac.at>

# python, make, g++ are needed by node-gyp
RUN apk add --no-cache build-base python3 > /dev/null

COPY ./.env ./
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

RUN yarn install --frozen-lockfile > /dev/null 2>&1

RUN yarn build

EXPOSE 3000

ENTRYPOINT ["yarn", "start"]
