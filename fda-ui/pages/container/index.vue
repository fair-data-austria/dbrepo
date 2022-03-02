<template>
  <div>
    <v-progress-linear v-if="loading" :color="loadingColor" :indeterminate="!error" />
    <v-toolbar flat>
      <v-toolbar-title>
        <span>Databases</span>
      </v-toolbar-title>
      <v-spacer />
      <v-toolbar-title>
        <v-btn v-if="token" color="primary" @click.stop="createDbDialog = true">
          <v-icon left>mdi-plus</v-icon> Database
        </v-btn>
      </v-toolbar-title>
    </v-toolbar>
    <v-card flat>
      <v-simple-table>
        <template v-slot:default>
          <thead>
            <tr>
              <th>Name</th>
              <th>Engine</th>
              <th>Tables</th>
              <th>Created</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="databases.length === 0" aria-readonly="true">
              <td colspan="4">
                <span v-if="!loading">(no databases)</span>
              </td>
            </tr>
            <tr
              v-for="item in databases"
              :key="item.id"
              class="database"
              @click="loadDatabase(item)">
              <td>{{ item.name }}</td>
              <td>{{ item.engine }}</td>
              <td />
              <td>{{ formatDate(item.created) }}</td>
            </tr>
          </tbody>
        </template>
      </v-simple-table>
      <v-dialog
        v-model="createDbDialog"
        persistent
        max-width="640">
        <CreateDB @close="createDbDialog = false" />
      </v-dialog>
    </v-card>
    <v-breadcrumbs :items="items" class="pa-0 mt-2" />
  </div>
</template>
<script>
import { mdiDatabaseArrowRightOutline } from '@mdi/js'
import CreateDB from '@/components/dialogs/CreateDB'
import { formatDistance, format } from 'date-fns'
import deLocale from 'date-fns/locale/de'

export default {
  components: {
    CreateDB
  },
  data () {
    return {
      createDbDialog: false,
      databases: [],
      items: [
        { text: 'Databases', href: '/container' }
      ],
      loading: true,
      error: false,
      iconSelect: mdiDatabaseArrowRightOutline
    }
  },
  computed: {
    loadingColor () {
      return this.error ? 'red lighten-2' : 'primary'
    },
    token () {
      return this.$store.state.token
    }
  },
  mounted () {
    this.refresh()
  },
  methods: {
    async refresh () {
      this.createDbDialog = false
      try {
        this.loading = true
        let res = await this.$axios.get('/api/container/')
        this.containers = res.data
        console.debug('containers', this.containers)
        for (const container of this.containers) {
          res = await this.$axios.get(`/api/container/${container.id}/database`)
          for (const database of res.data) {
            database.container_id = container.id
            this.databases.push(database)
          }
        }
        console.debug('databases', this.databases)
        this.loading = false
        this.error = false
      } catch (err) {
        console.error('containers', err)
        this.error = true
      }
    },
    loadDatabase (database) {
      this.$router.push(`/container/${database.container_id}/database/${database.id}/info`)
    },
    trim (s) {
      return s.slice(0, 12)
    },
    formatDate (d) {
      return format(new Date(d), 'dd.MM.yyyy HH:mm')
    },
    relativeDate (d) {
      let options = { addSuffix: true }
      if (this.$i18n.locale === 'de') {
        options = { ...options, locale: deLocale }
      }
      return formatDistance(new Date(d), new Date(), options)
    }
  }
}
</script>

<style>
  .trim {
    max-width: 10em;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .database:hover {
    cursor: pointer;
  }
  .color-grey {
    color: #aaa;
  }
  .v-progress-circular {
    margin-left: 8px;
  }
</style>
