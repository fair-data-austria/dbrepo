const test = require('ava')
const { pageMacro, before, after } = require('./_utils')

test.before(before)
test.after(after)

test('create database', pageMacro, async (t, page) => {
  const database = 'Test Database ' + Math.random().toString(36).substring(7)
  const description = 'Test Description'

  await page.go('/databases')

  // Click create new button
  await page.click('button:has-text("Create Database")')

  // Fill database name
  await page.fill('input[name="database"]', database)

  // Fill database description
  await page.fill('textarea[name="description"]', description)

  // Press Tab
  await page.press('textarea[name="description"]', 'Tab')

  // Select postgres:latest
  await page.press('#engine', 'ArrowDown')

  // Click submit button
  await page.click('#createDB')

  // See notification
  const success = await page.waitForSelector('.v-toast-text')
  t.true(!!success, `Database ${database} seems not to be created, notification not found`)

  // // make sure row exists
  // const newRow = await page.waitForSelector(`tr >> text=${database}`)
  // t.true(!!newRow, `DB ${database} row does not exist in DB list`)
  //
  // // Go to gffff info page
  // await page.click(`text=${database} >> a`)
  //
  // // click on admin tab
  // await page.click('text=admin')
  //
  // // click delete
  // await page.click('.v-btn >> text=delete')
  //
  // // confirm deletion in dialog
  // await page.click('.v-dialog .v-btn >> text=delete')
  // // await page.go('/gffff')
  //
  // // assert table row does not exist
  // const oldRow = await page.$(`tr >> text=${database}`)
  // t.false(!!oldRow, `Database ${database} not deleted`)
})
