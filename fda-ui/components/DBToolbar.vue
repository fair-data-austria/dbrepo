<template>
  <v-toolbar v-if="db" flat>
    <img id="engine-logo" :alt="`${db.image.repository}`" :src="`data:image/png;base64,${db.image.logo}`">
    <v-toolbar-title>
      {{ db.name }}
    </v-toolbar-title>
    <v-spacer />
    <v-toolbar-title>
      <v-btn color="primary" :to="`/databases/${$route.params.database_id}/tables/table_from_csv`">
        <v-icon left>mdi-cloud-upload</v-icon> Import CSV
      </v-btn>
    </v-toolbar-title>
    <template v-slot:extension>
      <v-tabs v-model="tab" color="primary">
        <v-tab :to="`/databases/${$route.params.database_id}/info`">
          Info
        </v-tab>
        <v-tab :to="`/databases/${$route.params.database_id}/tables/`">
          Tables
        </v-tab>
        <v-tab :to="`/databases/${$route.params.database_id}/queries/`">
          Queries
        </v-tab>
        <v-tab :to="`/databases/${$route.params.database_id}/admin/`">
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
#engine-logo {
  width: 2em;
  height: 2em;
  margin-right: 1.25em;
}
</style>
