<template>
  <div v-if="db">
    <DBToolbar />
    <QueryBuilder />
  </div>
</template>

<script>
import DBToolbar from '@/components/DBToolbar'
import QueryBuilder from '@/components/QueryBuilder'

export default {
  components: {
    DBToolbar,
    QueryBuilder
  },
  data () {
    return {
      db: null
    }
  },
  async mounted () {
    try {
      const res = await this.$axios.get(`/api/database/${this.$route.params.db_id}`)
      this.db = res.data
    } catch (err) {
      this.$toast.error('Could not load database.')
    }
  },
  methods: {
  }
}
</script>
