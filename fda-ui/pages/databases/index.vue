<template>
  <div>
    <v-toolbar flat>
      <v-toolbar-title>Databases</v-toolbar-title>
      <v-spacer />
      <v-toolbar-title>
        <v-btn color="primary" @click.stop="createDbDialog = true">
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
              <th>Description</th>
              <th>Engine</th>
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
              :key="item.id">
              <td>
                <v-btn :to="`/databases/${item.id}/info`" icon>
                  <v-icon>{{ iconSelect }}</v-icon>
                </v-btn>
                {{ item.name }}
              </td>
              <td>{{ item.description }}</td>
              <td>{{ item.engine }}</td>
              <td>{{ item.created }}</td>
            </tr>
          </tbody>
        </template>
      </v-simple-table>
      <v-dialog
        v-model="createDbDialog"
        persistent
        max-width="640">
        <CreateDB @refresh="refresh" />
      </v-dialog>
    </v-card>
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
      loading: true,
      iconSelect: mdiDatabaseArrowRightOutline
    }
  },
  mounted () {
    this.refresh()
  },
  methods: {
    async refresh () {
      this.createDbDialog = false
      this.loading = true
      const res = await this.$axios.get('/api/database/')
      this.databases = res.data
      this.loading = false
      console.debug('databases', res.data)
    },
    trim (s) {
      return s.slice(0, 12)
    },
    formatDate (d) {
      return format(new Date(d), 'dd/MM/yyyy HH:mm')
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
  .color-grey {
    color: #aaa;
  }
  .v-progress-circular {
    margin-left: 8px;
  }
</style>
