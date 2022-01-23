<template>
  <div>
    <v-breadcrumbs :items="items" class="pa-0 mt-2" />
    <DBToolbar v-model="$store.state.db" />
    <v-card class="pb-2">
      <v-card-title class="pb-0">
        Query Builder
      </v-card-title>
      <v-card-text>
        <QueryBuilder />
      </v-card-text>
    </v-card>
  </div>
</template>

<script>
export default {
  data () {
    return {
      items: [
        { text: 'Databases', href: '/container' },
        { text: `${this.$route.params.database_id}`, href: `/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/info` },
        { text: 'Tables', href: `/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/table` }
      ]
    }
  },
  computed: {
  }
}
</script>
