<template>
  <div>
    <DBToolbar v-model="db" />
    <QueryList />
    <v-breadcrumbs :items="items" class="pa-0 mt-2" />
  </div>
</template>

<script>

export default {
  data () {
    return {
      items: [
        { text: 'Databases', to: '/databases', activeClass: '' },
        { text: `${this.$route.params.database_id}`, to: `/databases/${this.$route.params.database_id}/queries`, activeClass: '' }
      ]
    }
  },
  computed: {
    db () {
      return this.$store.state.db
    }
  }
}
</script>
