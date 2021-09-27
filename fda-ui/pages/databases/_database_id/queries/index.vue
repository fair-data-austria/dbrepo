<template>
  <div>
    <DBToolbar v-model="db" />
    <QueryList />
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
