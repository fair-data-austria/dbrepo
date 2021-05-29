const { chromium } = require('playwright')

let browserPromise

export function before (t) {
  browserPromise = chromium.launch({
    // headless: false,
    // slowMo: 500
  })
}

export async function after (t) {
  await (await browserPromise).close()
}

export async function pageMacro (t, callback) {
  const browser = await browserPromise
  const page = await browser.newPage()

  page.go = function (s) {
    return this.goto('http://localhost:' + (process.env.PORT || 3001) + s)
  }

  try {
    await callback(t, page)
  } finally {
    await page.close()
  }
}
