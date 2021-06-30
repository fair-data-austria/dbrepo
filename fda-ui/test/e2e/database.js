const test = require('ava')
const { pageMacro, before, after } = require('./_utils')

test.before(before)
test.after(after)

test('create & delete database', pageMacro, async (t, page) => {
  const dbname = 'TestDB_' + Math.random().toString(36).substring(7)

  await page.go('/databases')

  // Click button:has-text("Create Database")
  await page.click('button:has-text("Create Database")')

  // Fill random DB Name
  await page.fill('#dbname', dbname)

  // Click Create button
  await page.click('#createDB')

  // make sure row exists
  const newRow = await page.waitForSelector(`tr >> text=${dbname}`)
  t.true(!!newRow, `DB ${dbname} row does not exist in DB list`)

  // Go to db info page
  await page.click(`text=${dbname} >> a`)

  // click on admin tab
  await page.click('text=admin')

  // click delete
  await page.click('.v-btn >> text=delete')

  // confirm deletion in dialog
  await page.click('.v-dialog .v-btn >> text=delete')
  // await page.go('/databases')

  // assert table row does not exist
  const oldRow = await page.$(`tr >> text=${dbname}`)
  t.false(!!oldRow, `Database ${dbname} not deleted`)
})
