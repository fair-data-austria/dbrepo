module.exports = {
  preset: '@nuxt/test-utils',
  transformIgnorePatterns: [
    // "node_modules/(?!(react-native|my-project|react-native-button)/)"
    'node_modules/(?!(@nuxtjs/vuetify|nuxt-i18n)/)'
  ]
}
