<template>
  <div>
    <DBToolbar v-model="db" />
    <TableList />
    <TableCreate />
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

<style>
</style>
