<template>
  <div>
    <h3>
      Tables
    </h3>
    <TableList />
    <TableCreate />
  </div>
</template>
<script>
import TableList from '@/components/TableList'
import TableCreate from '@/components/TableCreate'

export default {
  name: 'Tables',
  components: {
    TableList,
    TableCreate
  },
  data () {
    return {}
  },
  mounted () {
  },
  methods: {
  }
}
</script>

<style>
</style>
