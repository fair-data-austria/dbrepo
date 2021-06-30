<template>
  <div>
    <DBToolbar v-model="db" />
    <TableList />
    <TableCreate />
    <TableFromCSV></TableFromCSV>
  </div>
</template>
<script>
import TableList from '@/components/TableList'
import TableCreate from '@/components/TableCreate'
import DBToolbar from '@/components/DBToolbar'
import TableFromCSV from './table_from_csv'

export default {
  name: 'Tables',
  components: {
    TableFromCSV,
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

<style>
</style>
