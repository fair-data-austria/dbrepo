<template>
  <div>
    <v-toolbar flat>
      <v-toolbar-title>{{ identifier.title }}</v-toolbar-title>
      <v-spacer />
      <v-toolbar-title>
        <v-btn color="blue-grey white--text" class="mr-2" :disabled="!query.execution || identifier.id || !token" @click.stop="persistQueryDialog = true">
          <v-icon left>mdi-fingerprint</v-icon> Persist
        </v-btn>
        <v-btn v-if="false" color="primary" :disabled="!token" @click.stop="reExecute">
          <v-icon left>mdi-run</v-icon> Re-Execute
        </v-btn>
      </v-toolbar-title>
    </v-toolbar>
    <v-card v-if="!loading" class="pb-2" flat>
      <v-card-title>
        Query Information
      </v-card-title>
      <v-card-subtitle>
        <span v-if="query.created != null">
          Created {{ formatDate(query.created) }}
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
          <p>
            Persistent Identifier: <code v-if="identifier.id">https://dbrepo.ossdip.at/pid/{{ identifier.id }}</code><span v-if="!identifier.id">(empty)</span>
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
          Hash: <code v-if="query.result_hash">{{ query.result_hash }}</code><span v-if="!query.result_hash">(empty)</span>
        </p>
        <p>
          Rows: <code v-if="query.result_number">{{ query.result_number }}</code><span v-if="!query.result_number">(empty)</span>
        </p>
        <p>
          Executed: <code v-if="query.execution">{{ query.execution }}</code><span v-if="!query.execution">(empty)</span>
        </p>
        <p class="mt-2">
          <strong>Creator</strong>
        </p>
        <p>
          Username: <code v-if="query.username">{{ query.username }}</code><span v-if="!query.username">(empty)</span>
        </p>
      </v-card-text>
      <QueryResults ref="queryResults" v-model="query.id" class="ml-2 mr-2 mt-0" />
    </v-card>
    <v-breadcrumbs :items="items" class="pa-0 mt-2" />
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
      items: [
        { text: 'Databases', to: '/container', activeClass: '' },
        { text: `${this.$route.params.database_id}`, to: `/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}`, activeClass: '' },
        { text: 'Queries', to: `/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/query`, activeClass: '' },
        { text: `${this.$route.params.query_id}`, to: `/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/query/${this.$route.params.query_id}`, activeClass: '' }
      ],
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
  computed: {
    token () {
      return this.$store.state.token
    },
    headers () {
      if (this.token === null) {
        return null
      }
      return { Authorization: `Bearer ${this.token}` }
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

      // refresh QueryResults table
      setTimeout(() => {
        this.$refs.queryResults.execute()
      }, 200)
    },
    async reExecute () {
      try {
        this.loading = true
        const res = await this.$axios.put(`/api/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/query/${this.$route.params.query_id}`, {}, {
          headers: this.headers
        })
        console.debug('re-execute query', res.data)
      } catch (err) {
        console.error('Could not re-execute query', err)
        this.$toast.error('Could not re-execute query')
      }
      this.loading = false
    }
  }
}
</script>

<style>
</style>
