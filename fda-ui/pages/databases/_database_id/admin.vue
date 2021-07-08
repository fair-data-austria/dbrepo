<template>
  <div v-if="db">
    <DBToolbar />
    <v-card>
      <v-card-title>
        Database Administration
      </v-card-title>
      <v-card-text>
        <v-btn outlined color="error" @click="dialogDelete = true">Delete</v-btn>
      </v-card-text>
    </v-card>
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
      confirm: null
    }
  },
  computed: {
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
  methods: {
    async deleteDatabase () {
      try {
        await this.$axios.delete(`/api/database/${this.$route.params.database_id}`)
        this.$router.push({ path: '/databases' })
        this.$toast.success(`Database "${this.db.name}" deleted.`)
      } catch (err) {
        this.$toast.error('Could not delete database.')
      }
      this.dialogDelete = false
    }
  }
}
</script>
