<template>
  <v-expansion-panels v-model="panelIndex" accordion>
    <v-expansion-panel v-for="(item,i) in tables" :key="i">
      <v-expansion-panel-header>{{ item.name }}</v-expansion-panel-header>
      <v-expansion-panel-content>
        ID: {{ item.id }}<br>
        Internal Name: {{ item.internalName }}<br>
        <div v-if="tableDetails">
          Description: {{ tableDetails.description }}<br>
          <v-simple-table class="colTable">
            <thead>
              <th>Column Name</th>
              <th>Type</th>
              <th>Primary Key</th>
              <th>Null Allowed</th>
            </thead>
            <tbody>
              <tr v-for="(col, idx) in tableDetails.columns" :key="idx">
                <td class="pl-0">{{ col.name }}</td>
                <td class="pl-0">{{ col.columnType }}</td>
                <td class="pl-0"><v-simple-checkbox v-model="col.isPrimaryKey" disabled /></td>
                <td class="pl-0"><v-simple-checkbox v-model="col.isNullAllowed" disabled /></td>
              </tr>
            </tbody>
          </v-simple-table>
        </div>
      </v-expansion-panel-content>
    </v-expansion-panel>
  </v-expansion-panels>
</template>

<script>
export default {
  data () {
    return {
      tables: [],
      panelIndex: undefined,
      tableDetails: null
    }
  },
  watch: {
    async panelIndex () {
      if (typeof this.panelIndex !== 'undefined') {
        const tableId = this.tables[this.panelIndex].id
        try {
          const res = await this.$axios.get(`http://localhost:9094/api/database/${this.$route.params.db_id}/table/${tableId}`)
          this.tableDetails = res.data[0] // It's a list with one element
        } catch (err) {
          this.$toast.error('Could not get table details.')
        }
      } else {
        this.tableDetails = null
      }
    }
  },
  mounted () {
    this.$root.$on('table-create', this.refresh)
    this.refresh()
  },
  methods: {
    async refresh () {
      let res
      try {
        res = await this.$axios.get(`http://localhost:9094/api/database/${this.$route.params.db_id}/table`)
        this.tables = res.data
      } catch (err) {
        this.$toast.error('Could not list tables.')
      }
    }
  }
}
</script>

<style>
.colTable thead th {
  text-align: initial;
}
.colTable tbody tr td {
  padding-left: 0;
}
</style>
