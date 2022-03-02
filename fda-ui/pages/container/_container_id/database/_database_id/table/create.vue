<template>
  <div>
    <v-progress-linear v-if="loading" :color="loadingColor" :indeterminate="!error" />
    <v-toolbar flat>
      <v-toolbar-title>
        <span>Create table schema</span>
      </v-toolbar-title>
    </v-toolbar>
    <v-stepper v-model="step" vertical flat>
      <v-stepper-step :complete="step > 1" step="1">
        Table Information
      </v-stepper-step>

      <v-stepper-content class="pt-0 pb-1" step="1">
        <v-form ref="form" v-model="valid" @submit.prevent="submit">
          <v-row dense>
            <v-col cols="8">
              <v-text-field
                v-model="tableCreate.name"
                name="name"
                autocomplete="off"
                label="Name *"
                :rules="[v => !!v || $t('Required')]"
                required />
            </v-col>
          </v-row>
          <v-row dense>
            <v-col cols="8">
              <v-text-field
                v-model="tableCreate.description"
                name="description"
                autocomplete="off"
                label="Description *" />
            </v-col>
          </v-row>
          <v-row dense>
            <v-col cols="8">
              <v-btn :disabled="!step1Valid" color="primary" type="submit" @click="step = 2">
                Continue
              </v-btn>
            </v-col>
          </v-row>
        </v-form>
      </v-stepper-content>

      <v-stepper-step :complete="step > 2" step="2">
        Table Schema
      </v-stepper-step>

      <v-stepper-content class="pt-0 pb-1" step="2">
        <v-form ref="form" v-model="valid" @submit.prevent="submit">
          <div v-for="(c, idx) in tableCreate.columns" :key="idx">
            <v-row dense class="column pa-2 mb-2">
              <v-col cols="2">
                <v-text-field
                  v-model="c.name"
                  required
                  label="Name"
                  :rules="[v => !!v || $t('Required')]" />
              </v-col>
              <v-col cols="2">
                <v-select
                  v-model="c.type"
                  :items="columnTypes"
                  item-value="value"
                  required
                  label="Data Type" />
              </v-col>
              <v-col cols="auto" class="pl-10" :hidden="c.type !== 'DECIMAL'">
                <v-text-field
                  v-model="c.decimal_digits_before"
                  label="Digits before decimal"
                  type="number"
                  value="10"
                  required
                  hint="e.g. 4 for 1111.11"
                  :rules="[v => !!v || $t('Required')]" />
              </v-col>
              <v-col cols="auto" class="pl-10" :hidden="c.type !== 'DECIMAL'">
                <v-text-field
                  v-model="c.decimal_digits_after"
                  label="Digits after decimal"
                  type="number"
                  value="2"
                  required
                  hint="e.g. 2 for 1111.11"
                  :rules="[v => !!v || $t('Required')]" />
              </v-col>
              <v-col cols="2" :hidden="c.type !== 'ENUM'">
                <v-select
                  v-model="c.enum_values"
                  :disabled="c.type !== 'ENUM'"
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
                <v-checkbox v-model="c.unique" :disabled="c.primary_key" label="Unique" />
              </v-col>
              <v-col cols="auto" class="pl-10" hidden>
                <v-text-field v-model="c.foreign_key" required label="Foreign Key" />
              </v-col>
              <v-col cols="auto" class="pl-10" hidden>
                <v-text-field v-model="c.references" required label="References" />
              </v-col>
              <v-col cols="auto">
                <v-btn class="mt-2 float-right" @click.stop="removeColumn(idx)">Remove</v-btn>
              </v-col>
            </v-row>
          </div>
          <div>
            <v-btn @click="addColumn()">
              <v-icon left aria-label="add">mdi-plus</v-icon> Column
            </v-btn>
          </div>
          <div>
            <v-btn class="mt-10" :loading="loading" :disabled="!canCreateTable" color="primary" @click="createTable">
              Create Table
            </v-btn>
          </div>
        </v-form>
      </v-stepper-content>
    </v-stepper>
  </div>
</template>

<script>
export default {
  data () {
    return {
      columns: [],
      name: null,
      valid: false,
      description: null,
      dateFormats: [],
      loading: false,
      step: 1,
      error: false,
      columnTypes: [
        { value: 'ENUM', text: 'Enumeration' },
        { value: 'BOOLEAN', text: 'Boolean' },
        { value: 'NUMBER', text: 'Number' },
        { value: 'DECIMAL', text: 'Decimal' },
        { value: 'BLOB', text: 'Binary Large Object' },
        { value: 'DATE', text: 'Date' },
        { value: 'STRING', text: 'Character Varying' },
        { value: 'TEXT', text: 'Text' }
      ],
      tableCreate: {
        name: null,
        description: null,
        false_element: null,
        true_element: null,
        null_element: null,
        columns: [],
        separator: ',',
        skip_lines: '0'
      }
    }
  },
  computed: {
    databaseId () {
      return this.$route.params.database_id
    },
    step1Valid () {
      return this.tableCreate.name !== null && this.tableCreate.name.length > 0 && this.tableCreate.description !== null && this.tableCreate.description.length > 0
    },
    token () {
      return this.$store.state.token
    },
    loadingColor () {
      return this.error ? 'red lighten-2' : 'primary'
    },
    canCreateTable () {
      if (!this.step1Valid || this.step !== 2) {
        return false
      }
      return this.tableCreate.columns.length >= 1
    }
  },
  mounted () {
    this.loadDateFormats()
    this.addColumn('id', 'NUMBER', false, true, true)
  },
  methods: {
    submit () {
      this.$refs.form.validate()
    },
    setOthers (column) {
      column.null_allowed = false
      column.unique = true
    },
    checkForm (e) {
      e.preventDefault()
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
    onChange (idx, val, name) {
      const c = this.tableCreate.columns[idx]
      if (name === 'null_allowed' && val === true) {
        if (c.primary_key) {
          c.primary_key = false
        }
      }
      if (name === 'primary_key' && val === true) {
        if (c.null_allowed) {
          c.null_allowed = false
        }
      }
      this.tableCreate.columns[idx] = c
    },
    addColumn (name = '', type = '', null_allowed = true, primary_key = false, unique = true) {
      this.tableCreate.columns.push({
        name,
        type,
        null_allowed,
        primary_key,
        check_expression: null,
        date_format: null,
        foreign_key: null,
        references: null,
        unique
      })
    },
    removeColumn (idx) {
      this.tableCreate.columns.splice(idx, 1)
    },
    async createTable () {
      try {
        this.loading = true
        const res = await this.$axios.post(`/api/container/${this.$route.params.container_id}/database/${this.databaseId}/table`, this.tableCreate, {
          headers: { Authorization: `Bearer ${this.token}` }
        })
        if (res.status === 201) {
          this.error = false
          this.$toast.success('Table created.')
          this.$root.$emit('table-create', res.data)
          await this.$router.push(`/container/${this.$route.params.container_id}/database/${this.databaseId}/table/${res.data.id}`)
        } else {
          this.error = true
          this.$toast.error(`Could not create table: status ${res.status}`)
        }
        this.loading = false
      } catch (err) {
        this.error = true
        console.error('could not create table', err)
        this.$toast.error('Could not create table.')
        this.loading = false
      }
    }
  }
}
</script>

<style>
.row-border {
  border: 1px solid #ccc;
  border-radius: 3px;
  margin: 0 !important;
}
</style>
