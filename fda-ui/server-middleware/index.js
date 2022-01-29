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
    const url = `${process.env.API}/api/analyse/determinedt`
    console.log('analyse service url', url)
    analysis = await fetch(url, {
      method: 'post',
      body: JSON.stringify({ filepath: path }),
      headers: { 'Content-Type': 'application/json' }
    })
    console.debug('analyzed', analysis)
    const json = await analysis.json()
    console.log('json', json)
    analysis = JSON.parse(json)
    console.log(analysis)
    if (!analysis.columns) {
      return res.json({ success: false, message: 'Columns array missing' })
    }
  } catch (error) {
    console.error('failed to analyze', error)
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
      date_format: null,
      check_expression: null,
      foreign_key: null,
      references: null,
      null_allowed: true,
      primary_key: false,
      unique: null,
      enum_values: []
    }
  })

  res.json({ success: true, file, columns: entries })
})

app.post('/query/build', (req, res) => {
  return res.json(buildQuery(req.body))
})

module.exports = app
