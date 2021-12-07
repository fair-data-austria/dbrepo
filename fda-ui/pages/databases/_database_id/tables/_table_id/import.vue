<template>
  <div>
    <v-card>
      <v-card-title v-if="!loading">
        Import Data
      </v-card-title>
      <v-card-subtitle>{{ table.name }} ({{ table.internalName }})</v-card-subtitle>
      <v-card-text>
        <v-checkbox
          v-model="tableInsert.skipHeader"
          label="First row contains headers" />
        <v-text-field
          v-model="tableInsert.nullElement"
          placeholder="e.g. NA or leave empty"
          label="NULL Element" />
        <v-text-field
          v-model="tableInsert.delimiter"
          label="Delimiter"
          hint="Only 1 character"
          maxlength="1"
          placeholder="e.g. ;" />
        <v-file-input
          v-model="file"
          accept="text/csv"
          show-size
          label="CSV File" />
      </v-card-text>
      <v-card-actions>
        <v-col>
          <v-btn :disabled="!file" :loading="loading" color="primary" @click="upload">Next</v-btn>
        </v-col>
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
      table: {
        name: null,
        internalName: null
      },
      tableInsert: {
        skipHeader: false,
        nullElement: null,
        delimiter: ',',
        csvLocation: null
      },
      file: null
    }
  },
  computed: {
    tableId () {
      return this.$route.params.table_id
    },
    databaseId () {
      return this.$route.params.database_id
    }
  },
  mounted () {
    this.info()
  },
  methods: {
    async info () {
      this.loading = true
      const infoUrl = `/api/database/${this.databaseId}/table/${this.tableId}`
      try {
        const res = await this.$axios.get(infoUrl)
        console.debug('got table', res.data)
        this.table = res.data
      } catch (err) {
        console.error('Could not insert data.', err)
      }
      this.loading = false
    },
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
          return
        }
      } catch (err) {
        console.error('Could not upload data.', err)
        return
      }
      const insertUrl = `/api/database/${this.databaseId}/table/${this.tableId}/data/csv`
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
