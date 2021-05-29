// const test = require('ava')

// for e2e nuxt see https://soshace.com/writing-end-to-end-tests-for-nuxt-apps-using-jsdom-and-ava/

// test('e2e placeholder', (t) => {
//   t.is(2, 1)
// })
// import { get, setupTest } from '@nuxt/test-utils'
const { get, setupTest } = require('@nuxt/test-utils')

describe('My test', () => {
  setupTest({
    server: true
  })

  it('renders the index page', async () => {
    const { body } = await get('/')
    expect(body).toContain('Welcome')
  })
})
