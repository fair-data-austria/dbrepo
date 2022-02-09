import VuexPersistence from 'vuex-persist'

export default ({ store }) => {
  new VuexPersistence({
    storage: window.localStorage,
    reducer: state => ({
      token: state.token,
      user: state.user
    })
  }).plugin(store)
}
