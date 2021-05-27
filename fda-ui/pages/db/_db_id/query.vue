<template>
  <div v-if="db">
    <DBToolbar />
    <QueryBuilder />
  </div>
</template>

<script>
import DBToolbar from '@/components/DBToolbar'
import QueryBuilder from '@/components/query/QueryBuilder'

export default {
  components: {
    DBToolbar,
    QueryBuilder
  },
  data () {
    return {
    }
  },
  computed: {
    db () {
      return this.$store.state.db
    }
  }
}
</script>
