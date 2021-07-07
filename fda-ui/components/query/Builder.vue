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
    <QueryFilters
      v-if="table"
      v-model="clauses"
      :columns="columnNames" />
    <v-row v-if="query.formatted">
      <v-col>
        <highlightjs autodetect :code="query.formatted" />
      </v-col>
      <v-col cols="3" class="actions">
        <v-btn class="execute" color="primary">
          <v-icon>mdi-refresh</v-icon>
          Execute
        </v-btn>
      </v-col>
    </v-row>
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
      const columns = this.tableDetails && this.tableDetails.columns
      return columns || []
    },
    columnNames () {
      return this.selectItems && this.selectItems.map(s => s.name)
    }
  },
  watch: {
    clauses: {
      deep: true,
      handler () {
        this.buildQuery()
      }
    }
  },
  async mounted () {
    // XXX same as in TableList
    try {
      const res = await this.$axios.get(
        `/api/table/api/database/${this.$route.params.db_id}/table`)
      this.tables = res.data
    } catch (err) {
      this.$toast.error('Could not list table.')
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
        const res = await this.$axios.get(`/api/table/api/database/${this.$route.params.db_id}/table/${tableId}`)
        this.tableDetails = res.data
        this.buildQuery()
      } catch (err) {
        this.$toast.error('Could not get table details.')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
/* these are taked from solarized-light (plugins/vendors.js), to override the
main.scss file from vuetify, because it paints it red */
::v-deep code {
  background: #fdf6e3;
  color: #657b83;
}

.actions {
  align-items: center;
  display: flex;
  justify-content: center;
}
</style>
