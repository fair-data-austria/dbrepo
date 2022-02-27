<template>
  <div>
    <v-toolbar flat>
      <v-toolbar-title>Import data</v-toolbar-title>
    </v-toolbar>
    <v-card>
      <v-card-title v-if="!loading">
        {{ table.name }}
      </v-card-title>
      <v-card-subtitle>{{ table.internal_name }}</v-card-subtitle>
      <v-card-text>
        <v-row dense>
          <v-col cols="8">
            <v-select
              v-model="table.separator"
              :items="separators"
              disabled
              required
              hint="Character separating the values"
              label="Separator" />
          </v-col>
        </v-row>
        <v-row dense>
          <v-col cols="8">
            <v-text-field
              v-model="table.skip_lines"
              type="number"
              disabled
              required
              hint="Skip n lines from the top"
              label="Skip Lines"
              placeholder="e.g. 0" />
          </v-col>
        </v-row>
        <v-row dense>
          <v-col cols="8">
            <v-text-field
              v-model="table.null_element"
              hint="Representation of 'no value present'"
              placeholder="e.g. NA"
              disabled
              label="NULL Element" />
          </v-col>
        </v-row>
        <v-row dense>
          <v-col cols="8">
            <v-text-field
              v-model="table.true_element"
              label="True Element"
              hint="Representation of boolean 'true'"
              disabled
              placeholder="e.g. 1, true, YES" />
          </v-col>
        </v-row>
        <v-row dense>
          <v-col cols="8">
            <v-text-field
              v-model="table.false_element"
              label="False Element"
              hint="Representation of boolean 'false'"
              disabled
              placeholder="e.g. 0, false, NO" />
          </v-col>
        </v-row>
        <v-row dense>
          <v-col cols="8">
            <v-file-input
              v-model="file"
              accept="text/csv"
              show-size
              label="CSV File" />
          </v-col>
        </v-row>
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
      separators: [
        ',',
        ';',
        '-',
        '|',
        '$',
        '%',
        '#'
      ],
      table: {
        name: null,
        internal_name: null,
        separator: null,
        skip_lines: null,
        null_element: null,
        true_element: null,
        false_element: null
      },
      file: null,
      fileLocation: null
    }
  },
  computed: {
    tableId () {
      return this.$route.params.table_id
    },
    databaseId () {
      return this.$route.params.database_id
    },
    token () {
      return this.$store.state.token
    }
  },
  mounted () {
    this.info()
  },
  methods: {
    async info () {
      this.loading = true
      const infoUrl = `/api/container/${this.$route.params.container_id}/database/${this.databaseId}/table/${this.tableId}`
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
          headers: {
            'Content-Type': 'multipart/form-data',
            Authorization: `Bearer ${this.token}`
          }
        })
        if (res.data.success) {
          this.fileLocation = res.data.file.filename
          console.debug('upload csv', res.data)
        } else {
          console.error('Could not upload CSV data', res.data)
          return
        }
      } catch (err) {
        console.error('Could not upload data.', err)
        return
      }
      const insertUrl = `/api/container/${this.$route.params.container_id}/database/${this.databaseId}/table/${this.tableId}/data/import`
      let insertResult
      try {
        insertResult = await this.$axios.post(insertUrl, { location: `/tmp/${this.fileLocation}` }, {
          headers: { Authorization: `Bearer ${this.token}` }
        })
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
