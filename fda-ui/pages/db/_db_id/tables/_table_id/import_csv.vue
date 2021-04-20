<template>
  <div>
    <h3>
      Table Import CSV
    </h3>
    <v-row dense>
      <v-col cols="10">
        <v-file-input
          accept="text/csv"
          show-size
          label="CSV File" />
      </v-col>
      <v-col cols="2" class="mt-3">
        <v-btn :loading="loading" @click="upload">Upload</v-btn>
      </v-col>
    </v-row>
  </div>
</template>
<script>
export default {
  name: 'TableImportCSV',
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
    upload () {
      this.loading = true
      setTimeout(() => {
        this.$toast.success('Uploaded successfully!')
        this.loading = false
        this.$router.push({ path: '.' })
      }, 1000)
    }
  }
}
</script>

<style>
</style>
