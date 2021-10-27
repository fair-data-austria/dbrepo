const express = require('express')
const app = express()
const multer = require('multer')
const upload = multer({ dest: '/tmp' })
const fetch = require('node-fetch')

app.use(express.json())

const { buildQuery } = require('./query')

// TODO extend me
const colTypeMap = {
  Boolean: 'BOOLEAN',
  Date: 'DATE',
  Integer: 'NUMBER',
  Numeric: 'NUMBER',
  String: 'STRING',
  Text: 'STRING',
  Timestamp: 'DATE'
}

app.post('/table_from_csv', upload.single('file'), async (req, res) => {
  const { file } = req
  const { path } = file

  // send path to analyse service
  let analysis
  try {
    // FIXME: find what the endpoint actually is called
    console.log('FOO', `${process.env.API}/analyse/determinedt`)

    analysis = await fetch(`${process.env.API}/analyse/determinedt/`, {
      method: 'post',
      body: JSON.stringify({ filepath: path }),
      headers: { 'Content-Type': 'application/json' }
    })
    console.debug('analyzed', analysis)
    analysis = await analysis.json()
    analysis = JSON.parse(analysis)
  } catch (error) {
    return res.json({ success: false, error, analysis })
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
      primaryKey: false,
      unique: null,
      enumValues: []
    }
  })

  res.json({ success: true, file, columns: entries })
})

app.post('/query/build', (req, res) => {
  return res.json(buildQuery(req.body))
})

module.exports = app
