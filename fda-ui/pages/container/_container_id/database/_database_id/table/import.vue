<template>
  <div>
    <v-toolbar flat>
      <v-toolbar-title>Create Table Schema (and Import Data) from .csv</v-toolbar-title>
    </v-toolbar>
    <v-stepper v-model="step" vertical flat>
      <v-stepper-step :complete="step > 1" step="1">
        Table Information
      </v-stepper-step>

      <v-stepper-content class="pt-0 pb-1" step="1">
        <v-text-field
          v-model="tableCreate.name"
          required
          autocomplete="off"
          label="Name *" />
        <v-text-field
          v-model="tableCreate.description"
          required
          autocomplete="off"
          label="Description *" />
        <v-btn :disabled="!step1Valid" color="primary" @click="step = 2">
          Continue
        </v-btn>
      </v-stepper-content>

      <v-stepper-step :complete="step > 2" step="2">
        Metadata
      </v-stepper-step>

      <v-stepper-content step="2">
        <v-row dense>
          <v-col cols="8">
            <v-select
              v-model="tableCreate.separator"
              :rules="[rules.required]"
              :items="separators"
              required
              hint="Character separating the values"
              label="Separator *" />
          </v-col>
        </v-row>
        <v-row dense>
          <v-col cols="8">
            <v-text-field
              v-model="tableCreate.skip_lines"
              :rules="[rules.required, rules.positive]"
              type="number"
              required
              hint="Skip n lines from the top"
              label="Skip Lines *"
              placeholder="e.g. 0" />
          </v-col>
        </v-row>
        <v-row dense>
          <v-col cols="8">
            <v-text-field
              v-model="tableCreate.null_element"
              hint="Representation of 'no value present'"
              placeholder="e.g. NA"
              label="NULL Element" />
          </v-col>
        </v-row>
        <v-row dense>
          <v-col cols="8">
            <v-text-field
              v-model="tableCreate.true_element"
              label="True Element"
              hint="Representation of boolean 'true'"
              placeholder="e.g. 1, true, YES" />
          </v-col>
        </v-row>
        <v-row dense>
          <v-col cols="8">
            <v-text-field
              v-model="tableCreate.false_element"
              label="False Element"
              hint="Representation of boolean 'false'"
              placeholder="e.g. 0, false, NO" />
          </v-col>
        </v-row>
        <v-row dense>
          <v-col cols="6">
            <v-btn :disabled="!tableCreate.separator || !tableCreate.skip_lines" :loading="loading" color="primary" @click="step = 3">Next</v-btn>
          </v-col>
        </v-row>
      </v-stepper-content>

      <v-stepper-step :complete="step > 3" step="3">
        Import Data
      </v-stepper-step>
      <v-stepper-content step="3">
        <v-row dense>
          <v-col cols="4">
            <v-file-input
              v-model="file"
              accept="text/csv"
              show-size
              label="File Upload (.csv)" />
          </v-col>
          <v-col cols="4">
            <v-text-field
              v-model="url"
              disabled
              accept="text/csv"
              show-size
              hint="e.g. http://www.wienerlinien.at/ogd_realtime/doku/ogd/wienerlinien-ogd-verbindungen.csv"
              label="File URL (.csv)" />
          </v-col>
        </v-row>
        <v-row dense>
          <v-col cols="6">
            <v-btn :disabled="!file" :loading="loading" color="primary" @click="upload">Next</v-btn>
          </v-col>
        </v-row>
      </v-stepper-content>

      <v-stepper-step :complete="step > 4" step="4">
        Table Schema
      </v-stepper-step>
      <v-stepper-content step="4">
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
            <v-col cols="2" :hidden="c.type !== 'ENUM'">
              <v-select
                v-model="c.enum_values"
                :disabled="c.type !== 'ENUM'"
                :items="c.suggestions"
                :menu-props="{ maxHeight: '400' }"
                label="Enumeration"
                multiple />
            </v-col>
            <v-col cols="2" class="pl-10" :hidden="c.type !== 'DATE'">
              <v-select
                v-model="c.dfid"
                :disabled="c.type !== 'DATE'"
                :items="dateFormats"
                item-text="example"
                item-value="id" />
            </v-col>
            <v-col cols="auto" class="pl-10" :hidden="c.type !== 'STRING' || c.type !== 'VARCHAR'">
              <v-text-field v-model="c.check_expression" label="Check Expression" />
            </v-col>
            <v-col cols="auto" class="pl-2">
              <v-checkbox v-model="c.primary_key" label="Primary Key" @click="setOthers(c)" />
            </v-col>
            <v-col cols="auto" class="pl-10">
              <v-checkbox v-model="c.null_allowed" :disabled="c.primary_key" label="Null Allowed" />
            </v-col>
            <v-col cols="auto" class="pl-10">
              <v-checkbox v-model="c.unique" :hidden="c.primary_key" label="Unique" />
            </v-col>
            <v-col cols="auto" class="pl-10">
              <v-text-field v-model="c.foreign_key" hidden required label="Foreign Key" />
            </v-col>
            <v-col cols="auto" class="pl-10">
              <v-text-field v-model="c.references" hidden required label="References" />
            </v-col>
          </v-row>
        </div>

        <v-btn class="mt-2" color="primary" :loading="loading" @click="createTable">
          Continue
        </v-btn>
      </v-stepper-content>

      <v-stepper-step
        :complete="step > 5"
        step="5">
        Done
      </v-stepper-step>

      <v-stepper-content step="5">
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
      separators: [
        ',',
        ';',
        '-',
        '|',
        '$',
        '%',
        '#'
      ],
      items: [
        { text: 'Databases', href: '/container' },
        { text: `${this.$route.params.database_id}`, href: `/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/info` }
      ],
      rules: {
        required: value => !!value || 'Required',
        positive: value => value >= 0 || 'Positive number'
      },
      dateFormats: [],
      tableCreate: {
        name: null,
        description: null,
        columns: [],
        false_element: null,
        true_element: null,
        null_element: null,
        separator: ',',
        skip_lines: 0
      },
      loading: false,
      file: null,
      url: null,
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
      return this.tableCreate.name !== null && this.tableCreate.name.length > 0 && this.tableCreate.description !== null && this.tableCreate.description.length > 0
    },
    token () {
      return this.$store.state.token
    }
  },
  mounted () {
    this.loadDateFormats()
  },
  methods: {
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
        console.log(res.data)

        if (res.data.success) {
          this.tableCreate.columns = res.data.columns
          this.fileLocation = res.data.file.filename
          this.step = 4
          this.loading = false
          console.debug('upload csv', res.data)
        } else {
          console.error('Upload failed. Try removing the last / from the API url', res)
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
      column.null_allowed = false
      column.unique = true
    },
    async loadDateFormats () {
      const getUrl = `/api/container/${this.$route.params.container_id}`
      let getResult
      try {
        this.loading = true
        getResult = await this.$axios.get(getUrl)
        this.dateFormats = getResult.data.image.date_formats
        console.debug('retrieve image date formats', this.dateFormats)
        this.loading = false
      } catch (err) {
        this.loading = false
        console.error('retrieve image date formats failed', err)
      }
    },
    async createTable () {
      /* make enum values to array */
      this.tableCreate.columns.forEach((column) => {
        // validate `id` column: must be a PK
        if (column.name === 'id' && (!column.primary_key)) {
          this.$toast.error('Column `id` has to be a Primary Key')
          return
        }
        if (column.enum_values == null) {
          return
        }
        if (column.enum_values.length > 0) {
          column.enum_values = column.enum_values.split(',')
        }
      })
      const createUrl = `/api/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/table`
      let createResult
      try {
        this.loading = true
        createResult = await this.$axios.post(createUrl, this.tableCreate, {
          headers: { Authorization: `Bearer ${this.token}` }
        })
        this.newTableId = createResult.data.id
        console.debug('created table', createResult.data)
      } catch (err) {
        this.loading = false
        if (err.response.status === 409) {
          this.$toast.error('Table name already exists.')
        } else {
          this.$toast.error('Could not create table.')
        }
        console.error('create table failed', err)
        return
      }
      const insertUrl = `/api/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/table/${createResult.data.id}/data?location=${encodeURI('/tmp/' + this.fileLocation)}`
      let insertResult
      try {
        insertResult = await this.$axios.post(insertUrl, {
          headers: { Authorization: `Bearer ${this.token}` }
        })
        console.debug('inserted table', insertResult.data)
      } catch (err) {
        this.loading = false
        console.error('insert table failed', err)
        this.$toast.error('Could not insert csv into table.')
        return
      }
      this.loading = false
      this.step = 5
    }
  }
}
</script>

<style scoped>
</style>
