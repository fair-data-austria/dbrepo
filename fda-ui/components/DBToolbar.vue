<template>
  <v-toolbar v-if="db" flat>
    <img id="engine-logo" :alt="`${db.image.repository}`" :src="`data:image/png;base64,${db.image.logo}`">
    <v-toolbar-title>
      {{ db.name }}
    </v-toolbar-title>
    <v-spacer />
    <v-toolbar-title>
      <v-btn :to="`/databases/${$route.params.database_id}/tables/import`" class="mr-2">
        <v-icon left>mdi-cloud-upload</v-icon> Import CSV
      </v-btn>
      <v-btn color="blue-grey" :to="`/databases/${$route.params.database_id}/queries/create`" class="mr-2 white--text">
        <v-icon left>mdi-wrench</v-icon> Query Builder
      </v-btn>
      <v-btn color="primary" :to="`/databases/${$route.params.database_id}/tables/create`">
        <v-icon left>mdi-table-large-plus</v-icon> Create Table
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
