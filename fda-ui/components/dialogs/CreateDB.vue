<template>
  <div>
    <v-form ref="form" v-model="valid" @submit.prevent="submit">
      <v-card>
        <v-progress-linear v-if="loading" :color="loadingColor" :indeterminate="!error" />
        <v-card-title>
          Create Database
        </v-card-title>
        <v-card-text>
          <v-alert
            border="left"
            color="amber lighten-4 black--text">
            Choose an expressive database name and select a database engine.
          </v-alert>
          <v-text-field
            id="database"
            v-model="database"
            name="database"
            label="Name *"
            autofocus
            :rules="[v => notEmpty(v) || $t('Required')]"
            required />
          <v-textarea
            id="description"
            v-model="description"
            name="description"
            rows="2"
            label="Description *"
            :rules="[v => notEmpty(v) || $t('Required')]"
            required />
          <v-select
            id="engine"
            v-model="engine"
            name="engine"
            label="Engine *"
            :items="engines"
            :item-text="item => `${item.repository}:${item.tag}`"
            :rules="[v => !!v || $t('Required')]"
            return-object
            required />
          <v-checkbox
            id="public"
            v-model="isPublic"
            name="public"
            disabled
            label="Public" />
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
            :disabled="!valid || loading"
            color="primary"
            type="submit"
            @click="createDB">
            Create
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-form>
  </div>
</template>

<script>
export default {
  data () {
    return {
      valid: false,
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
    },
    token () {
      return this.$store.state.token
    }
  },
  mounted () {
    this.getImages()
  },
  methods: {
    submit () {
      this.$refs.form.validate()
    },
    cancel () {
      this.$parent.$parent.$parent.$parent.createDbDialog = false
    },
    async getImages () {
      let res
      try {
        this.loading = true
        this.error = false
        res = await this.$axios.get('/api/image')
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
    notEmpty (str) {
      return typeof str === 'string' && str.trim().length > 0
    },
    async createDB () {
      let res
      // create a container
      let containerId
      console.debug('model', this.engine)
      try {
        this.loading = true
        this.error = false
        res = await this.$axios.post('/api/container', {
          name: this.database.trim(),
          description: this.description.trim(),
          repository: this.engine.repository,
          tag: this.engine.tag
        }, {
          headers: { Authorization: `Bearer ${this.token}` }
        })
        containerId = res.data.id
        console.debug('created container', res.data)
        this.loading = false
      } catch (err) {
        this.error = true
        this.loading = false
        if (err.status === 401) {
          this.$toast.error('Authentication missing')
          console.error('permission denied', err)
          return
        }
        console.error('failed to create container', err)
        this.$toast.error('Could not create container.')
        return
      }

      // start the container
      try {
        this.loading = true
        this.error = false
        res = await this.$axios.put(`/api/container/${containerId}`,
          { action: 'START' }, {
            headers: { Authorization: `Bearer ${this.token}` }
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
          res = await this.$axios.post(`/api/container/${containerId}/database`, {
            name: this.database.trim(),
            description: this.description.trim(),
            is_public: this.isPublic
          }, {
            headers: { Authorization: `Bearer ${this.token}` }
          })
          console.debug('created database', res)
          break
        } catch (err) {
          console.debug('wait', res)
          await this.sleep(3000)
        }
      }
      if (res.status !== 201) {
        this.loading = false
        this.error = true
        this.$toast.error('Could not create database.')
        return
      }
      this.loading = false
      this.$toast.success(`Database "${res.data.name}" created.`)
      this.$emit('close')
      await this.$router.push(`/container/${containerId}/database/${res.data.id}/info`)
    }
  }
}
</script>
