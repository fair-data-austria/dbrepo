<template>
  <v-stepper v-model="step" vertical>
    <v-stepper-step :complete="step > 1" step="1">
      Upload CSV file
    </v-stepper-step>

    <v-stepper-content step="1">
      <v-row dense>
        <v-col cols="8">
          <v-file-input
            v-model="file"
            accept="text/csv"
            show-size
            label="CSV File" />
        </v-col>
        <v-col cols="4" class="mt-3">
          <v-btn :disabled="!file" :loading="loading" @click="upload">Upload</v-btn>
        </v-col>
      </v-row>
    </v-stepper-content>

    <v-stepper-step :complete="step > 2" step="2">
      Choose data type of columns
    </v-stepper-step>
    <v-stepper-content step="2">
      <div v-for="(c, idx) in columns" :key="idx">
        <v-row dense class="column pa-2 ml-1 mr-1">
          <v-col cols="4">
            <v-text-field v-model="c.name" required label="Name" />
          </v-col>
          <v-col cols="3">
            <v-select
              v-model="c.type"
              :items="columnTypes"
              item-value="value"
              required
              label="Data Type" />
          </v-col>
          <v-col cols="auto" class="pl-2">
            <v-checkbox v-model="c.primaryKey" label="Primary Key" />
          </v-col>
          <v-col cols="auto" class="pl-10">
            <v-checkbox v-model="c.nullAllowed" label="Null Allowed" />
          </v-col>
        </v-row>
      </div>

      <v-btn color="primary" @click="step = 3">
        Continue
      </v-btn>
    </v-stepper-content>

    <v-stepper-step
      :complete="step > 3"
      step="3">
      Done
    </v-stepper-step>

    <v-stepper-content step="3">
      Done. Go to table.
    </v-stepper-content>
  </v-stepper>
</template>
<script>
export default {
  name: 'TableFromCSV',
  components: {
  },
  data () {
    return {
      step: 1,
      loading: false,
      file: null,
      columns: [],
      columnTypes: [
        { value: 'ENUM', text: 'ENUM' },
        { value: 'BOOLEAN', text: 'BOOLEAN' },
        { value: 'NUMBER', text: 'NUMBER' },
        { value: 'BLOB', text: 'BLOB' },
        { value: 'DATE', text: 'DATE' },
        { value: 'STRING', text: 'STRING' },
        { value: 'TEXT', text: 'TEXT' }
      ]
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
          this.columns = res.data.columns
          this.step = 2
        } else {
          this.$toast.error('Could not upload CSV data')
        }
      } catch (err) {
        this.$toast.error('Could not upload data.')
      }
      this.loading = false
    }
  }
}
</script>

<style scoped>
</style>
