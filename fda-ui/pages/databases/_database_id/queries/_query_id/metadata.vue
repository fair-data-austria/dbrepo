<template>
  <div>
    <v-card>
      <v-card-title v-if="!loading">
        Metadata
      </v-card-title>
      <v-card-subtitle v-if="!loading">
        Subtitle
      </v-card-subtitle>
      <div>
        aaa
      </div>
    </v-card>
  </div>
</template>
<script>
export default {
  name: 'QueryDoiMetadata',
  components: {
  },
  data () {
    return {
      loading: false
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
