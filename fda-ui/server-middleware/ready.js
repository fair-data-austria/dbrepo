import path from 'path'
const fs = require('fs')

fs.closeSync(fs.openSync(path.resolve(__dirname, '../ready'), 'w'))

// Since we are a serverMiddleware, we have to return a handler, even if this it does nothing
// I think this is really ugly...
export default function (req, res, next) {
  next()
}
