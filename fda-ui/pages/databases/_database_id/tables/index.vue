<template>
  <div>
    <DBToolbar />
    <v-tabs-items v-model="tab">
      <TableList />
    </v-tabs-items>
    <v-breadcrumbs :items="items" class="pa-0 mt-2" />
  </div>
</template>
<script>
import TableList from '@/components/TableList'
import DBToolbar from '@/components/DBToolbar'

export default {
  name: 'Tables',
  components: {
    TableList,
    DBToolbar
  },
  data () {
    return {
      db: null,
      items: [
        { text: 'Databases', href: '/databases' },
        { text: `${this.$route.params.database_id}`, href: `/databases/${this.$route.params.database_id}/tables` }
      ]
    }
  },
  computed: {
    tab () {
      return 1
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
