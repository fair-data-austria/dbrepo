const test = require('ava')
const { pageMacro, before, after } = require('./_utils')

test.before(before)
test.after(after)

test('visit homepage', pageMacro, async (t, page) => {
  await page.go('/')
  t.is(await page.title(), 'fda-ui - fda-ui')
})
