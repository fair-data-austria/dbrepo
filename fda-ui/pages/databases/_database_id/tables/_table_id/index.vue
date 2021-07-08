<template>
  <div>
    <v-card>
      <v-card-title v-if="!loading">
        {{ table.name }}
      </v-card-title>
      <v-card-subtitle v-if="!loading">
        {{ table.description }}
      </v-card-subtitle>
      <v-data-table
        :headers="headers"
        :items="rows"
        :loading="loading"
        :items-per-page="30"
        class="elevation-1" />
    </v-card>
    <div class="mt-3">
      <v-chip
        class="mr-2"
        label>
        ‡ Primary Key
      </v-chip>
      <v-chip
        class="mr-2"
        label>
        † Unique Column
      </v-chip>
    </div>
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
      table: null,
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
        const res = await this.$axios.get(`/api/database/${this.$route.params.database_id}/table/${this.$route.params.table_id}`)
        this.table = res.data
        console.debug('headers', res.data.columns)
        this.headers = res.data.columns.map((c) => {
          return {
            value: c.internalName,
            text: this.columnAddition(c) + c.name
          }
        })
      } catch (err) {
        this.$toast.error('Could not get table details.')
        this.loading = false
      }
    },
    async loadData () {
      try {
        const res = await this.$axios.get(`/api/database/${this.$route.params.database_id}/table/${this.$route.params.table_id}/data`)
        this.rows = res.data.result
        console.debug('table data', res.data)
      } catch (err) {
        this.$toast.error('Could not load table data.')
      }
      this.loading = false
    },
    columnAddition (column) {
      if (column.isPrimaryKey) {
        return '‡ '
      }
      if (column.unique) {
        return '† '
      }
      return ''
    }
  }
}
</script>

<style>
</style>
