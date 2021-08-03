<template>
  <div>
    <DBToolbar v-model="db" />
    <TableList />
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
