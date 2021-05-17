<template>
  <v-stepper v-model="step" vertical>
    <v-stepper-step :complete="step > 1" step="1">
      Upload CSV file
    </v-stepper-step>

    <v-stepper-content step="1">
      upload control
    </v-stepper-content>

    <v-stepper-step :complete="step > 2" step="2">
      Choose columns data types
    </v-stepper-step>
    <v-stepper-content step="2">
      Column select controls
      <v-btn color="primary" @click="step = 3">
        Continue
      </v-btn>
    </v-stepper-content>

    <v-stepper-step
      :complete="step > 3"
      step="3">
      Done
    </v-stepper-step>

    <v-stepper-content step="3">
      Done. Go to table.
    </v-stepper-content>
  </v-stepper>
</template>
<script>
export default {
  name: 'TableFromCSV',
  components: {
  },
  data () {
    return {
      step: 1
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
