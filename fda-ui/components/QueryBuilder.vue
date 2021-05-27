<template>
  <div>
    <div>
      QB
    </div>
    <v-btn @click="buildQuery">Build</v-btn>
  </div>
</template>

<script>
export default {
  data () {
    return {
    }
  },
  mounted () {
  },
  methods: {
    async buildQuery () {
      const url = '/server-middleware/query/build'
      const data = {}
      const res = await this.$axios.post(url, data)
      console.log(res)
    }
  }
}
</script>

<style scoped>
</style>
