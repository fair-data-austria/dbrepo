<template>
  <div v-if="db">
    <DBToolbar />
    <QueryBuilder />
  </div>
</template>

<script>
export default {
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
