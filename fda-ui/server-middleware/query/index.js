const { format } = require('sql-formatter')
const knex = require('knex')({ client: 'pg' })

export function buildQuery ({ table, select, clauses }) {
  const builder = knex(table)
  clauses = clauses || []

  select = select || []
  builder.select(...select)

  for (let i = 0; i < clauses.length; i++) {
    const clause = clauses[i]
    const cmd = builder[clause.type]
    let { params } = clause
    if (!params) {
      params = []
    }
    if (params.length >= 2) {
      params[2] = castNum(params[2])
    }
    if (typeof cmd === 'function') {
      builder[clause.type].apply(builder, params)
    }
  }

  let sql, formatted
  try {
    sql = builder.toQuery()
    formatted = format(sql)
  } catch (e) {
    return {
      error: e.message
    }
  }
  return {
    table,
    statements: builder._statements,
    sql,
    formatted
  }
}

export function castNum (s) {
  if (typeof s !== 'string') {
    return s
  }

  const num = Number(s)

  const ss = String(num)

  if (s !== ss) {
    return s
  }

  return num
}
