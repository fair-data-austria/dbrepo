<template>
  <div v-if="db">
    <v-toolbar dense color="" flat>
      <v-toolbar-title>{{ db.name }}</v-toolbar-title>
      <template v-slot:extension>
        <v-tabs v-model="tab" color="primary">
          <v-tabs-slider color="primary" />
          <v-tab>
            Info
          </v-tab>
          <v-tab>
            Tables
          </v-tab>
          <v-tab>
            Query
          </v-tab>
          <v-tab>
            Admin
          </v-tab>
        </v-tabs>
      </template>
    </v-toolbar>
    <v-tabs-items v-model="tab">
      <v-tab-item>
        <v-card flat>
          <v-card-text>
            {{ db.name }}
          </v-card-text>
        </v-card>
      </v-tab-item>
      <v-tab-item>
        <v-card flat>
          <v-card-text>
            <v-btn :to="`/db/${$route.params.db_id}/tables`">Tables</v-btn>
          </v-card-text>
        </v-card>
      </v-tab-item>
      <v-tab-item>
        <v-card flat>
          <v-card-text>Query</v-card-text>
        </v-card>
      </v-tab-item>
      <v-tab-item>
        <v-card flat>
          <v-card-text>
            <v-btn outlined color="error" @click="dialogDelete = true">Delete</v-btn>
          </v-card-text>
        </v-card>
      </v-tab-item>
    </v-tabs-items>
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
export default {
  components: {},
  data () {
    return {
      db: null,
      tab: 0,
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
