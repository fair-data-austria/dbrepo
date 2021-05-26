<template>
  <div v-if="db">
    <DBToolbar />
    <v-btn outlined color="error" @click="dialogDelete = true">Delete</v-btn>
    <v-dialog v-model="dialogDelete" max-width="640">
      <v-card>
        <v-card-title class="headline">
          Delete
        </v-card-title>
        <v-card-text class="pb-1">
          Are you sure you want to drop this database?
        </v-card-text>
        <v-card-actions class="pl-4 pb-4 pr-4">
          <v-btn @click="dialogDelete = false">
            Cancel
          </v-btn>
          <v-spacer />
          <v-btn color="error" @click="deleteDatabase()">
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
      db: null,
      dialogDelete: false
    }
  },
  async mounted () {
    try {
      const res = await this.$axios.get(`/api/database/${this.$route.params.db_id}`)
      this.db = res.data
    } catch (err) {
      this.$toast.error('Could not load database.')
    }
  },
  methods: {
    async deleteDatabase () {
      try {
        await this.$axios.delete(`/api/database/${this.$route.params.db_id}`)
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
