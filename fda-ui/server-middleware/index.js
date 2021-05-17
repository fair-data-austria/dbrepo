// const bodyParser = require('body-parser')
const app = require('express')()
const multer = require('multer')
const upload = multer({ dest: '/tmp' })
const fetch = require('node-fetch')

// TODO extend me
const colTypeMap = {
  Integer: 'NUMBER',
  String: 'TEXT'
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
    res.json({ success: false, error })
  }
  console.log('Analysis', analysis)

  // map `determine_dt` column types to ours
  // e.g. "Integer" -> "NUMBER"
  let entries = Object.entries(analysis.columns)
  console.log(entries)
  entries = entries.map(([k, v]) => {
    if (colTypeMap[v]) {
      v = colTypeMap[v]
    }
    return [k, v]
  })

  console.log(entries)

  res.json({ success: true, file, columns: entries })
})

module.exports = app
