<template>
  <div>
    <v-card>
      <v-progress-linear v-if="loading" :color="loadingColor" :indeterminate="!error" />
      <v-card-title>
        Time Travelling
      </v-card-title>
      <v-card-subtitle>
        View data for other version
      </v-card-subtitle>
      <v-card-text>
        <v-date-picker
          v-model="picker"
          no-title />
        <v-time-picker
          v-model="time"
          format="24hr"
          no-title />
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn
          class="mb-2"
          @click="cancel">
          Cancel
        </v-btn>
        <v-btn
          class="mb-2"
          color="blue-grey white--text"
          @click="reset">
          Now
        </v-btn>
        <v-btn
          id="version"
          class="mb-2"
          :disabled="version === null || version === undefined"
          color="primary"
          @click="pick">
          Pick
        </v-btn>
      </v-card-actions>
    </v-card>
  </div>
</template>

<script>
export default {
  data () {
    return {
      formValid: false,
      loading: false,
      error: false,
      version: null,
      versions: []
    }
  },
  computed: {
    loadingColor () {
      return this.error ? 'red lighten-2' : 'primary'
    }
  },
  mounted () {
    this.loadVersions()
  },
  methods: {
    cancel () {
      this.$parent.$parent.$parent.$parent.pickVersionDialog = false
    },
    sleep (ms) {
      return new Promise((resolve) => {
        setTimeout(resolve, ms)
      })
    },
    reset () {
      this.$parent.$parent.$parent.$parent.version = { id: null, created: null }
      this.cancel()
    },
    pick () {
      this.$parent.$parent.$parent.$parent.version = this.versions[this.version]
      this.cancel()
    },
    async loadVersions () {
      this.loading = true
      try {
        const url = `/api/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/version`
        const res = await this.$axios.get(url)
        this.versions = res.data
        console.debug('versions', this.versions)
      } catch (err) {
        console.error('Failed to get versions', err)
        this.$toast.error('Failed to get versions')
      }
      this.loading = false
    }
  }
}
</script>
