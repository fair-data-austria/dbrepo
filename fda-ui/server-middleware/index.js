// const bodyParser = require('body-parser')
const app = require('express')()
const multer = require('multer')
const upload = multer({ dest: '/tmp' })
const fetch = require('node-fetch')

// TODO extend me
const colTypeMap = {
  Boolean: 'BOOLEAN',
  Date: 'DATE',
  Integer: 'NUMBER',
  Numeric: 'NUMBER',
  String: 'STRING',
  Timestamp: 'DATE'
}

app.post('/table_from_csv', upload.single('file'), async (req, res) => {
  const { file } = req
  const { path } = file

  // send path to analyse service
  let analysis
  try {
    analysis = await fetch(`${process.env.API_ANALYSE}/datatypesbypath?filepath=${path}`)
    analysis = await analysis.json()
  } catch (error) {
    return res.json({ success: false, error })
  }

  // map messytables / CoMi's `determine_dt` column types to ours
  // e.g. "Integer" -> "NUMBER"
  let entries = Object.entries(analysis.columns)
  entries = entries.map(([k, v]) => {
    if (colTypeMap[v]) {
      v = colTypeMap[v]
    }
    return {
      name: k,
      type: v,
      nullAllowed: true,
      primaryKey: false
    }
  })

  res.json({ success: true, file, columns: entries })
})

module.exports = app
