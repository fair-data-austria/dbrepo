<template>
  <div>
    <v-card>
      <v-progress-linear v-if="loading" :color="loadingColor" :indeterminate="!error" />
      <v-card-title>
        Persist Query and Result
      </v-card-title>
      <v-card-text>
        <v-alert
          border="left"
          color="amber lighten-4 black--text">
          Choose an expressive query title and describe what result the query produces.
        </v-alert>
        <v-form v-model="formValid" autocomplete="off">
          <v-text-field
            id="title"
            v-model="identifier.title"
            name="title"
            label="Query Title"
            :rules="[v => !!v || $t('Required')]"
            required />
          <v-textarea
            id="description"
            v-model="identifier.description"
            name="description"
            rows="2"
            label="Query Description"
            :rules="[v => !!v || $t('Required')]"
            required />
          <v-select
            id="visibility"
            v-model="identifier.visibility"
            :items="visibility"
            item-value="value"
            item-text="name"
            label="Visibility"
            :rules="[v => !!v || $t('Required')]"
            disabled
            required />
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
          @click="persist">
          Persist
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
      visibility: [{
        name: 'Public',
        value: 'EVERYONE'
      },
      {
        name: 'Organization',
        value: 'TRUSTED'
      },
      {
        name: 'Hidden',
        value: 'SELF'
      }],
      identifier: {
        cid: parseInt(this.$route.params.container_id),
        dbid: parseInt(this.$route.params.database_id),
        qid: parseInt(this.$route.params.query_id),
        title: null,
        description: null,
        visibility: 'SELF',
        doi: null,
        creators: []
      }
    }
  },
  computed: {
    loadingColor () {
      return this.error ? 'red lighten-2' : 'primary'
    }
  },
  beforeMount () {
  },
  methods: {
    cancel () {
      this.$parent.$parent.$parent.persistQueryDialog = false
    },
    sleep (ms) {
      return new Promise((resolve) => {
        setTimeout(resolve, ms)
      })
    },
    async persist () {
      console.debug('identifier data', this.identifier)
      this.loading = true
      let res
      try {
        res = await this.$axios.post(`/api/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/identifier`, this.identifier)
        console.debug('persist', res.data)
      } catch (err) {
        this.$toast.error('Failed to persist query')
        console.error('persist failed', err)
      }
      this.$toast.success('Query persisted.')
      this.$emit('close')
    }
  }
}
</script>
