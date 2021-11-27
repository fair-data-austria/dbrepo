<template>
  <div>
    <DBToolbar v-model="db" />
    <QueryList />
    <v-breadcrumbs :items="items" class="pa-0 mt-2" />
  </div>
</template>

<script>
export default {
  data () {
    return {
      items: [
        { text: 'Databases', href: '/databases' },
        { text: `${this.$route.params.database_id}`, href: `/databases/${this.$route.params.database_id}/queries` }
      ]
    }
  },
  computed: {
    db () {
      return this.$store.state.db
    }
  }
}
</script>
