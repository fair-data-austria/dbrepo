<template>
  <v-data-table
    :headers="result.headers"
    :items="result.rows"
    :loading="loading"
    :options.sync="options"
    :server-items-length="total"
    class="elevation-1" />
</template>

<script>
import _ from 'lodash'

export default {
  data () {
    return {
      parent: null,
      loading: false,
      result: {
        headers: [],
        rows: []
      },
      options: {
        page: 1,
        itemsPerPage: 10
      },
      total: 0
    }
  },
  computed: {
    token () {
      return this.$store.state.token
    },
    headers () {
      if (this.token === null) {
        return null
      }
      return { Authorization: `Bearer ${this.token}` }
    }
  },
  watch: {
    value () {
      this.execute()
    },
    options (newVal, oldVal) {
      if (typeof oldVal.groupBy === 'undefined') {
        // initially, options do not have the groupBy field.
        // don't run the execute method twice, when a new query is created
        return
      }
      this.execute()
    }
  },
  mounted () {
  },
  methods: {
    async execute (parent) {
      if (parent) {
        this.parent = parent
      }

      this.loading = true
      try {
        const data = {
          statement: this.parent.query.sql,
          tables: [_.pick(this.parent.table, ['id', 'name', 'internal_name'])],
          columns: [this.parent.select.map(function (column) {
            return _.pick(column, ['id', 'name', 'internal_name'])
          })]
        }
        console.debug('send data', data)
        let page = this.options.page - 1
        if (!this.parent.queryId) {
          page = 0
        }
        const urlParams = `page=${page}&size=${this.options.itemsPerPage}`
        const res = await this.$axios.put(`/api/container/
${this.$route.params.container_id}/database/${this.$route.params.database_id}/query
${this.parent.queryId ? `/${this.parent.queryId}` : ''}
?${urlParams}`, data, {
          headers: this.headers
        })
        console.debug('query result', res)
        this.$toast.success('Successfully executed query')
        this.loading = false
        this.parent.queryId = res.data.id
        this.result.headers = this.parent.select.map((s) => {
          return { text: s.name, value: s.name, sortable: false }
        })
        this.result.rows = res.data.result
        this.total = res.data.resultNumber
      } catch (err) {
        console.error('query execute', err)
        this.$toast.error('Could not execute query')
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
</style>
