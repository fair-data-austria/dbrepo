const test = require('ava')
const { pageMacro, before, after } = require('./_utils')

test.before(before)
test.after(after)

test('create database and see the tabs', pageMacro, async (t, page) => {
  const database = 'Test Database ' + Math.random().toString(36).substring(7)
  const description = 'Test Description'

  await page.go('/databases')

  // Click create new button
  await page.click('button:has-text("Database")')

  // Fill database name
  await page.fill('input[name="database"]', database)

  // Fill database description
  await page.fill('textarea[name="description"]', description)

  // Press Tab
  await page.press('textarea[name="description"]', 'Tab')

  // Select mariadb:10.5
  await page.press('#engine', 'ArrowDown')

  // Click submit button
  await page.click('button:has-text("Create")')

  // See page load
  let success = await page.waitForSelector('text=' + database)
  t.true(!!success, `Database ${database} seems not to be created, notification not found`)

  // -------------------------------------------------------------------------------------------------------------------

  await page.go('/databases/1/info')

  // find 'mariadb' anywhere on the page:
  success = await page.waitForSelector('text=mariadb:10.5')
  t.true(!!success, 'Could not find the mariadb image on the site')

  await page.go('/databases/1/tables')

  // find 'mariadb' anywhere on the page:
  success = await page.waitForSelector('text=(no tables)')
  t.true(!!success, 'Could not find the tables on the site')

  // -------------------------------------------------------------------------------------------------------------------

  await page.go('/databases/1/queries')

  // find 'mariadb' anywhere on the page:
  success = await page.waitForSelector('text=(no queries)')
  t.true(!!success, 'Could not find the queries on the site')
})
