import path from 'path'
import colors from 'vuetify/es5/util/colors'

// pick env vars from .env file or get them passed through docker-compose
require('dotenv').config()

if (!process.env.API) {
  throw new Error(`Environment variable API not defined.

Have you passed env vars from docker-compose or defined them in your .env file?`)
}

if (process.env.SANDBOX) {
  console.info('[FDA] Running in sandbox environment')
}

let serv = {
  https: {
    key: process.env.KEY,
    cert: process.env.CERT
  }
}
if (!process.env.KEY || !process.env.CERT) {
  serv = {
    port: 3000,
    host: '0.0.0.0',
    timing: false
  }
}

export default {
  target: 'server',

  telemetry: false,

  server: serv,

  head: {
    titleTemplate: '%s - Database Repository (Sandbox)',
    title: 'FAIR Data Austria',
    meta: [
      { charset: 'utf-8' },
      { name: 'viewport', content: 'width=device-width, initial-scale=1' },
      { hid: 'description', name: 'description', content: '' }
    ],
    link: [
      { rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' }
    ]
  },

  css: [
    '@assets/globals.scss'
  ],

  plugins: [
    { src: '~/plugins/toast', ssr: false },
    { src: '~/plugins/vendors', ssr: false }
  ],

  // Auto import components (https://go.nuxtjs.dev/config-components)
  components: true,

  buildModules: [
    '@nuxtjs/eslint-module',
    '@nuxtjs/vuetify'
  ],

  modules: [
    '@nuxtjs/proxy',
    '@nuxtjs/axios',
    ['nuxt-i18n', {
      locales: [
        { code: 'de', file: 'de-DE.js', name: 'Deutsch' },
        { code: 'en', file: 'en-US.js', name: 'English' }
      ],
      lazy: true,
      langDir: 'lang/',
      defaultLocale: 'en'
    }]
  ],

  axios: {
    proxy: true
  },

  proxy: {
    '/api': process.env.API
  },

  serverMiddleware: [
    { path: '/server-middleware', handler: path.resolve(__dirname, 'server-middleware/index.js') }
  ],

  vuetify: {
    customVariables: ['~/assets/variables.scss'],
    theme: {
      dark: false,
      themes: {
        light: {
          primary: colors.blue.darken2,
          accent: colors.grey.darken3,
          secondary: colors.amber.darken3,
          info: colors.teal.lighten1,
          warning: colors.amber.base,
          error: colors.deepOrange.accent4,
          success: colors.green.accent3
        }
      }
    }
  },

  build: {
    babel: {
      presets (env, [preset, options]) {
        return [
          ['@babel/preset-env', {
            targets: {
              node: 'current'
            }
          }]
        ]
      }
    }
  }
}
