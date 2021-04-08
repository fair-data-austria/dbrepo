<template>
  <v-row justify="center" align="center">
    <v-col cols="12" sm="8" md="6">
      DB {{ $route.params.id }}
    </v-col>
  </v-row>
</template>

<script>
export default {
  components: {},
  mounted () {
    console.log(this)
  }
}
</script>
