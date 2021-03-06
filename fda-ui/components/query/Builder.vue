<template>
  <div>
    <v-form ref="form">
      <v-toolbar flat>
        <v-toolbar-title>Create Query</v-toolbar-title>
        <v-spacer />
        <v-toolbar-title>
          <v-btn v-if="false" :disabled="!valid || !token" color="blue-grey white--text" @click="save">
            Save without execution
          </v-btn>
          <v-btn :disabled="!valid || !token" color="primary" @click="execute">
            <v-icon left>mdi-run</v-icon>
            Execute
          </v-btn>
        </v-toolbar-title>
      </v-toolbar>
      <v-card flat>
        <v-card-text>
          <v-row>
            <v-col cols="6">
              <v-select
                v-model="table"
                :items="tables"
                item-text="name"
                return-object
                label="Table"
                @change="loadColumns" />
            </v-col>
            <v-col cols="6">
              <v-select
                v-model="select"
                item-text="name"
                :disabled="!table"
                :items="selectItems"
                label="Columns"
                return-object
                multiple
                @change="buildQuery" />
            </v-col>
          </v-row>
          <QueryFilters
            v-if="table"
            v-model="clauses"
            :columns="columnNames" />
          <v-row v-if="query.formatted">
            <v-col>
              <highlightjs autodetect :code="query.formatted" />
            </v-col>
          </v-row>
          <v-row>
            <v-col>
              <p>Results</p>
              <QueryResults ref="queryResults" v-model="queryId" />
            </v-col>
          </v-row>
          <v-row>
            <v-col>
              <v-btn v-if="queryId" color="blue-grey white--text" :to="`/container/${$route.params.container_id}/database/${databaseId}/query/${queryId}`">
                More
              </v-btn>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>
    </v-form>
  </div>
</template>

<script>
export default {
  data () {
    return {
      table: null,
      tables: [],
      tableDetails: null,
      queryId: null,
      query: {
        sql: ''
      },
      select: [],
      clauses: []
    }
  },
  computed: {
    selectItems () {
      const columns = this.tableDetails && this.tableDetails.columns
      return columns || []
    },
    columnNames () {
      return this.selectItems && this.selectItems.map(s => s.internal_name)
    },
    databaseId () {
      return this.$route.params.database_id
    },
    tableId () {
      return this.table.id
    },
    token () {
      return this.$store.state.token
    },
    headers () {
      if (this.token === null) {
        return null
      }
      return { Authorization: `Bearer ${this.token}` }
    },
    valid () {
      // we need to have at least one column selected
      return this.select.length
    }
  },
  watch: {
    clauses: {
      deep: true,
      handler () {
        this.buildQuery()
        this.queryId = null
      }
    },
    table () {
      this.queryId = null
    },
    select () {
      this.queryId = null
    }
  },
  beforeMount () {
    this.loadTables()
  },
  methods: {
    async loadTables () {
      try {
        const res = await this.$axios.get(`/api/container/${this.$route.params.container_id}/database/${this.databaseId}/table`, {
          headers: this.headers
        })
        this.tables = res.data
        console.debug('tables', this.tables)
      } catch (err) {
        this.$toast.error('Could not list table.')
      }
    },
    execute () {
      this.$refs.queryResults.executeFirstTime(this)
    },
    async buildQuery () {
      if (!this.table) {
        return
      }
      const url = '/server-middleware/query/build'
      const data = {
        table: this.table.internal_name,
        select: this.select.map(s => s.internal_name),
        clauses: this.clauses
      }
      try {
        const res = await this.$axios.post(url, data)
        if (res && !res.error) {
          this.query = res.data
        }
      } catch (e) {
        console.log(e)
      }
    },
    async loadColumns () {
      const tableId = this.table.id
      try {
        const res = await this.$axios.get(`/api/container/${this.$route.params.container_id}/database/${this.databaseId}/table/${tableId}`, {
          headers: this.headers
        })
        this.tableDetails = res.data
        this.buildQuery()
      } catch (err) {
        this.$toast.error('Could not get table details.')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
/* these are taked from solarized-light (plugins/vendors.js), to override the
main.scss file from vuetify, because it paints it red */
::v-deep code {
  background: #fdf6e3;
  color: #657b83;
}

</style>
