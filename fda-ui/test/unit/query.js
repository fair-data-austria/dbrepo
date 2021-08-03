const test = require('ava')
const { buildQuery, castNum } = require('@/server-middleware/query')

test('simple select', (t) => {
  const r = buildQuery({
    table: 'Table'
  })
  t.is(r.sql, 'select * from "Table"')
})

test('select some columns', (t) => {
  const r = buildQuery({
    table: 'Table',
    select: ['databases', 'bbb']
  })
  t.is(r.sql, 'select "database", "bbb" from "Table"')
})

test('simple where clause', (t) => {
  const r = buildQuery({
    table: 'Table',
    clauses: [
      { type: 'where', params: ['foo', '=', 42] }
    ]
  })
  t.is(r.sql, 'select * from "Table" where "foo" = 42')
})

test('simple where clause with numeric string', (t) => {
  const r = buildQuery({
    table: 'Table',
    clauses: [
      { type: 'where', params: ['foo', '=', '42'] }
    ]
  })
  t.is(r.sql, 'select * from "Table" where "foo" = 42')
})

test('simple where clause with non-numeric string', (t) => {
  const r = buildQuery({
    table: 'Table',
    clauses: [
      { type: 'where', params: ['foo', '=', 'bla'] }
    ]
  })
  t.is(r.sql, 'select * from "Table" where "foo" = \'bla\'')
})

test('using unallowed operator', (t) => {
  const r = buildQuery({
    table: 'Table',
    clauses: [
      { type: 'where', params: ['foo', 'UNKNOWN', 42] }
    ]
  })
  t.is(r.sql, undefined)
  t.is(r.error, 'The operator "UNKNOWN" is not permitted')
})

test('where clause with explicit "and"', (t) => {
  const r = buildQuery({
    table: 'Table',
    clauses: [
      { type: 'where', params: ['foo', '=', 42] },
      { type: 'and' }, // here, unlike below
      { type: 'where', params: ['bar', '=', 42] }
    ]
  })
  t.is(r.sql, 'select * from "Table" where "foo" = 42 and "bar" = 42')
})

test('where clause with implicit "and"', (t) => {
  const r = buildQuery({
    table: 'Table',
    clauses: [
      { type: 'where', params: ['foo', '=', 42] },
      // not here, unlike above
      { type: 'where', params: ['bar', '=', 42] }
    ]
  })
  t.is(r.sql, 'select * from "Table" where "foo" = 42 and "bar" = 42')
})

test('where clause with "or"', (t) => {
  const r = buildQuery({
    table: 'Table',
    clauses: [
      { type: 'where', params: ['foo', '=', 42] },
      { type: 'or' },
      { type: 'where', params: ['bar', '=', 42] }
    ]
  })
  t.is(r.sql, 'select * from "Table" where "foo" = 42 or "bar" = 42')
})

test('cast numeric strings to numbers', (t) => {
  t.is(castNum(''), '')
  t.is(castNum(' '), ' ')
  t.is(castNum('0'), 0)
  t.is(castNum('0 '), '0 ')
  t.is(castNum('1'), 1)
  t.is(castNum('1 '), '1 ')
  t.is(castNum(1), 1)
  t.is(castNum('1'), 1)
  t.is(castNum('1.1'), 1.1)
  t.is(castNum('69.420'), '69.420')
  t.is(castNum('a'), 'a')
})
