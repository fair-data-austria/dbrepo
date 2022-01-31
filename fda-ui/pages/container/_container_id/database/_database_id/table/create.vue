<template>
  <form id="create_table" action="/" method="post" @submit="checkForm">
    <v-progress-linear v-if="loading" :color="loadingColor" :indeterminate="!error" />
    <v-toolbar flat>
      <v-toolbar-title>
        <span>Create Table</span>
      </v-toolbar-title>
      <v-spacer />
      <v-toolbar-title>
        <v-btn :disabled="!canCreateTable" color="primary" @click="createTable">
          Create Table
        </v-btn>
      </v-toolbar-title>
    </v-toolbar>
    <v-card flat>
      <v-card-text>
        <v-text-field
          v-model="name"
          name="name"
          label="Table Name"
          :rules="[v => !!v || $t('Required')]"
          required />
        <v-text-field
          v-model="description"
          name="description"
          label="Description" />
      </v-card-text>
      <v-card-text>
        <v-row v-for="(c, idx) in columns" :key="idx" class="row-border mt-2">
          <v-col cols="3">
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
          <v-col cols="2">
            <v-checkbox
              v-model="c.primary_key"
              label="Primary Key"
              @click="setOthers(c)"
              @change="(x) => onChange(idx, x, 'primary_key')" />
          </v-col>
          <v-col cols="1">
            <v-checkbox
              v-model="c.unique"
              label="Unique"
              :disabled="c.primary_key"
              @change="(x) => onChange(idx, x, 'unique')" />
          </v-col>
          <v-col cols="2">
            <v-checkbox
              v-model="c.null_allowed"
              label="NULL Allowed"
              :disabled="c.primary_key"
              @change="(x) => onChange(idx, x, 'null_Allowed')" />
          </v-col>
          <v-spacer />
          <v-btn title="Remove column" outlined icon @click="removeColumn(idx)">
            <v-icon>mdi-minus</v-icon>
          </v-btn>
        </v-row>
        <v-row>
          <v-col>
            <v-btn @click="addColumn">
              Add Column
            </v-btn>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
  </form>
</template>

<script>
export default {
  data () {
    return {
      columns: [],
      name: null,
      description: null,
      loading: false,
      error: false,
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
  computed: {
    databaseId () {
      return this.$route.params.database_id
    },
    loadingColor () {
      return this.error ? 'red lighten-2' : 'primary'
    },
    canCreateTable () {
      if (this.name === null) { return false }
      if (this.description === '') { return false }
      if (!this.columns.length) { return false }
      for (let i = 0; i < this.columns.length; i++) {
        const col = this.columns[i]
        if (col.name === '') { return false }
        if (col.type === '') { return false }
        if (col.name === 'id' && (!col.primary_key)) {
          return false
        }
      }
      return true
    }
  },
  mounted () {
    this.addColumn('id', 'NUMBER', false, true, true)
  },
  methods: {
    setOthers (column) {
      column.null_allowed = false
      column.unique = true
    },
    checkForm (e) {
      e.preventDefault()
    },
    onChange (idx, val, name) {
      const c = this.columns[idx]
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
      this.columns[idx] = c
    },
    addColumn (name = '', type = '', null_allowed = true, primary_key = false, unique = true) {
      this.columns.push({
        // default column
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
      this.columns.splice(idx, 1)
    },
    async createTable () {
      const data = {
        name: this.name,
        description: this.description,
        columns: this.columns
      }
      try {
        this.loading = true
        const res = await this.$axios.post(`/api/container/${this.$route.params.container_id}/database/${this.databaseId}/table`, data)
        if (res.status === 201) {
          this.error = false
          this.$toast.success('Table created.')
          this.$root.$emit('table-create', res.data)
          await this.$router.push(`/container/${this.$route.params.container_id}/database/${this.databaseId}/table/${res.data.id}`)
        } else {
          this.error = true
          this.$toast.error(`Could not create table: status ${res.status}`)
        }
      } catch (err) {
        this.error = true
        console.error('could not create table', err)
        this.$toast.error('Could not create table.')
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
