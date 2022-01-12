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
        { text: 'Databases', to: '/databases', activeClass: '' },
        { text: `${this.$route.params.database_id}`, to: `/databases/${this.$route.params.database_id}/info`, activeClass: '' },
        { text: 'Tables', to: `/databases/${this.$route.params.database_id}/tables`, activeClass: '' }
      ]
    }
  },
  computed: {
  }
}
</script>
