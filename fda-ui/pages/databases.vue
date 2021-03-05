<template>
  <div>
    <h3>
      Databases
    </h3>
    <v-simple-table>
      <template v-slot:default>
        <thead>
          <tr>
            <th>DbName</th>
            <th>ContainerID</th>
            <th>ContainerName</th>
            <th>Created</th>
            <th>IpAddress</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="item in databases"
            :key="item.ContainerID">
            <td>
              <v-btn @click="selectClick(item)" icon><v-icon>{{ iconSelect }}</v-icon></v-btn>
              {{ item.DbName }}
            </td>
            <td class="trim">{{ trim(item.ContainerID) }}</td>
            <td>{{ item.ContainerName }}</td>
            <td>
              {{ formatDate(item.Created) }}<br>
              <span class="color-grey">
                ({{ relativeDate(item.Created) }})
              </span>
            </td>
            <td>{{ item.IpAddress }}</td>
            <td>{{ item.Status }}</td>
          </tr>
        </tbody>
      </template>
    </v-simple-table>
    <v-btn class="float-right mt-1" color="primary" @click.stop="createDbDialog = true">
      <v-icon class="mr-1">
        mdi-plus
      </v-icon>
      Create Database
    </v-btn>
    <v-dialog
      v-model="createDbDialog"
      max-width="640">
      <CreateDB @refresh="refresh" />
    </v-dialog>
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
      iconSelect: mdiDatabaseArrowRightOutline
    }
  },
  mounted () {
    this.refresh()
  },
  methods: {
    async refresh () {
      this.createDbDialog = false
      const res = await this.$axios.post('/listDatabases', {})
      this.databases = res.data
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
    },
    selectClick (item) {
      this.$store.commit('SET_CONTAINER', item)
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
</style>
