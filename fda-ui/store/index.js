export const state = () => ({
  db: null,
  token: null
})

export const mutations = {
  SET_DATABASE (state, db) {
    state.db = db
  },
  SET_TOKEN (state, token) {
    state.token = token
  }
}
