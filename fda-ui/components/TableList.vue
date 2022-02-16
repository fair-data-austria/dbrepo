<template>
  <div>
    <v-progress-linear v-if="loading" :indeterminate="!error" />
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
                      ID
                    </v-list-item-title>
                    <v-list-item-content>
                      {{ tableDetails.id }}
                    </v-list-item-content>
                  </v-list-item-content>
                </v-list-item>
                <v-list-item>
                  <v-list-item-icon>
                    <v-icon>mdi-information-variant</v-icon>
                  </v-list-item-icon>
                  <v-list-item-content>
                    <v-list-item-title>
                      Internal Name
                    </v-list-item-title>
                    <v-list-item-content>
                      {{ tableDetails.internal_name }}
                    </v-list-item-content>
                  </v-list-item-content>
                </v-list-item>
                <v-list-item>
                  <v-list-item-icon>
                    <v-icon>mdi-road-variant</v-icon>
                  </v-list-item-icon>
                  <v-list-item-content>
                    <v-list-item-title>
                      AMQP Routing Key
                    </v-list-item-title>
                    <v-list-item-content>
                      {{ tableDetails.topic }}
                    </v-list-item-content>
                  </v-list-item-content>
                </v-list-item>
                <v-list-item>
                  <v-list-item-icon>
                    <v-icon>mdi-table</v-icon>
                  </v-list-item-icon>
                  <v-list-item-content>
                    <v-list-item-title>
                      Columns
                    </v-list-item-title>
                    <v-list-item-content>
                      {{ tableDetails.columns.length }}
                    </v-list-item-content>
                  </v-list-item-content>
                </v-list-item>
                <v-list-item>
                  <v-list-item-icon>
                    <v-icon>mdi-notebook-outline</v-icon>
                  </v-list-item-icon>
                  <v-list-item-content>
                    <v-list-item-title>
                      Description
                    </v-list-item-title>
                    <v-list-item-content>
                      {{ tableDetails.description }}
                    </v-list-item-content>
                  </v-list-item-content>
                </v-list-item>
              </v-list>
            </v-col>
          </v-row>
          <v-row dense>
            <v-col>
              <v-btn color="blue-grey" class="white--text" :to="`/container/${$route.params.container_id}/database/${$route.params.database_id}/table/${item.id}`">
                More
              </v-btn>
            </v-col>
            <v-col class="align-right">
              <v-btn outlined color="error" @click="showDeleteTableDialog(item.id)">
                Delete
              </v-btn>
            </v-col>
          </v-row>
          <v-row v-if="tableDetails.columns">
            <v-col>
              <v-simple-table class="colTable">
                <thead>
                  <th>Column Name</th>
                  <th>Type</th>
                  <th>Unit</th>
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
                      {{ col.column_type }}
                    </td>
                    <td>
                      <DialogsColumnUnit :column="col" />
                    </td>
                    <td>
                      <v-simple-checkbox v-model="col.is_primary_key" disabled aria-readonly="true" />
                    </td>
                    <td>
                      <v-simple-checkbox v-model="col.unique" disabled aria-readonly="true" />
                    </td>
                    <td>
                      <v-simple-checkbox v-model="col.is_null_allowed" disabled aria-readonly="true" />
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
      tableDetails: {
        id: null,
        internal_name: null,
        description: null,
        topic: null,
        columns: []
      },
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
        const res = await this.$axios.get(`/api/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/table/${table.id}`)
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
        res = await this.$axios.get(`/api/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/table`)
        this.tables = res.data
        this.loading = false
      } catch (err) {
        this.$toast.error('Could not list table.')
      }
    },
    async deleteTable () {
      try {
        this.loading = true
        await this.$axios.delete(`/api/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/table/${this.deleteTableId}`)
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
