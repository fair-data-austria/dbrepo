<template>
  <div>
    <DBToolbar v-model="db" />
    <TableList />
    <TableCreate />
    <v-card class="mt-1">
      <v-card-text>
        <nuxt-link class="table_from_csv" :to="`/db/${$route.params.db_id}/tables/table_from_csv`">
          Create table from CSV file
        </nuxt-link>
      </v-card-text>
    </v-card>
  </div>
</template>
<script>
import TableList from '@/components/TableList'
import TableCreate from '@/components/TableCreate'
import DBToolbar from '@/components/DBToolbar'

export default {
  name: 'Tables',
  components: {
    TableList,
    TableCreate,
    DBToolbar
  },
  data () {
    return {
      db: null
    }
  },
  mounted () {
  },
  methods: {
  }
}
</script>

<style scoped>
a.table_from_csv {
  font-size: 14pt;
}
</style>
