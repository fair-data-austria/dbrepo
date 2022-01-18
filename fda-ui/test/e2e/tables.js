const test = require('ava')
const axios = require('axios')
const { pageMacro, before, after } = require('./_utils')

test.before(before)
test.after(after)

test('create table using form', pageMacro, async (t, page) => {
  const database = 'Test Database ' + Math.random().toString(36).substring(7)
  const table = 'Test Table ' + Math.random().toString(36).substring(7)
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

  const id = await axios.get('http://localhost:9092/api/database/').then(function (response) {
    return response.filter(function (item) {
      return item.name === database
    }).id
  })

  // -------------------------------------------------------------------------------------------------------------------

  await page.go('/databases/' + id + '/tables')

  // Click create new button
  await page.click('button:has-text("Create Table")')

  // Fill table name
  await page.fill('input[name="name"]', table)

  // Fill table description
  await page.fill('input[name="description"]', description)

  // Click submit button
  await page.click('button:has-text("Create Table")')

  // See page load
  success = await page.waitForSelector('text=aaaaaaaa')
  t.true(!!success, `Table ${database} seems not to be created, notification not found`)
})
