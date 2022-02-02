<template>
  <div>
    <v-progress-linear v-if="loading" :color="loadingColor" :indeterminate="!error" />
    <v-toolbar flat>
      <v-toolbar-title>
        {{ table.name }}
      </v-toolbar-title>
      <v-spacer />
      <v-toolbar-title>
        <v-btn class="mr-2" :to="`/container/${$route.params.container_id}/database/${$route.params.database_id}/table/${$route.params.table_id}/import`">
          <v-icon left>mdi-cloud-upload</v-icon> Import csv
        </v-btn>
        <v-btn color="primary" :href="`/api/container/${$route.params.container_id}/database/${$route.params.database_id}/table/${$route.params.table_id}/data/export`" target="_blank">
          <v-icon left>mdi-download</v-icon> Download
        </v-btn>
      </v-toolbar-title>
    </v-toolbar>
    <v-toolbar color="primary white--text" flat>
      <v-toolbar-title>
        <strong>Versioning</strong>
      </v-toolbar-title>
      <v-spacer />
      <v-toolbar-title>
        <v-btn @click="pickVersionDialog = true">
          <v-icon left>mdi-update</v-icon> Pick
        </v-btn>
        <v-dialog
          v-model="pickVersionDialog"
          max-width="640">
          <TimeTravel @close="pickVersionDialog = false" />
        </v-dialog>
      </v-toolbar-title>
    </v-toolbar>
    <v-card>
      <v-data-table
        :headers="headers"
        :items="rows"
        :options.sync="options"
        :server-items-length="total"
        :footer-props="footerProps"
        class="elevation-1" />
    </v-card>
    <div class="mt-3">
      <v-chip
        class="mr-2"
        label>
        ‡ Primary Key
      </v-chip>
      <v-chip
        class="mr-2"
        label>
        † Unique Column
      </v-chip>
    </div>
    <v-breadcrumbs :items="items" class="pa-0 mt-2" />
  </div>
</template>
<script>
import TimeTravel from '@/components/dialogs/TimeTravel'

export default {
  components: {
    TimeTravel
  },
  data () {
    return {
      loading: true,
      total: null,
      footerProps: {
        'items-per-page-options': [10, 20, 30, 40, 50]
      },
      // datetime: Date.now(),
      // datetime: new Date().toISOString(),
      dateMenu: false,
      timeMenu: false,
      pickVersionDialog: null,
      version: new Date(),
      options: {
        page: 1,
        itemsPerPage: 10
        // sortBy: string[],
        // sortDesc: boolean[],
        // groupBy: string[],
        // groupDesc: boolean[],
        // multiSort: boolean,
        // mustSort: boolean
      },
      table: {
        name: null,
        description: null
      },
      items: [
        { text: 'Databases', href: '/container' },
        { text: `${this.$route.params.database_id}`, href: `/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/info` },
        { text: 'Tables', href: `/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/table` },
        { text: `${this.$route.params.table_id}`, href: `/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/table/${this.$route.params.table_id}` }
      ],
      headers: [],
      rows: []
    }
  },
  computed: {
    loadingColor () {
      return this.error ? 'red lighten-2' : 'primary'
    }
  },
  watch: {
    options: {
      handler () {
        this.loadData()
      },
      deep: true
    }
  },
  mounted () {
    this.loadProperties()
    this.loadData()
    this.loadDataCount()
  },
  methods: {
    async loadTotalTuples () {
      try {
        const res = await this.$axios.get(`/api/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/table/${this.$route.params.table_id}`)
        this.table = res.data
        console.debug('headers', res.data.columns)
        this.headers = res.data.columns.map((c) => {
          return {
            value: c.internal_name,
            text: this.columnAddition(c) + c.name
          }
        })
      } catch (err) {
        this.$toast.error('Could not get table details.')
        this.loading = false
      }
    },
    async loadProperties () {
      try {
        const res = await this.$axios.get(`/api/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/table/${this.$route.params.table_id}`)
        this.table = res.data
        console.debug('headers', res.data.columns)
        this.headers = res.data.columns.map((c) => {
          return {
            value: c.internal_name,
            text: this.columnAddition(c) + c.name
          }
        })
      } catch (err) {
        this.$toast.error('Could not get table details.')
        this.loading = false
      }
    },
    async loadData () {
      try {
        this.loading = true
        const datetime = this.version.toISOString()
        let url = `/api/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/table/${this.$route.params.table_id}/data`
        url += `?page=${this.options.page - 1}&size=${this.options.itemsPerPage}&timestamp=${datetime}`
        const res = await this.$axios.get(url)
        this.rows = res.data.result
        console.debug('table data', res.data)
      } catch (err) {
        console.error('failed to load data', err)
        this.$toast.error('Could not load table data.')
      }
      this.loading = false
    },
    async loadDataCount () {
      try {
        this.loading = true
        const url = `/api/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/table/${this.$route.params.table_id}/data?total=1`
        const res = await this.$axios.get(url)
        this.total = res.data.count
        console.debug('data count', res.data)
      } catch (err) {
        console.error('failed to load total count', err)
      }
      this.loading = false
    },
    columnAddition (column) {
      if (column.is_primary_key) {
        return '‡ '
      }
      if (column.unique) {
        return '† '
      }
      return ''
    }
  }
}
</script>

<style>
</style>
