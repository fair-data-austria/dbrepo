<template>
  <div>
    <DBToolbar v-model="$store.state.db" />
    <v-card>
      <v-card-title v-if="!loading">
        Result of Query #{{ id }}
      </v-card-title>
      <v-card-subtitle v-if="!loading">
        <code v-if="hash">{{ hash }}</code>
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
      id: this.$route.params.query_id,
      hash: null,
      loading: true,
      table: null,
      headers: [],
      rows: []
    }
  },
  mounted () {
    this.loadData()
  },
  methods: {
    async loadData () {
      try {
        const res = await this.$axios.get(`/api/database/${this.$route.params.database_id}/query/${this.$route.params.query_id}`)
        this.headers = Object.keys(res.data.result[0]).map((c) => {
          return { text: c, value: c }
        })
        this.rows = res.data.result
        console.debug('query data', res.data)
      } catch (err) {
        this.$toast.error('Could not load table data.')
      }
      this.loading = false
    }
  }
}
</script>

<style>
</style>
