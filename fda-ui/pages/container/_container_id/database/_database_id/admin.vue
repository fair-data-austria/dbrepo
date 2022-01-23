<template>
  <div v-if="db">
    <DBToolbar />
    <v-tabs-items v-model="tab">
      <v-card flat>
        <v-card-title>
          Database Administration
        </v-card-title>
        <v-card-text>
          <v-btn outlined color="error" @click="dialogDelete = true">Delete</v-btn>
        </v-card-text>
      </v-card>
    </v-tabs-items>
    <v-breadcrumbs :items="items" class="pa-0 mt-2" />
    <v-dialog v-model="dialogDelete" max-width="500">
      <v-card>
        <v-card-title class="headline">
          Delete
        </v-card-title>
        <v-card-text class="pb-1">
          Are you sure to drop this database? Confirm the deletion by typing the database internal name
          <strong>{{ db.internalName }}</strong> in the text box below.
          <v-text-field v-model="confirm" label="Database Name" />
        </v-card-text>
        <v-card-actions class="pl-4 pb-4 pr-4">
          <v-btn @click="dialogDelete=false">
            Cancel
          </v-btn>
          <v-spacer />
          <v-btn :disabled="canDelete" color="error" @click="deleteDatabase()">
            Delete
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import DBToolbar from '@/components/DBToolbar'

export default {
  components: {
    DBToolbar
  },
  data () {
    return {
      dialogDelete: false,
      confirm: null,
      items: [
        { text: 'Databases', href: '/databases' },
        { text: `${this.$route.params.database_id}`, href: `/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/info` }
      ]
    }
  },
  computed: {
    tab () {
      return 3
    },
    db () {
      return this.$store.state.db
    },
    canDelete () {
      if (this.confirm === null) {
        return true
      }
      return this.confirm !== this.db.internalName
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    async deleteDatabase () {
      try {
        await this.$axios.delete(`/api/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}`)
        this.$router.push({ path: '/databases' })
        this.$toast.success(`Database "${this.db.name}" deleted.`)
      } catch (err) {
        this.$toast.error('Could not delete database.')
      }
      this.dialogDelete = false
    },
    async init () {
      if (this.db != null) {
        return
      }
      try {
        this.loading = true
        const res = await this.$axios.get(`/api/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}`)
        console.debug('database', res.data)
        this.$store.commit('SET_DATABASE', res.data)
        this.loading = false
      } catch (err) {
        this.$toast.error('Could not load database.')
        this.loading = false
      }
    }
  }
}
</script>
