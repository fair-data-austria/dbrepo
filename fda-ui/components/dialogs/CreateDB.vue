<template>
  <v-card>
    <v-card-title class="headline">
      Database
    </v-card-title>
    <v-card-text>
      <v-form v-model="formValid">
        <v-text-field
          id="dbname"
          v-model="database"
          label="Name"
          :rules="[v => !!v || $t('Required')]"
          required />
        <v-select
          v-model="engine"
          label="Engine"
          :items="engines"
          :loading="loading"
          item-text="label"
          :rules="[v => !!v || $t('Required')]"
          return-object
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
        id="createDB"
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
      database: null,
      engine: null,
      engines: [],
      container: null
    }
  },
  beforeMount () {
    this.getImages()
  },
  methods: {
    cancel () {
      this.$parent.$parent.$parent.createDbDialog = false
    },
    async getImages () {
      this.loading = true
      let res
      try {
        res = await this.$axios.get('/api/image/')
        this.engines = res.data
        console.debug('engines', this.engines)
      } catch (err) {
        this.$toast.error('Failed to fetch supported engines. Try reload the page.')
      }
      this.loading = false
    },
    async createDB () {
      this.loading = true

      let res
      // TODO list images and create image if needed
      // try {
      //   res = await this.$axios.get('/api/container/api/image/')
      //   console.log(res.data)
      //   return
      // } catch (e) {
      //   console.log(e)
      // }
      //
      // create a container
      let containerId
      const isPublic = false
      console.debug('model', this.engine)
      try {
        res = await this.$axios.post('/api/container/', {
          name: this.database,
          repository: this.engine.repository,
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
          containerId,
          isPublic
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
