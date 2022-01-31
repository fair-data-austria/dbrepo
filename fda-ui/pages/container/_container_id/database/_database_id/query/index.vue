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
        { text: 'Databases', href: '/container' },
        { text: `${this.$route.params.database_id}`, href: `/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/query` }
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
