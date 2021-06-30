export const state = () => ({
  db: null
})

export const mutations = {
  SET_DATABASE (state, db) {
    state.db = db
  }
}
