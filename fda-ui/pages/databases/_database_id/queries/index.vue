<template>
  <div>
    <DBToolbar v-model="db" />
    <v-card>
      <v-card-title>
        Query Builder
      </v-card-title>
      <v-card-text>
        <QueryBuilder />
      </v-card-text>
    </v-card>
  </div>
</template>

<script>
export default {
  data () {
    return {
    }
  },
  computed: {
    db () {
      return this.$store.state.db
    }
  }
}
</script>
