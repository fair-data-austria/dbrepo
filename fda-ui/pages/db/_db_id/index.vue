<template>
  <v-row v-if="db" justify="center" align="center">
    <v-col cols="12" sm="8" md="6">
      <h2>
        {{ db.name }}
      </h2>
      <v-btn :to="`/db/${$route.params.db_id}/tables`">Tables</v-btn>
    </v-col>
  </v-row>
</template>

<script>
export default {
  components: {},
  data () {
    return {
      db: null
    }
  },
  async mounted () {
    try {
      const res = await this.$axios.get(`http://localhost:9092/api/database/${this.$route.params.db_id}`)
      this.db = res.data
    } catch (err) {
      this.$toast.error('Could not load database.')
    }
  }
}
</script>
