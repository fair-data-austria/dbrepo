// import VuexPersistence from 'vuex-persist'

// export default ({ store }) => {
//   new VuexPersistence({
//     storage: window.sessionStorage
//   }).plugin(store)
// }

import Vue from 'vue'
import hljs from 'highlight.js/lib/core'
import sql from 'highlight.js/lib/languages/sql'
import hljsVuePlugin from '@highlightjs/vue-plugin'
import 'highlight.js/styles/solarized-light.css'
// import 'highlight.js/styles/default.css'

hljs.registerLanguage('sql', sql)

Vue.use(hljsVuePlugin)
