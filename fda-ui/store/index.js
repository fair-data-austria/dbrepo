export const state = () => ({
  db: null,
  token: null,
  user: null
})

export const mutations = {
  SET_DATABASE (state, db) {
    state.db = db
  },
  SET_TOKEN (state, token) {
    state.token = token
  },
  SET_USER (state, user) {
    state.user = user
  }
}
