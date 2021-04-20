<template>
  <div>
    <v-expansion-panels v-model="panelIndex" accordion>
      <v-expansion-panel v-for="(item,i) in tables" :key="i">
        <v-expansion-panel-header>
          {{ item.name }}
        </v-expansion-panel-header>
        <v-expansion-panel-content>
          <v-row dense>
            <v-col>
              <v-btn :to="`/db/${$route.params.db_id}/tables/${item.id}`" outlined>
                <v-icon>mdi-table</v-icon>
                View
              </v-btn>
              <v-btn :to="`/db/${$route.params.db_id}/tables/${item.id}/import_csv`" outlined>
                Import CSV
              </v-btn>
            </v-col>
            <v-col>
              ID: {{ item.id }}<br>
              Internal Name: {{ item.internalName }}<br>
            </v-col>
            <v-col class="align-right">
              <v-btn outlined color="error" @click="showDeleteTableDialog(item.id)">
                Delete
              </v-btn>
            </v-col>
          </v-row>
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

    <v-dialog v-model="dialogDelete" max-width="640">
      <v-card>
        <v-card-title class="headline">
          Delete
        </v-card-title>
        <v-card-text class="pb-1">
          Are you sure you want to drop this table?
        </v-card-text>
        <v-card-actions class="pl-4 pb-4 pr-4">
          <v-btn @click="dialogDelete = false">
            Cancel
          </v-btn>
          <v-spacer />
          <v-btn color="error" @click="deleteTable()">
            Delete
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
export default {
  data () {
    return {
      tables: [],
      panelIndex: undefined,
      tableDetails: null,
      dialogDelete: false,
      deleteTableId: -1
    }
  },
  watch: {
    async panelIndex () {
      if (typeof this.panelIndex !== 'undefined') {
        const tableId = this.tables[this.panelIndex].id
        try {
          const res = await this.$axios.get(`/api/tables/api/database/${this.$route.params.db_id}/table/${tableId}`)
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
        res = await this.$axios.get(
          `/api/tables/api/database/${this.$route.params.db_id}/table`)
        this.tables = res.data
      } catch (err) {
        this.$toast.error('Could not list tables.')
      }
    },
    async deleteTable () {
      try {
        await this.$axios.delete(
          `/api/tables/api/database/${this.$route.params.db_id}/table/${this.deleteTableId}`)
        this.refresh()
      } catch (err) {
        this.$toast.error('Could not delete table.')
      }
      this.dialogDelete = false
    },
    showDeleteTableDialog (id) {
      this.deleteTableId = id
      this.dialogDelete = true
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
.align-right {
  text-align: right;
}
</style>
