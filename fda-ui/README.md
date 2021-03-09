# fda-ui

## Prerequisit

We use Ubuntu 20.04 LTS.

## Build environment: yarn

Install yarn:

First we need curl:

	sudo apt install curl

Add yarn GPG-Key

	curl -sL https://dl.yarnpkg.com/debian/pubkey.gpg | sudo apt-key add -

Add repo

	echo "deb https://dl.yarnpkg.com/debian/ stable main" | sudo tee /etc/apt/sources.list.d/yarn.list

Install yarn

	sudo apt update && sudo apt install yarn

Run yarn

	$ yarn

which gives yarn install v1.22.5

## Nuxt

With yarn installed, install nuxt.js

See https://nuxtjs.org/

	yarn add nuxt


## Prepare

Configure the `.env` file for the IP and port running.

## Build Setup

```bash
# install dependencies
$ yarn install

# serve with hot reload at localhost:3000
$ yarn dev

# build for production and launch server
$ yarn build
$ yarn start

# generate static project
$ yarn generate
```

For detailed explanation on how things work, check out [Nuxt.js docs](https://nuxtjs.org).
