<template>
  <v-toolbar v-if="value" dense flat>
    <v-toolbar-title>{{ value.name }}</v-toolbar-title>
    <template v-slot:extension>
      <v-tabs v-model="tab" color="primary" class="mb-1">
        <v-tabs-slider color="primary" />
        <v-tab :to="`/db/${$route.params.db_id}`">
          Info
        </v-tab>
        <v-tab :to="`/db/${$route.params.db_id}/tables`">
          Tables
        </v-tab>
        <v-tab :to="`/db/${$route.params.db_id}/query`">
          Query
        </v-tab>
        <v-tab :to="`/db/${$route.params.db_id}/admin`">
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
      value: null,
      tab: null
    }
  },
  async mounted () {
    try {
      const res = await this.$axios.get(`/api/database/${this.$route.params.db_id}`)
      this.value = res.data
    } catch (err) {
      this.$toast.error('Could not load database.')
    }
  },
  methods: {
  }
}
</script>

<style scoped>
</style>
