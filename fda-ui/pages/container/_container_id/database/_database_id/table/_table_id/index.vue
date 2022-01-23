<template>
  <div>
    <v-card>
      <v-row dense>
        <v-col cols="6">
          <v-card-title v-if="table.name">
            {{ table.name }}
          </v-card-title>
          <v-card-subtitle v-if="table.name">
            {{ table.description }}
          </v-card-subtitle>
        </v-col>
        <v-col class="text-right" cols="6">
          <v-row dense>
            <v-col>
              <v-menu
                ref="dateMenu"
                v-model="dateMenu"
                :close-on-content-click="false"
                :return-value.sync="date"
                transition="scale-transition"
                offset-y>
                <template v-slot:activator="{ on, attrs }">
                  <v-text-field
                    v-model="date"
                    label="Date"
                    prepend-icon="mdi-calendar"
                    readonly
                    v-bind="attrs"
                    v-on="on" />
                </template>
                <v-date-picker
                  v-model="date"
                  no-title
                  scrollable>
                  <v-spacer />
                  <v-btn
                    text
                    color="primary"
                    @click="dateMenu = false">
                    Cancel
                  </v-btn>
                  <v-btn
                    text
                    color="primary"
                    @click="$refs.dateMenu.save(date)">
                    OK
                  </v-btn>
                </v-date-picker>
              </v-menu>
            </v-col>
            <v-col>
              <v-menu
                ref="timeMenu"
                v-model="timeMenu"
                :close-on-content-click="false"
                :nudge-right="40"
                :return-value.sync="time"
                transition="scale-transition"
                offset-y
                max-width="290px"
                min-width="290px">
                <template v-slot:activator="{ on, attrs }">
                  <v-text-field
                    v-model="time"
                    label="Time"
                    prepend-icon="mdi-clock-time-four-outline"
                    readonly
                    v-bind="attrs"
                    v-on="on" />
                </template>
                <v-time-picker
                  v-if="timeMenu"
                  v-model="time"
                  format="24hr"
                  @click:minute="$refs.timeMenu.save(time)" />
              </v-menu>
            </v-col>
          </v-row>
        </v-col>
      </v-row>
      <v-data-table
        dense
        :headers="headers"
        :items="rows"
        :loading="loading"
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
import { format, parse } from 'date-fns'

export default {
  name: 'TableListing',
  components: {
  },
  data () {
    return {
      loading: true,
      total: 150, // FIXME hardcoded until issue #119 is resolved
      footerProps: {
        'items-per-page-options': [10, 20, 30, 40, 50]
      },
      // datetime: Date.now(),
      // datetime: new Date().toISOString(),
      dateMenu: false,
      timeMenu: false,
      date: format(new Date(), 'yyyy-MM-dd'),
      time: format(new Date(), 'HH:mm'),
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
  watch: {
    date (val) {
      console.log('new date', val)
      if (!val) {
        this.date = format(new Date(), 'yyyy-MM-dd')
      }
      this.loadData()
    },
    time (val) {
      console.log('new time', val)
      if (!val) {
        this.time = '00:00'
      }
      this.loadData()
    },
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
  },
  methods: {
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
      const datetime = parse(`${this.date} ${this.time}`, 'yyyy-MM-dd HH:mm', new Date()).toISOString()
      console.log(datetime)
      this.loading = true
      try {
        let url = `/api/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/table/${this.$route.params.table_id}/data`
        url += `?page=${this.options.page - 1}&size=${this.options.itemsPerPage}&timestamp=${datetime}`
        const res = await this.$axios.get(url)
        this.rows = res.data.result
        console.debug('table data', res.data)
      } catch (err) {
        this.$toast.error('Could not load table data.')
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
