<template>
  <div>
    <v-toolbar flat>
      <v-toolbar-title>{{ identifier.title }}</v-toolbar-title>
      <v-spacer />
      <v-toolbar-title>
        <v-btn color="blue-grey white--text" class="mr-2" :disabled="!query.execution || identifier.id" @click.stop="persistQueryDialog = true">
          <v-icon left>mdi-fingerprint</v-icon> Persist
        </v-btn>
        <v-btn color="primary" disabled>
          <v-icon left>mdi-run</v-icon> Re-Execute
        </v-btn>
      </v-toolbar-title>
    </v-toolbar>
    <v-card flat>
      <v-card-title v-if="!loading">
        <span v-if="query.execution != null">
          sha256:{{ query.query_hash }}
        </span>
      </v-card-title>
      <v-card-subtitle v-if="!loading">
        <span v-if="query.execution != null">
          Executed {{ formatDate(query.execution) }}
        </span>
        <span v-if="query.execution == null">
          Query was never executed
        </span>
      </v-card-subtitle>
      <v-card-text>
        <p>
          <strong>Query</strong>
        </p>
        <div>
          <p>Persistent Identifier</p>
          <p v-if="identifier.id">
            <code>https://dbrepo.ossdip.at/pid/{{ identifier.id }}</code>
          </p>
          <p>Statement</p>
          <v-alert
            border="left"
            color="grey lighten-4 black--text">
            <pre>{{ query.query }}</pre>
          </v-alert>
          <p>
            Hash: <code>sha256:{{ query.query_hash }}</code>
          </p>
        </div>
        <p class="mt-2">
          <strong>Description</strong>
        </p>
        <div>
          <p v-if="!identifier.description">
            (empty) &#8212; <a href="#" @click.stop="persistQueryDialog = true">modify</a>
          </p>
          <p v-if="identifier.description">{{ identifier.description }}</p>
        </div>
        <p class="mt-2">
          <strong>Result</strong>
        </p>
        <p>
          Hash: <code>{{ query.result_hash }}</code>
        </p>
        <p>
          Rows: <code>{{ query.result_number }}</code>
        </p>
      </v-card-text>
    </v-card>
    <v-dialog
      v-model="persistQueryDialog"
      persistent
      max-width="640">
      <PersistQuery @close="persistQueryDialog = false" />
    </v-dialog>
  </div>
</template>
<script>
import { format } from 'date-fns'
import PersistQuery from '@/components/dialogs/PersistQuery'

export default {
  name: 'QueryShow',
  components: {
    PersistQuery
  },
  data () {
    return {
      query: {
        id: this.$route.params.query_id,
        database_id: null,
        query: null,
        query_hash: null,
        result_hash: null,
        result_number: null,
        execution: null,
        created: null
      },
      identifier: {
        id: null,
        dbid: null,
        qid: null,
        title: null,
        description: null,
        visibility: null,
        doi: null,
        creators: []
      },
      persistQueryExists: false,
      persistQueryDialog: false,
      loading: true
    }
  },
  mounted () {
    this.loadMetadata()
  },
  methods: {
    formatDate (d) {
      return format(new Date(d), 'dd.MM.yyyy HH:mm:ss')
    },
    async loadMetadata () {
      this.loading = true
      try {
        const res = await this.$axios.get(`/api/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/query/${this.$route.params.query_id}`)
        console.debug('query', res.data)
        this.query = res.data
      } catch (err) {
        console.error('Could not load query', err)
        this.$toast.error('Could not load query')
        this.loading = false
      }
      try {
        this.loading = true
        const res = await this.$axios.get(`/api/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/identifier?qid=${this.$route.params.query_id}`)
        this.identifier = res.data[0]
        console.debug('identifier', res.data[0])
      } catch (err) {
        if (err.response.status !== 404) {
          console.error('Could not load identifier', err)
          this.$toast.error('Could not load identifier')
        }
        this.loading = false
      }
      this.loading = false
    }
  }
}
</script>

<style>
</style>
