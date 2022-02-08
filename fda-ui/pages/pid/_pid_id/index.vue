<template>
  <div>
    <v-card v-if="loading">
      <v-card-title>PID Not Found</v-card-title>
      <v-card-subtitle>{{ pid }}</v-card-subtitle>
      <v-card-text>
        <p>This PID cannot be found in the system. Possible reasons are:</p>
        <ul>
          <li>The PID is incorrect in your source.</li>
          <li>The PID was copied incorrectly.</li>
          <li>The PID has not been activated yet.</li>
        </ul>
      </v-card-text>
    </v-card>
  </div>
</template>

<script>
export default {
  data () {
    return {
      loading: false
    }
  },
  computed: {
    pid () {
      return this.$route.params.pid_id
    }
  },
  mounted () {
    this.findPid()
  },
  methods: {
    async findPid () {
      this.loading = true
      try {
        const res = await this.$axios.get(`/api/pid/${this.$route.params.pid_id}`)
        console.debug('persistent id', res.data)
        this.$router.push(`/container/${res.data.cid}/database/${res.data.dbid}/query/${res.data.qid}`)
      } catch (err) {
        console.error('Could not load query', err)
        this.$toast.error('Could not load query')
      }
      this.loading = false
    }
  }
}
</script>
