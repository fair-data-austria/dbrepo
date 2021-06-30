// Set up JSDom.
require('jsdom-global')()

// Setup browser environment
const hooks = require('require-extension-hooks')
const Vue = require('vue')

const Vuetify = require('vuetify')

// Fix the Date object, see <https://github.com/vuejs/vue-test-utils/issues/936#issuecomment-415386167>.
window.Date = Date

// prevent unknown components: <v-btn>, etc.
Vue.use(Vuetify)

// remove dev tip
Vue.config.devtools = false
// remove production tip
Vue.config.productionTip = false

// Setup vue files to be processed by `require-extension-hooks-vue`
hooks('vue').plugin('vue').push()

// Setup vue and js files to be processed by `require-extension-hooks-babel`
hooks(['vue', 'js'])
  .exclude(({ filename }) => filename.match(/\/node_modules\//))
  .plugin('babel')
  .push()
