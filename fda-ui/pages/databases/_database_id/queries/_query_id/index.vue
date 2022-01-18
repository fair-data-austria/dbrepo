<template>
  <div>
    <DBToolbar v-model="$store.state.db" />
    <v-card>
      <v-card-title v-if="!loading">
        {{ query.title }}
      </v-card-title>
      <v-card-subtitle v-if="!loading">
        <span v-if="query.execution_timestamp != null">
          Executed {{ query.execution_timestamp }}, result hash <code>{{ query.result_hash }}</code>
        </span>
        <span v-if="query.execution_timestamp == null">
          Query was never executed
        </span>
      </v-card-subtitle>
      <v-data-table
        :headers="headers"
        :items="rows"
        :loading="loading"
        :items-per-page="30"
        class="elevation-1" />
    </v-card>
  </div>
</template>
<script>
export default {
  name: 'QueryShow',
  components: {
  },
  data () {
    return {
      query: {
        id: this.$route.params.query_id,
        title: null,
        description: null,
        query_hash: null,
        result_hash: null,
        result_number: null,
        doi: null,
        execution_timestamp: null,
        created: null
      },
      loading: true,
      table: null,
      headers: [],
      rows: []
    }
  },
  mounted () {
    this.loadMetadata()
    // this.reExecute()
  },
  methods: {
    async loadMetadata () {
      this.loading = true
      try {
        const res = await this.$axios.get(`/api/database/${this.$route.params.database_id}/query/${this.$route.params.query_id}`)
        console.debug('query', res.data)
        this.query = res.data
      } catch (err) {
        console.error('Could not load query', err)
        this.$toast.error('Could not load query')
        this.loading = false
      }
      this.loading = false
    },
    async reExecute () {
      this.loading = true
      try {
        const res = await this.$axios.put(`/api/database/${this.$route.params.database_id}/store/table/1/execute/${this.$route.params.query_id}`)
        this.headers = Object.keys(res.data.result[0]).map((c) => {
          return { text: c, value: c }
        })
        this.rows = res.data.result
        console.debug('query data', res.data)
        this.query = res.data
      } catch (err) {
        console.error('Could not load query data', err)
        this.$toast.error('Could not load query data')
        this.loading = false
      }
      this.loading = false
    }
  }
}
</script>

<style>
</style>
