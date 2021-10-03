<template>
  <v-toolbar v-if="db" flat>
    <img id="engine-logo" :alt="`${db.image.repository}`" :src="`data:image/png;base64,${db.image.logo}`">
    <v-toolbar-title>
      {{ db.name }}
    </v-toolbar-title>
    <v-spacer />
    <v-toolbar-title>
      <v-btn :to="`/databases/${databaseId}/tables/import`" class="mr-2">
        <v-icon left>mdi-cloud-upload</v-icon> Import CSV
      </v-btn>
      <v-btn color="blue-grey" :to="`/databases/${databaseId}/queries/create`" class="mr-2 white--text">
        <v-icon left>mdi-wrench</v-icon> Query Builder
      </v-btn>
      <v-btn color="primary" :to="`/databases/${databaseId}/tables/create`">
        <v-icon left>mdi-table-large-plus</v-icon> Create Table
      </v-btn>
    </v-toolbar-title>
    <template v-slot:extension>
      <v-tabs v-model="tab" color="primary">
        <v-tab :to="`/databases/${databaseId}/info`">
          Info
        </v-tab>
        <v-tab :to="`/databases/${databaseId}/tables`">
          Tables
        </v-tab>
        <v-tab :to="`/databases/${databaseId}/queries`">
          Queries
        </v-tab>
        <v-tab :to="`/databases/${databaseId}/admin`">
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
    },
    databaseId () {
      return this.$route.params.database_id
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
