<template>
  <div>
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
        <v-btn @click="addColumn">
          Add Column
        </v-btn>
      </v-card-text>
      <v-card-text v-for="(c, idx) in columns" :key="idx" class="pa-3 mb-2">
        <v-row class="column pa-2 ml-1 mr-1">
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
          <v-col cols="2">
            <v-checkbox
              v-model="c.primaryKey"
              label="Primary Key"
              @change="(x) => onChange(idx, x, 'primaryKey')" />
          </v-col>
          <v-col cols="2">
            <v-checkbox
              v-model="c.nullAllowed"
              label="Null Allowed"
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
        <v-btn :disabled="!canCreateTable()" @click="createTable">
          Create Table
        </v-btn>
      </v-card-actions>
    </v-card>
  </div>
</template>

<script>
export default {
  data () {
    return {
      columns: [],
      name: '',
      description: '',
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
    this.addColumn()
  },
  methods: {
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
      if (this.name === '') { return false }
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
        const res = await this.$axios.post(`/api/tables/api/database/${this.$route.params.db_id}/table`, data)
        if (res.status === 201) {
          this.$toast.success('Table created.')
          this.$root.$emit('table-create', res.data)
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
