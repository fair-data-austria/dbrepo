module.exports = {
  root: true,
  env: {
    browser: true,
    node: true
  },
  parserOptions: {
    parser: 'babel-eslint'
  },
  extends: [
    '@nuxtjs',
    'plugin:nuxt/recommended'
  ],
  plugins: [
  ],
  // add your custom rules here
  rules: {
    'no-console': 'off',
    'vue/html-closing-bracket-newline': [
      'error',
      { singleline: 'never', multiline: 'never' }
    ],
    'vue/singleline-html-element-content-newline': 0
  }
}
