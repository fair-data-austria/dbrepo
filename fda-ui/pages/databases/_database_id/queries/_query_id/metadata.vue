<template>
  <div>
    <v-card>
      <v-card-title v-if="!loading">
        Cite dataset for query #{{ queryId }}
      </v-card-title>
      <v-card-subtitle v-if="!loading">
        Executed {{ query.execution_timestamp | date }}
      </v-card-subtitle>
      <v-card-text>
        <pre>{{ query.query }}</pre>
        TODO, metadata needed for doi
      </v-card-text>
    </v-card>
  </div>
</template>
<script>
export default {
  name: 'QueryDoiMetadata',
  components: {
  },
  data () {
    return {
      loading: false,
      query: {
        hash: null,
        query: null,
        execution_timestamp: null,
        result_hash: null,
        result_number: null,
        doi: null
      }
    }
  },
  computed: {
    queryId () {
      return this.$route.params.query_id
    },
    databaseId () {
      return this.$route.params.database_id
    }
  },
  mounted () {
    this.loadData()
  },
  methods: {
    async loadData () {
      try {
        const res = await this.$axios.get(`/api/database/${this.databaseId}/querystore/${this.queryId}`)
        this.query = res.data
        console.debug('query data', res.data)
      } catch (err) {
        this.$toast.error('Could not load table data.')
      }
      this.loading = false
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
