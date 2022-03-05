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
          <v-row v-if="queryId">
            <v-col>
              <p>Results</p>
              <v-data-table
                :headers="result.headers"
                :items="result.rows"
                :loading="loading"
                :options.sync="options"
                :server-items-length="total"
                class="elevation-1" />
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
import _ from 'lodash'

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
      options: {
        page: 1,
        itemsPerPage: 10
      },
      select: [],
      clauses: [],
      result: {
        headers: [],
        rows: []
      },
      total: 0,
      loading: false
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
    options (newVal, oldVal) {
      if (typeof oldVal.groupBy === 'undefined') {
        // initially, options do not have the groupBy field.
        // don't run the execute method twice, when a new query is created
        return
      }
      this.execute()
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
    async execute () {
      this.loading = true
      try {
        const data = {
          statement: this.query.sql,
          tables: [_.pick(this.table, ['id', 'name', 'internal_name'])],
          columns: [this.select.map(function (column) {
            return _.pick(column, ['id', 'name', 'internal_name'])
          })]
        }
        console.debug('send data', data)
        const urlParams = `page=${this.options.page - 1}&size=${this.options.itemsPerPage}`
        const res = await this.$axios.put(`/api/container/
${this.$route.params.container_id}/database/${this.databaseId}/query
${this.queryId ? `/${this.queryId}` : ''}
?${urlParams}`, data, {
          headers: this.headers
        })
        console.debug('query result', res)
        this.$toast.success('Successfully executed query')
        this.loading = false
        this.queryId = res.data.id
        this.result.headers = this.select.map((s) => {
          return { text: s.name, value: s.name, sortable: false }
        })
        this.result.rows = res.data.result
        this.total = res.data.resultNumber
      } catch (err) {
        console.error('query execute', err)
        this.$toast.error('Could not execute query')
        this.loading = false
      }
    },
    /*
    async save () {
      this.$refs.form.validate()
      const query = this.query.sql.replaceAll('`', '')
      this.loading = true
      try {
        const res = await this.$axios.post(`/api/container/${this.$route.params.container_id}/database/${this.databaseId}/query`, { statement: query }, {
          headers: this.headers
        })
        console.debug('query result', res)
        this.$toast.success('Successfully saved query')
        this.loading = false
        this.queryId = res.data.id
        this.$router.push(`/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/query/${this.queryId}`)
      } catch (err) {
        console.error('query save', err)
        this.$toast.error('Could not save query')
        this.loading = false
      }
    },
    */
    async buildQuery () {
      if (!this.table) {
        return
      }
      const url = '/server-middleware/query/build'
      const data = {
        table: this.table.internal_name,
        select: this.select.map(s => s.name),
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
