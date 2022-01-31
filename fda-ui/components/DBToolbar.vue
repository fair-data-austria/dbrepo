<template>
  <div>
    <v-progress-linear v-if="loading" :color="loadingColor" />
    <v-toolbar v-if="db" flat>
      <img id="engine-logo" :alt="`${db.image.repository}`" :src="`data:image/png;base64,${db.image.logo}`">
      <v-toolbar-title>
        {{ db.name }}
      </v-toolbar-title>
      <v-spacer />
      <v-toolbar-title>
        <v-btn :to="`/container/${$route.params.container_id}/database/${databaseId}/table/import`" class="mr-2">
          <v-icon left>mdi-cloud-upload</v-icon> Import CSV
        </v-btn>
        <v-btn color="blue-grey" :to="`/container/${$route.params.container_id}/database/${databaseId}/query/create`" class="mr-2 white--text">
          <v-icon left>mdi-wrench</v-icon> Query Builder
        </v-btn>
        <v-btn color="primary" :to="`/container/${$route.params.container_id}/database/${databaseId}/table/create`">
          <v-icon left>mdi-table-large-plus</v-icon> Create Table
        </v-btn>
      </v-toolbar-title>
      <template v-slot:extension>
        <v-tabs v-model="tab" color="primary">
          <v-tab :to="`/container/${$route.params.container_id}/database/${databaseId}/info`">
            Info
          </v-tab>
          <v-tab :to="`/container/${$route.params.container_id}/database/${databaseId}/table`">
            Tables
          </v-tab>
          <v-tab :to="`/container/${$route.params.container_id}/database/${databaseId}/query`">
            Queries
          </v-tab>
          <v-tab :to="`/container/${$route.params.container_id}/database/${databaseId}/admin`">
            Admin
          </v-tab>
        </v-tabs>
      </template>
    </v-toolbar>
  </div>
</template>

<script>
export default {
  data () {
    return {
      tab: null,
      loading: false,
      error: false
    }
  },
  computed: {
    db () {
      return this.$store.state.db
    },
    databaseId () {
      return this.$route.params.database_id
    },
    loadingColor () {
      return 'primary'
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    async init () {
      if (this.db != null) {
        return
      }
      try {
        this.loading = true
        const res = await this.$axios.get(`/api/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}`)
        console.debug('database', res.data)
        this.$store.commit('SET_DATABASE', res.data)
        this.loading = false
      } catch (err) {
        this.$toast.error('Could not load database.')
        this.loading = false
      }
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
