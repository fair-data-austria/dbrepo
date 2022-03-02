export const state = () => ({
  token: null,
  user: null,
  db: null,
  table: null
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
  },

  /**
   Workaround. Helps to go 'back' from table data view and
   have the accordion open on the same table
   */
  SET_TABLE (state, table) {
    state.table = table
  }
}
