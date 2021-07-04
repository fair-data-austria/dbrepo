<template>
  <div>
    <h3>
      {{ tableName }}
    </h3>
    <v-data-table
      :headers="headers"
      :items="rows"
      :loading="loading"
      :items-per-page="10"
      class="elevation-1">
    </v-data-table>
  </div>
</template>
<script>
export default {
  name: 'TableListing',
  components: {
  },
  data () {
    return {
      loading: true,
      tableName: '',
      headers: [],
      rows: []
    }
  },
  mounted () {
    this.loadProperties()
    this.loadData()
  },
  methods: {
    async loadProperties () {
      try {
        const res = await this.$axios.get(`/api/tables/api/database/${this.$route.params.db_id}/table/${this.$route.params.table_id}`)
        this.tableName = res.data.name
        console.debug('headers', res.data.columns)
        this.headers = res.data.columns.map((c) => {
          return {
            value: c.internalName,
            text: c.name
          }
        })
      } catch (err) {
        this.$toast.error('Could not get table details.')
        this.loading = false
      }
    },
    async loadData () {
      try {
        const res = await this.$axios.get(`/api/tables/api/database/${this.$route.params.db_id}/table/${this.$route.params.table_id}/data`)
        this.rows = res.data.result
        console.debug('table data', res.data)
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
