const axios = require('axios')
const MockAdapter = require('axios-mock-adapter')

// This sets the mock adapter on the default instance
const mock = new MockAdapter(axios)

// TODO only enable mocking when in dev mode
// console.log(process.env)

// Mock any GET request
// arguments for reply are (status, data, headers)
mock.onGet('/database/database').reply(200, [
  {
    DbName: 'myDB',
    ContainerID: 'container1',
    ContainerName: 'myContainer',
    IpAddress: '123',
    Status: 'status',
    Created: '2021-03-31T09:48:07Z'
  }
])
