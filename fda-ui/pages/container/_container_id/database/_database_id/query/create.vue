<template>
  <div>
    <QueryBuilder />
    <v-breadcrumbs :items="items" class="pa-0 mt-2" />
  </div>
</template>

<script>
export default {
  data () {
    return {
      items: [
        { text: 'Databases', href: '/container' },
        { text: `${this.$route.params.database_id}`, href: `/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/info` },
        { text: 'Queries', href: `/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/query` }
      ]
    }
  },
  computed: {
  }
}
</script>
