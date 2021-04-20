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
        <v-select
          v-model="engine"
          label="Engine"
          :items="[engine]"
          item-text="label"
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
        :loading="loading"
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
      loading: false,
      database: '',
      engine: {
        label: 'PostgreSQL, latest',
        repo: 'postgres',
        tag: 'latest'
      },
      container: ''
    }
  },
  methods: {
    cancel () {
      this.$parent.$parent.$parent.createDbDialog = false
    },
    async createDB () {
      this.loading = true

      // create a container
      let res
      let containerId
      try {
        res = await this.$axios.post('/api/container/', {
          name: this.database,
          repository: this.engine.repo,
          tag: this.engine.tag
        })
        containerId = res.data.id
        console.log(containerId)
      } catch (err) {
        this.$toast.error('Could not create container. Try another name.')
        this.loading = false
        return
      }

      // start the container
      try {
        res = await this.$axios.put(`/api/container/${containerId}`, {
          action: 'START'
        })
      } catch (err) {
        this.loading = false
        this.$toast.error('Could not start container.')
        return
      }

      // Pause.
      // DB fails to create when container has not started up yet
      await new Promise(resolve => setTimeout(resolve, 2000))

      // create the DB
      try {
        res = await this.$axios.post('/api/database/', {
          name: this.database,
          containerId
        })
        console.log(res)
      } catch (err) {
        this.loading = false
        this.$toast.error('Could not create database.')
        return
      }

      this.loading = false
      this.$toast.success(`Database "${res.data.name}" created.`)
      this.$emit('refresh')
    }
  }
}

</script>
