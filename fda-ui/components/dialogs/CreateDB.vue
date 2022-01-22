<template>
  <div>
    <v-card>
      <v-progress-linear v-if="loading" :color="loadingColor" :indeterminate="!error" />
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
          :disabled="!formValid || loading"
          color="primary"
          @click="createDB">
          Create
        </v-btn>
      </v-card-actions>
    </v-card>
  </div>
</template>

<script>
export default {
  data () {
    return {
      formValid: false,
      loading: false,
      error: false,
      database: null,
      description: null,
      isPublic: true,
      engine: null,
      engines: [],
      container: null
    }
  },
  computed: {
    loadingColor () {
      return this.error ? 'red lighten-2' : 'primary'
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
      let res
      try {
        this.loading = true
        this.error = false
        res = await this.$axios.get('/api/image/')
        this.engines = res.data
        console.debug('engines', this.engines)
        this.loading = false
      } catch (err) {
        this.error = true
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
      let res
      // create a container
      let containerId
      console.debug('model', this.engine)
      try {
        this.loading = true
        this.error = false
        res = await this.$axios.post('/api/container/', {
          name: this.database,
          description: this.description,
          repository: this.engine.repository,
          tag: this.engine.tag
        })
        containerId = res.data.id
        console.debug('created container', res.data)
        this.loading = false
      } catch (err) {
        this.error = true
        this.$toast.error('Could not create container. Try another name.')
        return
      }

      // start the container
      try {
        this.loading = true
        this.error = false
        res = await this.$axios.put(`/api/container/${containerId}`, {
          action: 'START'
        })
        console.debug('started container', res.data)
      } catch (err) {
        this.error = true
        this.$toast.error('Could not start container.')
        return
      }

      // Pause.
      // DB fails to create when container has not started up yet
      await new Promise(resolve => setTimeout(resolve, 2000))

      // wait for it to finish
      this.loading = true
      this.error = false
      for (let i = 0; i < 5; i++) {
        try {
          res = await this.$axios.post('/api/database/', {
            name: this.database,
            containerId,
            description: this.description,
            isPublic: this.isPublic
          }, { progress: false })
          break
        } catch (err) {
          console.debug('wait', res)
          await this.sleep(3000)
        }
      }
      if (res.status !== 201) {
        this.error = true
        this.$toast.error('Could not create database.')
        return
      }
      this.loading = false
      this.$toast.success(`Database "${res.data.name}" created.`)
      this.$emit('close')
      await this.$router.push(`/databases/${containerId}/info`)
    }
  }
}
</script>
