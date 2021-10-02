<template>
  <form id="create_table" action="/" method="post" @submit="checkForm">
    <v-card class="pb-2">
      <v-card-title class="pb-0">
        Create Table
      </v-card-title>
      <v-card-text>
        <v-text-field
          v-model="name"
          label="Table Name"
          :rules="[v => !!v || $t('Required')]"
          required />
        <v-text-field
          v-model="description"
          label="Description" />
      </v-card-text>
      <v-card-text v-for="(c, idx) in columns" :key="idx" class="pa-3 mb-2">
        <v-row class="column pa-2 ml-1 mr-1">
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
              v-model="c.primaryKey"
              label="Primary Key"
              @click="setOthers(c)"
              @change="(x) => onChange(idx, x, 'primaryKey')" />
          </v-col>
          <v-col cols="1">
            <v-checkbox
              v-model="c.unique"
              label="Unique"
              :disabled="c.primaryKey"
              @change="(x) => onChange(idx, x, 'unique')" />
          </v-col>
          <v-col cols="2">
            <v-checkbox
              v-model="c.nullAllowed"
              label="NULL Allowed"
              :disabled="c.primaryKey"
              @change="(x) => onChange(idx, x, 'nullAllowed')" />
          </v-col>
          <v-spacer />
          <v-btn title="Remove column" outlined icon @click="removeColumn(idx)">
            <v-icon>mdi-minus</v-icon>
          </v-btn>
        </v-row>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn @click="addColumn">
          Add Column
        </v-btn>
        <v-btn :disabled="!canCreateTable" color="primary" @click="createTable">
          Create Table
        </v-btn>
      </v-card-actions>
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
    }
  },
  mounted () {
    this.addColumn()
  },
  methods: {
    setOthers (column) {
      column.nullAllowed = false
      column.unique = true
    },
    checkForm (e) {
      e.preventDefault()
    },
    onChange (idx, val, name) {
      const c = this.columns[idx]
      if (name === 'nullAllowed' && val === true) {
        if (c.primaryKey) {
          c.primaryKey = false
        }
      }
      if (name === 'primaryKey' && val === true) {
        if (c.nullAllowed) {
          c.nullAllowed = false
        }
      }
      this.columns[idx] = c
    },
    addColumn () {
      this.columns.push({
        // default column
        name: '',
        type: '',
        nullAllowed: true,
        primaryKey: false
      })
    },
    removeColumn (idx) {
      this.columns.splice(idx, 1)
    },
    canCreateTable () {
      if (this.name === null) { return false }
      if (this.description === '') { return false }
      if (!this.columns.length) { return false }
      for (let i = 0; i < this.columns.length; i++) {
        const col = this.columns[i]
        if (col.name === '') { return false }
        if (col.type === '') { return false }
      }
      return true
    },
    async createTable () {
      const data = {
        name: this.name,
        description: this.description,
        columns: this.columns
      }
      try {
        const res = await this.$axios.post(`/api/database/${this.databaseId}/table`, data)
        if (res.status === 201) {
          this.$toast.success('Table created.')
          // this.$root.$emit('table-create', res.data)
          const tableId = res.data.id
          await this.$router.push(`/databases/${this.databaseId}/tables/${tableId}`)
        } else {
          this.$toast.error(`Could not create table: status ${res.status}`)
        }
      } catch (err) {
        this.$toast.error('Could not create table.')
      }
    }
  }
}
</script>

<style>
.column {
  border: 1px solid #ccc;
  border-radius: 3px;
}
</style>
