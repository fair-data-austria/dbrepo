<template>
  <v-dialog
    v-model="dialog"
    max-width="600px">
    <template v-slot:activator="{ on, attrs }">
      <i v-if="!name">unspecified</i>
      <span v-else>{{ name }}</span>
      <v-btn
        class="ml-2"
        icon
        small
        v-bind="attrs"
        v-on="on">
        <v-icon>
          mdi-pencil-outline
        </v-icon>
      </v-btn>
    </template>
    <v-card>
      <v-card-title>
        <span class="text-h5">Column Unit</span>
      </v-card-title>
      <v-card-text>
        <v-autocomplete
          v-model="model"
          solo
          clearable
          auto-select-first
          :cache-items="false"
          autofocus
          :search-input.sync="search"
          :items="items"
          hide-no-data
          hide-details
          dense>
          <template
            v-slot:item="{ item, attrs, on }">
            <v-list-item v-bind="attrs" v-on="on">
              <v-list-item-content>
                <v-list-item-title>{{ item.value.name }}</v-list-item-title>
                <v-list-item-subtitle>{{ item.value.comment }}</v-list-item-subtitle>
              </v-list-item-content>
            </v-list-item>
          </template>
        </v-autocomplete>
      </v-card-text>
      <v-expand-transition>
        <v-list v-if="model" class="lighten-3" subheader three-line>
          <v-list-item>
            <v-list-item-content>
              <v-list-item-title>Name</v-list-item-title>
              <v-list-item-subtitle>{{ model.name }}</v-list-item-subtitle>
            </v-list-item-content>
          </v-list-item>
          <v-list-item>
            <v-list-item-content>
              <v-list-item-title>Symbol</v-list-item-title>
              <v-list-item-subtitle>{{ model.symbol }}</v-list-item-subtitle>
            </v-list-item-content>
          </v-list-item>
          <v-list-item>
            <v-list-item-content>
              <v-list-item-title>Comment</v-list-item-title>
              <v-list-item-subtitle>{{ model.comment }}</v-list-item-subtitle>
            </v-list-item-content>
          </v-list-item>
          <v-list-item v-if="uri" three-line>
            <v-list-item-content>
              <v-list-item-title>URI</v-list-item-title>
              <v-list-item-subtitle>{{ uri }}</v-list-item-subtitle>
            </v-list-item-content>
          </v-list-item>
        </v-list>
      </v-expand-transition>
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
          :disabled="!model || !uri"
          @click="save">
          Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  props: {
    column: { type: Object, default: () => ({}) },
    tableId: { type: Number, default: () => -1 }
  },
  data () {
    return {
      dialog: false,
      isLoading: false,
      saved: false,
      model: null,
      uri: null,
      search: null,
      entries: []
    }
  },
  computed: {
    name () {
      return this.saved && this.model && this.model.name
    },
    items () {
      return this.entries && this.entries.map((entry) => {
        return {
          // text: `${entry.name} [${entry.symbol}], ${entry.comment}`,
          text: entry.name,
          value: entry
        }
      })
    }
  },
  watch: {
    async model (val) {
      this.uri = null
      this.saved = false
      if (!val) { return }
      try {
        const res = await this.$axios.get(`/api/units/uri/${val.name}`)
        this.uri = res.data.URI
      } catch (err) {
        this.$toast.error(`Could not load URI of unit "${val.name}"`)
        console.log(err)
      }
    },
    async search (val) {
      if (this.isLoading) { return }
      if (!val || !val.length) { return }
      this.isLoading = true
      try {
        const res = await this.$axios.post('/api/units/suggest', {
          offset: 0,
          ustring: this.search
        })
        this.entries = res.data
      } catch (err) {
        this.$toast.error('Could not load unit suggestions.')
        console.log(err)
      }
      this.isLoading = false
    }
  },
  mounted () {
  },
  methods: {
    async save () {
      try {
        await this.$axios.post('/api/units/saveconcept', {
          name: this.model.name,
          uri: this.uri
        })
      } catch (error) {
        const { status } = error.response
        if (status !== 201 && status !== 400) {
          this.$toast.error('Could not save concept.')
          console.log(error)
        }
      }
      console.log(this.$route.params.database_id, this.tableId, this.column)
      try {
        await this.$axios.post('/api/units/savecolumnsconcept', {
          cdbid: Number(this.$route.params.database_id),
          cid: this.column.id,
          tid: this.tableId,
          uri: this.uri
        })
        this.dialog = false
        this.saved = true
        this.$nextTick(() => {
          this.$emit('save')
        })
      } catch (err) {
        this.$toast.error('Could not save column unit.')
        console.log(err)
      }
    }
  }
}
</script>

<style scoped>
</style>
