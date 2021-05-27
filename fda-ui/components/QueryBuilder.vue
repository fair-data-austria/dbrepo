<template>
  <div>
    <div>
      QB
    </div>
    <v-btn @click="buildQuery">Build</v-btn>
    <br>
    {{ query.table }}
    <br>
    {{ query.statements }}
    <br>
    {{ query.sql }}
  </div>
</template>

<script>
export default {
  data () {
    return {
      query: {},
      table: 'MyTable',
      select: [],
      clauses: []
    }
  },
  mounted () {
  },
  methods: {
    async buildQuery () {
      const url = '/server-middleware/query/build'
      const data = {
        table: this.table,
        select: this.select,
        clauses: this.clauses
      }
      try {
        const res = await this.$axios.post(url, data)
        if (res && !res.error) {
          this.query = res.data
        }
      } catch (e) {
        console.log(e)
      }
    }
  }
}
</script>

<style scoped>
</style>
