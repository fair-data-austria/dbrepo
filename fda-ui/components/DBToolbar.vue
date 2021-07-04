<template>
  <v-toolbar v-if="db" flat>
    <v-toolbar-title>
      {{ db.name }}
    </v-toolbar-title>
    <template v-slot:extension>
      <v-tabs v-model="tab" color="primary" class="mb-1">
        <v-tabs-slider color="primary" />
        <v-tab :to="`/db/${$route.params.db_id}/`">
          Info
        </v-tab>
        <v-tab :to="`/db/${$route.params.db_id}/tables/`">
          Tables
        </v-tab>
        <v-tab v-if="false" :to="`/db/${$route.params.db_id}/query/`">
          Query
        </v-tab>
        <v-tab :to="`/db/${$route.params.db_id}/admin/`">
          Admin
        </v-tab>
      </v-tabs>
    </template>
  </v-toolbar>
</template>

<script>
export default {
  data () {
    return {
      tab: null
    }
  },
  computed: {
    db () {
      return this.$store.state.db
    }
  }
}
</script>

<style scoped>
</style>
