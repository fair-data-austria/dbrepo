<template>
  <v-card>
    <v-card-title class="headline">
      Database
    </v-card-title>
    <v-card-text>
      <v-form v-model="formValid">
        <v-text-field
          v-model="database"
          label="Name"
          :rules="[v => !!v || $t('Required')]"
          required />
      </v-form>
    </v-card-text>
    <v-card-actions>
      <v-spacer />
      <v-btn
        @click="cancel">
        Cancel
      </v-btn>
      <v-btn
        :disabled="!formValid"
        color="primary"
        @click="createDB">
        Create
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>
export default {
  data () {
    return {
      formValid: false,
      database: null,
      container: ''
    }
  },
  methods: {
    cancel () {
      this.$parent.$parent.$parent.createDbDialog = false
    },
    async createDB () {
      const res = await this.$axios.post('/createDatabase', {
        ContainerName: this.container,
        DatabaseName: this.database
      })
      const { status } = res.data
      if (status === 201) {
        this.$toast.success(this.$t('db_created_success'))
        this.$emit('refresh') // this will also close dialog
      } else if (status === 500) {
        this.$toast.error(this.$t('internal_server_error'))
        console.error(res.data)
      }
    }
  }
}

</script>
