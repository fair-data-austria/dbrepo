<template>
  <div>
    <h3>
      Table Paginated Listing
    </h3>
    <v-data-table
      :headers="headers"
      :items="rows"
      :items-per-page="10"
      class="elevation-1" />
  </div>
</template>
<script>
export default {
  name: 'TableListing',
  components: {
  },
  data () {
    return {
      headers: [
        { value: 'foo', text: 'Foo' },
        {}
      ],
      rows: [
        { foo: 'Bar' },
        {}
      ]
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
