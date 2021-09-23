<template>
  <v-card>
    <v-card-title>
      Create Database
    </v-card-title>
    <v-card-text>
      <v-alert
        border="left"
        color="amber lighten-4">
        Choose an expressive database name and select a database engine.
      </v-alert>
      <v-form v-model="formValid" autocomplete="off">
        <v-text-field
          id="database"
          v-model="database"
          name="database"
          label="Database Name"
          :rules="[v => !!v || $t('Required')]"
          required />
        <v-textarea
          id="description"
          v-model="description"
          name="description"
          rows="2"
          label="Database Description"
          :rules="[v => !!v || $t('Required')]"
          required />
        <v-select
          id="engine"
          v-model="engine"
          name="engine"
          label="Database Engine"
          :items="engines"
          item-text="label"
          :rules="[v => !!v || $t('Required')]"
          return-object
          required />
        <v-checkbox
          id="public"
          v-model="isPublic"
          name="public"
          disabled
          label="Public" />
      </v-form>
    </v-card-text>
    <v-card-actions>
      <v-spacer />
      <v-btn
        class="mb-2"
        @click="cancel">
        Cancel
      </v-btn>
      <v-btn
        id="createDB"
        class="mb-2"
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
      description: null,
      isPublic: true,
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
      this.$parent.$parent.$parent.$parent.createDbDialog = false
    },
    async getImages () {
      this.loading = true
      let res
      try {
        res = await this.$axios.get('/api/image/')
        this.engines = res.data.map((e) => {
          e.disabled = (e.id !== 3)
          return e
        })
        console.debug('engines', this.engines)
      } catch (err) {
        this.$toast.error('Failed to fetch supported engines. Try reload the page.')
      }
      this.loading = false
    },
    sleep (ms) {
      return new Promise((resolve) => {
        setTimeout(resolve, ms)
      })
    },
    async createDB () {
      this.loading = true
      let res
      // create a container
      let containerId
      console.debug('model', this.engine)
      try {
        res = await this.$axios.post('/api/container/', {
          name: this.database,
          description: this.description,
          repository: this.engine.repository,
          tag: this.engine.tag
        })
        containerId = res.data.id
        console.debug('created container', res.data)
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
        console.debug('started container', res.data)
      } catch (err) {
        this.loading = false
        this.$toast.error('Could not start container.')
        return
      }

      // Pause.
      // DB fails to create when container has not started up yet
      await new Promise(resolve => setTimeout(resolve, 2000))

      // create the DB
      for (let i = 0; i < 30; i++) {
        try {
          res = await this.$axios.post('/api/database/', {
            name: this.database,
            containerId,
            description: this.description,
            isPublic: this.isPublic
          }, { progress: false })
          i = 31
        } catch (err) {
          console.debug('wait', res)
          await this.sleep(1000)
        }
      }
      this.loading = false
      if (res.status !== 201) {
        this.$toast.error('Could not create database.')
        return
      }
      this.$toast.success(`Database "${res.data.name}" created.`)
      this.$emit('refresh')
    }
  }
}
</script>
