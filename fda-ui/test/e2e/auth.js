const test = require('ava')
const { pageMacro, before, after } = require('./_utils')

test.before(before)
test.after(after)

test('redirect', pageMacro, async (t, page) => {
  await page.go('/')

  // Click login button
  await page.click('button:has-text("Login")')

  // See notification
  const success = await page.waitForSelector(':has-text("TU Wien Login")')
  t.true(!!success, 'Redirect to TU SSO failed')
})
