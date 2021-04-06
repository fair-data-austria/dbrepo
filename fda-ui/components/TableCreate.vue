<template>
  <v-card>
    <v-card-title>
      Create Table
    </v-card-title>
    <v-card-subtitle>
      Table is not created until the "Create Table" button is pressed.
    </v-card-subtitle>
    <v-card-text>
      <v-btn @click="addColumn">
        Add Column
      </v-btn>
    </v-card-text>
    <v-card-text v-for="(c, idx) in columns" :key="idx" class="pa-3">
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
        <v-spacer />
        <v-btn title="Remove column" outlined icon @click="removeColumn(idx)">
          <v-icon>mdi-minus</v-icon>
        </v-btn>
      </v-row>
    </v-card-text>
    <v-card-actions>
      <v-spacer />
      <v-btn>
        Create Table
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>
export default {
  data () {
    return {
      columns: [],
      columnTypes: [
        { value: 'varchar', text: 'VARCHAR' }
      ]
    }
  },
  methods: {
    addColumn () {
      this.columns.push({
        // default column
        name: 'myColumn',
        type: 'varchar'
      })
    },
    removeColumn (idx) {
      this.columns.splice(idx, 1)
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
