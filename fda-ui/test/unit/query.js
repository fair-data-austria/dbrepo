const test = require('ava')
const { buildQuery } = require('../../server-middleware/query')

// for e2e nuxt see https://soshace.com/writing-end-to-end-tests-for-nuxt-apps-using-jsdom-and-ava/

test('simple select', (t) => {
  const r = buildQuery({
    table: 'Table'
  })
  t.is(r.sql, 'select * from "Table"')
})

test('select some columns', (t) => {
  const r = buildQuery({
    table: 'Table',
    select: ['aaa', 'bbb']
  })
  t.is(r.sql, 'select "aaa", "bbb" from "Table"')
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
