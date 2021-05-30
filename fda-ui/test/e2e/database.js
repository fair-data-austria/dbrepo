const test = require('ava')
const { pageMacro, before, after } = require('./_utils')

test.before(before)
test.after(after)

test('create & delete database', pageMacro, async (t, page) => {
  await page.go('/databases')
  // t.is(await page.title(), 'fda-ui - fda-ui')
})
