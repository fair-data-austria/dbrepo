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
          v-model="date"
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
          :disabled="date === null || time === null"
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
      date: null,
      time: null
    }
  },
  computed: {
    loadingColor () {
      return this.error ? 'red lighten-2' : 'primary'
    }
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
      this.$parent.$parent.$parent.$parent.version = null
      this.cancel()
    },
    pick () {
      this.$parent.$parent.$parent.$parent.version = this.formatDate()
      this.cancel()
    },
    formatDate () {
      if (this.date === null || this.time === null) {
        return null
      }
      console.debug('selected date', this.date, 'time', this.time)
      return Date.parse(this.date + ' ' + this.time)
    }
  }
}
</script>
