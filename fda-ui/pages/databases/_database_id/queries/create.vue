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
        { text: 'Databases', href: '/databases' },
        { text: `${this.$route.params.database_id}`, href: `/databases/${this.$route.params.database_id}` },
        { text: 'Tables', href: `/databases/${this.$route.params.database_id}/tables` }
      ]
    }
  },
  computed: {
  }
}
</script>
