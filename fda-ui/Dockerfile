###### FIRST STAGE ######
FROM node:alpine3.13 as build
MAINTAINER Martin Weise <martin.weise@tuwien.ac.at>

RUN apk add --no-cache build-base python3 > /dev/null

COPY ./package.json ./
COPY ./yarn.lock ./

RUN yarn install > /dev/null

COPY . .

RUN yarn generate > /dev/null

EXPOSE 3000

ENTRYPOINT ["npm", "start"]
