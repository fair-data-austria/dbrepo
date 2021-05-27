const knex = require('knex')({ client: 'pg' })

export function buildQuery ({ table, select, clauses }) {
  const builder = knex(table)
  clauses = clauses || []

  select = select || []
  builder.select(...select)

  for (let i = 0; i < clauses.length; i++) {
    const clause = clauses[i]
    const cmd = builder[clause.type]
    if (typeof cmd === 'function') {
      builder[clause.type].apply(builder, clause.params)
    }
  }

  let sql
  try {
    sql = builder.toQuery()
  } catch (e) {
    return {
      error: e.message
    }
  }
  return {
    table,
    statements: builder._statements,
    sql
  }
}
