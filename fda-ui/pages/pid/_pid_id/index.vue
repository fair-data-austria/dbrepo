<template>
  <div />
</template>

<script>
export default {
  mounted () {
    this.findPid()
  },
  methods: {
    async findPid () {
      try {
        const res = await this.$axios.get(`/api/pid/${this.$route.params.pid_id}`)
        console.debug('persistent id', res.data)
        this.$router.push(`/container/${res.data.cid}/database/${res.data.dbid}/query/${res.data.qid}`)
      } catch (err) {
        console.error('Could not load query', err)
        this.$toast.error('Could not load query')
        this.loading = false
      }
    }
  }
}
</script>
