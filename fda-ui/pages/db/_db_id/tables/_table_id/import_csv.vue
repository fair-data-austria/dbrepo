<template>
  <div>
    <h3>
      Table Import CSV
    </h3>
    <v-row dense>
      <v-col cols="10">
        <v-file-input
          v-model="file"
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
      loading: false,
      file: null
    }
  },
  mounted () {
  },
  methods: {
    async upload () {
      this.loading = true
      const url = `/api/tables/api/database/${this.$route.params.db_id}/table/${this.$route.params.table_id}`
      const data = new FormData()
      data.append('file', this.file)
      try {
        const res = await this.$axios.post(url, data, {
          headers: { 'Content-Type': 'multipart/form-data' }
        })
        if (res.data.Result) {
          this.$toast.success('Uploaded successfully!')
          this.$router.push({ path: '.' })
        } else {
          this.$toast.error('Could not upload CSV data')
        }
      } catch (err) {
        this.$toast.error('Could not upload data.')
      }
      this.loading = false
    }
  }
}
</script>

<style>
</style>
