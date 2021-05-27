<template>
  <div>
    <v-row dense>
      <v-col cols="6">
        <v-select
          v-model="table"
          :items="tables"
          item-text="name"
          return-object
          label="Table"
          @change="loadColumns" />
      </v-col>
      <v-col cols="6">
        <v-select
          v-model="select"
          item-text="name"
          :disabled="!table"
          :items="selectItems"
          label="Columns"
          return-object
          multiple
          @change="buildQuery" />
      </v-col>
    </v-row>
    {{ query.sql }}
  </div>
</template>

<script>
export default {
  data () {
    return {
      table: null,
      tables: [],
      tableDetails: null,
      query: {},
      select: [],
      clauses: []
    }
  },
  computed: {
    selectItems () {
      return this.tableDetails && this.tableDetails.columns
    }
  },
  async mounted () {
    // XXX same as in TableList
    try {
      const res = await this.$axios.get(
        `/api/tables/api/database/${this.$route.params.db_id}/table`)
      this.tables = res.data
    } catch (err) {
      this.$toast.error('Could not list tables.')
    }
  },
  methods: {
    async buildQuery () {
      if (!this.table) {
        return
      }
      const url = '/server-middleware/query/build'
      const data = {
        table: this.table.internalName,
        select: this.select.map(s => s.name),
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
    },
    async loadColumns () {
      const tableId = this.table.id
      try {
        const res = await this.$axios.get(`/api/tables/api/database/${this.$route.params.db_id}/table/${tableId}`)
        this.tableDetails = res.data
        this.buildQuery()
      } catch (err) {
        this.$toast.error('Could not get table details.')
      }
    }
  }
}
</script>

<style scoped>
/* .select {
   width: 200px;
   } */
</style>
