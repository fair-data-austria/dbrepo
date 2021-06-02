const { chromium } = require('playwright')

let browserPromise

export function before (t) {
  const config = {
    // extra settings
    // headless: false // helps debugging
  }
  const debugConfig = {}

  if (process.env.SLOWMO) {
    debugConfig.slowMo = Number(process.env.SLOWMO)
  }

  browserPromise = chromium.launch({
    ...config,
    ...debugConfig
  })
}

export async function after (t) {
  await (await browserPromise).close()
}

export async function pageMacro (t, callback) {
  const browser = await browserPromise
  const context = await browser.newContext({
    recordVideo: {
      dir: 'videos/',
      size: { width: 1024, height: 768 }
    }
  })
  const page = await context.newPage()

  page.go = function (s) {
    return this.goto('http://localhost:' + (process.env.PORT || 3001) + s)
  }

  try {
    await callback(t, page)
  } finally {
    const path = await page.video().path()
    await page.close()
    await context.close()
    console.log(`vvv Video vvv: ${path}`)
  }
}
