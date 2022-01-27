<template>
  <div>
    <h3 class="mb-2 mt-1">Create Table Schema (and Import Data) from .csv</h3>
    <v-stepper v-model="step" vertical>
      <v-stepper-step :complete="step > 1" step="1">
        Table Metadata
      </v-stepper-step>

      <v-stepper-content class="pt-0 pb-1" step="1">
        <v-text-field
          v-model="tableCreate.name"
          required
          autocomplete="off"
          label="Name" />
        <v-text-field
          v-model="tableCreate.description"
          autocomplete="off"
          label="Description" />
        <v-btn :disabled="!step1Valid" color="primary" @click="step = 2">
          Continue
        </v-btn>
      </v-stepper-content>

      <v-stepper-step :complete="step > 2" step="2">
        .csv Metadata
      </v-stepper-step>

      <v-stepper-content step="2">
        <v-row dense>
          <v-col cols="8">
            <v-checkbox
              v-model="tableInsert.skip_header"
              label="Skip first row" />
          </v-col>
        </v-row>
        <v-row dense>
          <v-col cols="8">
            <v-text-field
              v-model="tableInsert.delimiter"
              :rules="[rules.required]"
              required
              label="Delimiter"
              placeholder="e.g. ;" />
          </v-col>
        </v-row>
        <v-row dense>
          <v-col cols="8">
            <v-text-field
              v-model="tableInsert.null_element"
              placeholder="e.g. NA or leave empty"
              label="NULL Element" />
          </v-col>
        </v-row>
        <v-row dense>
          <v-col cols="8">
            <v-text-field
              v-model="tableInsert.true_element"
              label="Element for 'true'"
              placeholder="e.g. 1, true or YES" />
          </v-col>
        </v-row>
        <v-row dense>
          <v-col cols="8">
            <v-text-field
              v-model="tableInsert.false_element"
              label="Element for 'false'"
              placeholder="e.g. 0, false or NO" />
          </v-col>
        </v-row>
        <v-row dense>
          <v-col cols="8">
            <v-file-input
              v-model="file"
              accept="text/csv"
              :rules="[rules.required]"
              show-size
              label="CSV File" />
          </v-col>
        </v-row>
        <v-row dense>
          <v-col cols="6">
            <v-btn :disabled="!file" :loading="loading" color="primary" @click="upload">Next</v-btn>
          </v-col>
        </v-row>
      </v-stepper-content>

      <v-stepper-step :complete="step > 3" step="3">
        Choose data type of columns
      </v-stepper-step>
      <v-stepper-content step="3">
        <div v-for="(c, idx) in tableCreate.columns" :key="idx">
          <v-row dense class="column pa-2 ml-1 mr-1 mb-2">
            <v-col cols="2">
              <v-text-field v-model="c.name" disabled required label="Name" />
            </v-col>
            <v-col cols="2">
              <v-select
                v-model="c.type"
                :items="columnTypes"
                item-value="value"
                required
                label="Data Type" />
            </v-col>
            <v-col cols="2">
              <v-select
                v-model="c.enumValues"
                :disabled="c.type !== 'ENUM'"
                :items="c.suggestions"
                :menu-props="{ maxHeight: '400' }"
                label="Enumeration"
                multiple />
            </v-col>
            <v-col cols="auto" class="pl-2">
              <v-checkbox v-model="c.primaryKey" label="Primary Key" @click="setOthers(c)" />
            </v-col>
            <v-col cols="auto" class="pl-10">
              <v-checkbox v-model="c.nullAllowed" :disabled="c.primaryKey" label="Null Allowed" />
            </v-col>
            <v-col cols="auto" class="pl-10">
              <v-checkbox v-model="c.unique" :disabled="c.primaryKey" label="Unique" />
            </v-col>
          </v-row>
        </div>

        <v-btn class="mt-2" color="primary" @click="createTable">
          Continue
        </v-btn>
      </v-stepper-content>

      <v-stepper-step
        :complete="step > 4"
        step="4">
        Done
      </v-stepper-step>

      <v-stepper-content step="4">
        Proceed to table view.
        <div class="mt-2">
          <v-btn :to="`/container/${$route.params.container_id}/database/${$route.params.database_id}/table/${newTableId}`" outlined>
            <v-icon>mdi-table</v-icon>
            View
          </v-btn>
        </div>
      </v-stepper-content>
    </v-stepper>
    <v-breadcrumbs :items="items" class="pa-0 mt-2" />
  </div>
</template>
<script>
export default {
  name: 'TableFromCSV',
  components: {
  },
  data () {
    return {
      step: 1,
      items: [
        { text: 'Databases', href: '/container' },
        { text: `${this.$route.params.database_id}`, href: `/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/info` }
      ],
      rules: {
        required: value => !!value || 'Required'
      },
      tableInsert: {
        skip_header: true,
        false_element: null,
        true_element: null,
        null_element: null,
        delimiter: null,
        csv_location: null
      },
      tableCreate: {
        name: null,
        description: null,
        columns: []
      },
      loading: false,
      file: null,
      fileLocation: null,
      columns: [],
      columnTypes: [
        { value: 'ENUM', text: 'Enumeration' },
        { value: 'BOOLEAN', text: 'Boolean' },
        { value: 'NUMBER', text: 'Number' },
        { value: 'BLOB', text: 'Binary Large Object' },
        { value: 'DATE', text: 'Date' },
        { value: 'STRING', text: 'Character Varying' },
        { value: 'TEXT', text: 'Text' }
      ],
      newTableId: 42
    }
  },
  computed: {
    step1Valid () {
      return this.tableCreate.name !== null && this.tableCreate.name.length > 0
    }
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
        console.log(res.data)

        if (res.data.success) {
          this.tableCreate.columns = res.data.columns
          this.fileLocation = res.data.file.filename
          this.tableInsert.csvLocation = this.fileLocation
          this.step = 3
          this.loading = false
          console.debug('upload csv', res.data)
        } else {
          console.error('Upload failed. Try from docker container, not with yarn dev', res)
          this.$toast.error('Could not upload CSV data')
          this.loading = false
          return
        }
      } catch (err) {
        this.$toast.error('Could not upload data.')
        return
      }
      this.loading = false
    },
    setOthers (column) {
      column.nullAllowed = false
      column.unique = true
    },
    async createTable () {
      /* make enum values to array */
      this.tableCreate.columns.forEach((column) => {
        // validate `id` column: must be a PK
        if (column.name === 'id' && (!column.primaryKey)) {
          this.$toast.error('Column `id` has to be a Primary Key')
          return
        }
        if (column.enumValues == null) {
          return
        }
        if (column.enumValues.length > 0) {
          column.enumValues = column.enumValues.split(',')
        }
      })
      const createUrl = `/api/container/${this.route.params.container_id}/database/${this.$route.params.database_id}/table`
      let createResult
      try {
        createResult = await this.$axios.post(createUrl, this.tableCreate)
        this.newTableId = createResult.data.id
        console.debug('created table', createResult.data)
      } catch (err) {
        console.log(err)
        return
      }
      const insertUrl = `/api/container/${this.route.params.container_id}/database/${this.$route.params.database_id}/table/${createResult.data.id}/data/csv`
      let insertResult
      try {
        insertResult = await this.$axios.post(insertUrl, this.tableInsert)
        console.debug('inserted table', insertResult.data)
      } catch (err) {
        console.log(err)
        this.$toast.error('Could not insert csv into table.')
        return
      }
      this.step = 4
    }
  }
}
</script>

<style scoped>
</style>
