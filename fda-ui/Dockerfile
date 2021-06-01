# FROM node:lts as build
FROM mcr.microsoft.com/playwright:v1.11.1-focal as build

ENV DEBIAN_FRONTEND noninteractive

MAINTAINER Kirill Stytsenko <kirill.stytsenko@univie.ac.at>
WORKDIR /app

# python, make, g++ are needed by node-gyp
# RUN apk add --no-cache build-base python3 > /dev/null
RUN apt-get install make g++ python3 -y > /dev/null

# 4. Install Chromium dependencies - playwright
RUN apt-get update && apt-get install -y --no-install-recommends \
  libnss3 \
  libxss1 \
  libasound2 \
  fonts-noto-color-emoji \
  libxtst6

# Our app
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

# Install npm dependencies
RUN yarn install --frozen-lockfile #> /dev/null 2>&1

RUN yarn build

EXPOSE 3000

ENTRYPOINT ["yarn", "start"]
