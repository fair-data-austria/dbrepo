<template>
  <div>
    <v-card>
      <v-card-title v-if="!loading">
        Import CSV
      </v-card-title>
      <v-card-subtitle>Table xxx</v-card-subtitle>
      <v-card-text>
        <v-checkbox
          v-model="tableInsert.skipHeader"
          label="Skip first row" />
        <v-text-field
          v-model="tableInsert.nullElement"
          placeholder="e.g. NA or leave empty"
          label="NULL Element" />
        <v-text-field
          v-model="tableInsert.delimiter"
          label="Delimiter"
          placeholder="e.g. ;" />
        <v-file-input
          v-model="file"
          accept="text/csv"
          show-size
          label="CSV File" />
      </v-card-text>
      <v-card-actions>
        <v-btn :disabled="!file" :loading="loading" color="primary" @click="upload">Next</v-btn>
      </v-card-actions>
    </v-card>
  </div>
</template>
<script>
export default {
  name: 'TableImportCSV',
  components: {
  },
  data () {
    return {
      loading: false,
      tableInsert: {
        skipHeader: false,
        nullElement: null,
        delimiter: null,
        csvLocation: null
      },
      file: null
    }
  },
  mounted () {
  },
  methods: {
    async upload () {
      this.loading = true
      const url = '/server-middleware/table_from_csv'
      const data = new FormData()
      data.append('file', this.file)
      try {
        const res = await this.$axios.post(url, data, {
          headers: { 'Content-Type': 'multipart/form-data' }
        })
        if (res.data.success) {
          this.tableInsert.csvLocation = res.data.file.filename
          console.debug('upload csv', res.data)
        } else {
          console.error('Could not upload CSV data', res.data)
          this.loading = false
          return
        }
      } catch (err) {
        console.error('Could not upload data.', err)
        this.loading = false
        return
      }
      const insertUrl = `http://localhost:9094/api/database/${this.$route.params.database_id}/table/${this.$route.params.table_id}/data`
      let insertResult
      try {
        insertResult = await this.$axios.post(insertUrl, this.tableInsert)
        console.debug('inserted table', insertResult.data)
      } catch (err) {
        console.error('Could not insert data.', err)
        this.loading = false
        return
      }
      this.$toast.success('Uploaded csv into table.')
      this.loading = false
    }
  }
}
</script>

<style>
</style>
