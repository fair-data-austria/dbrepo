<template>
  <div>
    <v-progress-linear v-if="loading" :color="loadingColor" :indeterminate="!error" />
    <v-toolbar flat>
      <v-toolbar-title>
        {{ table.name }}
      </v-toolbar-title>
      <v-spacer />
      <v-toolbar-title>
        <v-btn class="mr-2" :disabled="!token" :to="`/container/${$route.params.container_id}/database/${$route.params.database_id}/table/${$route.params.table_id}/import`">
          <v-icon left>mdi-cloud-upload</v-icon> Import csv
        </v-btn>
        <v-btn color="primary" :disabled="!token" :href="`/api/container/${$route.params.container_id}/database/${$route.params.database_id}/table/${$route.params.table_id}/data/export`" target="_blank">
          <v-icon left>mdi-download</v-icon> Download
        </v-btn>
      </v-toolbar-title>
    </v-toolbar>
    <v-toolbar :color="versionColor" flat>
      <v-toolbar-title>
        <strong>Versioning</strong>
        <span v-if="version !== null">{{ versionFormatted }}</span>
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
        :loading="loadingData"
        :options.sync="options"
        :server-items-length="total"
        :footer-props="footerProps"
        class="elevation-1"
        @update:page="paginate" />
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
import { format } from 'date-fns'

export default {
  components: {
    TimeTravel
  },
  data () {
    return {
      loading: true,
      loadingData: true,
      total: 0,
      footerProps: {
        'items-per-page-options': [10, 20, 30, 40, 50]
      },
      dateMenu: false,
      timeMenu: false,
      pickVersionDialog: null,
      version: null,
      options: {
        page: 1,
        itemsPerPage: 10
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
    },
    token () {
      return this.$store.state.token
    },
    versionColor () {
      console.debug('version', this.version)
      if (this.version === null) {
        return 'grey lighten-1'
      }
      return 'primary white--text'
    },
    versionFormatted () {
      if (this.version === null) {
        return null
      }
      return this.formatDate(this.version)
    }
  },
  watch: {
    version (newVersion, oldVersion) {
      console.info('selected new version', newVersion)
      this.loadData()
    }
  },
  mounted () {
    this.loadProperties()
    this.loadData()
  },
  methods: {
    async loadProperties () {
      try {
        const res = await this.$axios.get(`/api/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/table/${this.$route.params.table_id}`)
        this.table = res.data
        console.debug('headers', res.data.columns)
        this.headers = res.data.columns.filter(c => !c.auto_generated)
        this.headers = this.headers.map((c) => {
          return {
            value: c.internal_name,
            text: this.columnAddition(c) + c.name
          }
        })
      } catch (err) {
        console.error('Failed to load table details', err)
        this.$toast.error('Could not get table details.')
      }
      this.loading = false
    },
    async loadData () {
      try {
        this.loadingData = true
        let url = `/api/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/table/${this.$route.params.table_id}/data?page=${this.options.page - 1}&size=${this.options.itemsPerPage}`
        if (this.version !== null) {
          console.info('versioning active', this.version)
          url += `&timestamp=${new Date(this.version).toISOString()}`
        }
        const res = await this.$axios.get(url)
        console.debug('version', this.datetime, 'data', res.data)
        this.total = Number.parseFloat(res.headers['fda-count'])
        this.rows = res.data.result
      } catch (err) {
        console.error('failed to load data', err)
        this.$toast.error('Could not load table data.')
      }
      this.loadingData = false
    },
    paginate (page) {
      this.options.page = page
      this.loadData()
    },
    columnAddition (column) {
      if (column.is_primary_key) {
        return '‡ '
      }
      if (column.unique) {
        return '† '
      }
      return ''
    },
    formatDate (d) {
      return format(new Date(d), 'dd.MM.yyyy HH:mm')
    }
  }
}
</script>

<style>
</style>
