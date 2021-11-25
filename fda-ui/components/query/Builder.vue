<template>
  <div>
    <v-form
      ref="form"
      v-model="valid"
      lazy-validation>
      <v-toolbar flat>
        <v-toolbar-title>Create Query</v-toolbar-title>
        <v-spacer />
        <v-toolbar-title>
          <v-btn :disabled="!valid" @click="save">
            Save without execution
          </v-btn>
          <v-btn :disabled="!valid" color="primary" @click="execute">
            <v-icon left>mdi-run</v-icon>
            Execute
          </v-btn>
        </v-toolbar-title>
      </v-toolbar>
      <v-card flat>
        <v-card-text>
          <v-row class="mt-2">
            <v-col cols="6">
              <v-text-field
                v-model="title"
                :rules="[rules.required, rules.titleMin]"
                class="pa-0"
                label="Query Title"
                required />
            </v-col>
          </v-row>
          <v-row class="mt-2">
            <v-col cols="6">
              <v-select
                v-model="table"
                :rules="[rules.required]"
                :items="tables"
                item-text="name"
                return-object
                label="Table"
                @change="loadColumns" />
            </v-col>
            <v-col cols="6">
              <v-select
                v-model="select"
                :rules="[rules.required]"
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
              <v-data-table
                :headers="result.headers"
                :items="result.rows"
                :loading="loading"
                :items-per-page="30"
                class="elevation-1" />
            </v-col>
          </v-row>
          <v-row>
            <v-col>
              <v-btn v-if="queryId" color="primary" :to="`/databases/${databaseId}/queries/${queryId}`">
                <v-icon left>mdi-fingerprint</v-icon>
                Obtain Query DOI
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
      valid: false,
      table: null,
      tables: [],
      title: null,
      tableDetails: null,
      queryId: null,
      query: {
        sql: ''
      },
      select: [],
      clauses: [],
      result: {
        headers: [],
        rows: []
      },
      rules: {
        required: value => !!value || 'Required',
        titleMin: value => (value || '').length >= 10 || 'Minimum 10 characters'
      },
      loading: false
    }
  },
  computed: {
    selectItems () {
      const columns = this.tableDetails && this.tableDetails.columns
      return columns || []
    },
    columnNames () {
      return this.selectItems && this.selectItems.map(s => s.internalName)
    },
    databaseId () {
      return this.$route.params.database_id
    },
    tableId () {
      return this.table.id
    }
  },
  watch: {
    clauses: {
      deep: true,
      handler () {
        this.buildQuery()
      }
    }
  },
  async mounted () {
    // XXX same as in TableList
    try {
      const res = await this.$axios.get(
        `/api/database/${this.databaseId}/table`)
      this.tables = res.data
      console.debug('tables', this.tables)
    } catch (err) {
      this.$toast.error('Could not list table.')
    }
  },
  methods: {
    async execute () {
      this.$refs.form.validate()
      const query = this.query.sql.replaceAll('`', '')
      this.loading = true
      try {
        const res = await this.$axios.put(`/api/database/${this.databaseId}/store/table/${this.tableId}/execute`, {
          title: this.title,
          query
        })
        console.debug('query result', res)
        this.$toast.success('Successfully executed query')
        this.loading = false
        this.queryId = res.data.id
        this.result.headers = this.select.map((s) => {
          return { text: s.name, value: 'mdb_' + s.name, sortable: false }
        })
        this.result.rows = res.data.result
      } catch (err) {
        console.error('query execute', err)
        this.$toast.error('Could not execute query')
        this.loading = false
      }
    },
    async save () {
      this.$refs.form.validate()
      const query = this.query.sql.replaceAll('`', '')
      this.loading = true
      try {
        const res = await this.$axios.put(`/api/database/${this.databaseId}/store/table/${this.tableId}/save`, {
          Query: query
        })
        console.debug('query result', res)
        this.$toast.success('Successfully saved query')
        this.loading = false
        this.queryId = res.data.id
      } catch (err) {
        console.error('query save', err)
        this.$toast.error('Could not save query')
        this.loading = false
      }
    },
    async buildQuery () {
      if (!this.table) {
        return
      }
      const url = '/server-middleware/query/build'
      const data = {
        table: this.table.internalName,
        select: this.select.map(s => 'mdb_' + s.name),
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
        const res = await this.$axios.get(`/api/database/${this.databaseId}/table/${tableId}`)
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
