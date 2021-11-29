<template>
  <div>
    <v-card v-if="!loading && tables.length === 0" flat>
      <v-card-title>
        (no tables)
      </v-card-title>
    </v-card>
    <v-expansion-panels v-if="!loading && tables.length > 0" accordion>
      <v-expansion-panel v-for="(item,i) in tables" :key="i" @click="details(item)">
        <v-expansion-panel-header>
          {{ item.name }}
        </v-expansion-panel-header>
        <v-expansion-panel-content>
          <v-row dense>
            <v-col>
              <v-list dense>
                <v-list-item>
                  <v-list-item-icon>
                    <v-icon>mdi-fingerprint</v-icon>
                  </v-list-item-icon>
                  <v-list-item-content>
                    <v-list-item-title>
                      ID: {{ tableDetails.id }}
                    </v-list-item-title>
                  </v-list-item-content>
                </v-list-item>
                <v-list-item>
                  <v-list-item-icon>
                    <v-icon>mdi-information-variant</v-icon>
                  </v-list-item-icon>
                  <v-list-item-content>
                    <v-list-item-title>
                      Internal Name: <code>{{ tableDetails.internalName }}</code>
                    </v-list-item-title>
                  </v-list-item-content>
                </v-list-item>
                <v-list-item>
                  <v-list-item-icon>
                    <v-icon>mdi-road-variant</v-icon>
                  </v-list-item-icon>
                  <v-list-item-content>
                    <v-list-item-title>
                      AMQP Routing Key: <code>{{ tableDetails.topic }}</code>
                    </v-list-item-title>
                  </v-list-item-content>
                </v-list-item>
                <v-list-item>
                  <v-list-item-icon>
                    <v-icon>mdi-notebook-outline</v-icon>
                  </v-list-item-icon>
                  <v-list-item-content>
                    <v-list-item-title>
                      Description: {{ tableDetails.description }}
                    </v-list-item-title>
                  </v-list-item-content>
                </v-list-item>
              </v-list>
            </v-col>
          </v-row>
          <v-row dense>
            <v-col>
              <v-btn :to="`/databases/${$route.params.database_id}/tables/${item.id}`" outlined>
                <v-icon>mdi-table</v-icon>
                View
              </v-btn>
              <v-btn :to="`/databases/${$route.params.database_id}/tables/${item.id}/import`" outlined>
                Import CSV
              </v-btn>
            </v-col>
            <v-col class="align-right">
              <v-btn outlined color="error" @click="showDeleteTableDialog(item.id)">
                Delete
              </v-btn>
            </v-col>
          </v-row>
          <v-row v-if="tableDetails.columns" dense>
            <v-col>
              <v-simple-table class="colTable">
                <thead>
                  <th>Column Name</th>
                  <th>Type</th>
                  <th>Primary Key</th>
                  <th>Unique</th>
                  <th>NULL Allowed</th>
                </thead>
                <tbody>
                  <tr v-for="(col, idx) in tableDetails.columns" :key="idx">
                    <td>
                      {{ col.name }}
                    </td>
                    <td>
                      {{ col.columnType }}
                    </td>
                    <td>
                      <v-simple-checkbox v-model="col.isPrimaryKey" disabled aria-readonly="true" />
                    </td>
                    <td>
                      <v-simple-checkbox v-model="col.unique" disabled aria-readonly="true" />
                    </td>
                    <td>
                      <v-simple-checkbox v-model="col.isNullAllowed" disabled aria-readonly="true" />
                    </td>
                  </tr>
                </tbody>
              </v-simple-table>
            </v-col>
          </v-row>
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
      loading: false,
      tables: [],
      tableDetails: { id: 0 },
      dialogDelete: false
    }
  },
  mounted () {
    this.$root.$on('table-create', this.refresh)
    this.refresh()
  },
  methods: {
    async details (table) {
      if (this.tableDetails.id === table.id) {
        return
      }
      try {
        const res = await this.$axios.get(`/api/database/${this.$route.params.database_id}/table/${table.id}`)
        console.debug('table', res.data)
        this.tableDetails = res.data
      } catch (err) {
        this.tableDetails = undefined
        this.$toast.error('Could not get table details.')
      }
    },
    async refresh () {
      // XXX same as in QueryBuilder
      let res
      try {
        this.loading = true
        res = await this.$axios.get(`/api/database/${this.$route.params.database_id}/table`)
        this.tables = res.data
        this.loading = false
      } catch (err) {
        this.$toast.error('Could not list table.')
      }
    },
    async deleteTable () {
      try {
        this.loading = true
        await this.$axios.delete(`/api/database/${this.$route.params.database_id}/table/${this.deleteTableId}`)
        this.loading = false
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
