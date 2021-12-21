<template>
  <v-dialog
    v-model="dialog"
    max-width="600px">
    <template v-slot:activator="{ on, attrs }">
      <v-btn
        small
        v-bind="attrs"
        v-on="on">
        Unit
      </v-btn>
    </template>
    <v-card>
      <v-card-title>
        <span class="text-h5">Column Unit</span>
      </v-card-title>
      <v-card-text>
        <div>
          <i>
            Autocomplete not working yet
          </i>
        </div>
        <v-autocomplete
          hide-details
          dense
          clearable />
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          text
          @click="dialog = false">
          Close
        </v-btn>
        <v-btn
          color="blue darken-1"
          text
          @click="dialog = false">
          Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  props: {
    column: { type: Object, default: () => ({}) }
  },
  data () {
    return {
      dialog: false,
      isLoading: false,
      model: null,
      search: null,
      entries: []
    }
  },
  computed: {
    items () {
      return this.entries.map((entry) => {
        const Description = entry.Description.length > this.descriptionLimit
          ? entry.Description.slice(0, this.descriptionLimit) + '...'
          : entry.Description

        return Object.assign({}, entry, { Description })
      })
    }
  },

  watch: {
    search (val) {
      // Items have already been loaded
      if (this.items.length > 0) { return }

      // Items have already been requested
      if (this.isLoading) { return }

      this.isLoading = true

      // Lazily load input items
      this.$axios.get('/api/units/suggest')
        .then((res) => {
          debugger
          this.entries = res
        })
        .catch((err) => {
          console.log(err)
        })
        .finally(() => (this.isLoading = false))
    }
  },
  mounted () {
  },
  methods: {
  }
}
</script>

<style scoped>
</style>
