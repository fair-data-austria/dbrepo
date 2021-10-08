<template>
  <div v-if="!loading">
    <v-progress-linear v-if="loading" :color="loadingColor" :indeterminate="!error" />
    <v-toolbar flat>
      <v-btn id="zenodo-logo" class="mr-2" :style="`background-image:url(${zenodoLogo});`" disabled />
      <v-toolbar-title>Cite Query No. {{ queryId }}</v-toolbar-title>
      <v-spacer />
      <v-toolbar-title>
        <v-btn color="primary" :disabled="!valid" @click="submit()">
          <v-icon left>mdi-publish</v-icon>
          Publish
        </v-btn>
      </v-toolbar-title>
    </v-toolbar>
    <v-form
      ref="form"
      v-model="valid"
      lazy-validation>
      <v-card flat>
        <v-card-subtitle v-if="!loading">
          Executed {{ query.execution_timestamp }}
        </v-card-subtitle>
        <v-card-text>
          <v-alert
            v-if="query.query"
            border="left"
            class="mb-6"
            color="amber lighten-4">
            <pre>{{ query.query }}</pre>
          </v-alert>
          <v-select
            v-model="data.metadata.access_right"
            :items="accessRights"
            item-text="name"
            item-value="value"
            class="col-lg-6 col-md-8 pa-0"
            label="Access Right" />
          <v-text-field
            v-model="data.metadata.title"
            class="pa-0"
            :rules="[rules.required]"
            label="Query Title"
            required />
          <v-textarea
            v-model="data.metadata.description"
            class="pa-0 mt-4"
            :rules="[rules.required, rules.descriptionMin]"
            label="Query Description"
            counter
            rows="4"
            hint="Minimum 100 Characters"
            required />
        </v-card-text>
      </v-card>
      <v-card class="space mt-4" flat>
        <v-card-text>
          <v-row v-for="(author,i) in data.metadata.creators" :key="i">
            <v-col
              cols="12"
              md="4">
              <v-text-field
                v-model="author.name"
                :rules="[rules.required]"
                class="pa-0"
                label="Firstname Surname"
                required />
            </v-col>
            <v-col
              cols="12"
              md="4">
              <v-text-field
                v-model="author.affiliation"
                :rules="[rules.required]"
                class="pa-0"
                label="Affiliation"
                required />
            </v-col>
            <v-col
              cols="12"
              :md="i !== 0 ? 3 : 4">
              <v-text-field
                v-model="author.orcid"
                class="pa-0"
                label="ORCiD"
                required />
            </v-col>
            <v-col
              v-if="i !== 0"
              cols="12"
              md="1">
              <v-btn @click="removeAuthor(i)">Remove</v-btn>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>
      <v-btn color="blue-grey" class="mt-4 mb-4 white--text" @click="addAuthor()">
        <v-icon left>mdi-plus</v-icon>
        Add Author
      </v-btn>
    </v-form>
  </div>
</template>
<script>
export default {
  name: 'QueryDoiMetadata',
  components: {},
  data () {
    return {
      loading: false,
      error: false,
      valid: false,
      data: {
        metadata: {
          access_right: null,
          creators: [{
            name: null,
            affiliation: null,
            orcid: null
          }],
          title: null,
          description: null,
          upload_type: 'DATASET'
        }
      },
      query: {
        hash: null,
        query: null,
        execution_timestamp: null,
        result_hash: null,
        result_number: null,
        doi: null
      },
      rules: {
        required: value => !!value || 'Required',
        descriptionMin: value => (value || '').length >= 100 || 'Minimum 100 characters'
      }
    }
  },
  computed: {
    databaseId () {
      return this.$route.params.database_id
    },
    queryId () {
      return this.$route.params.query_id
    },
    accessRights () {
      return [{ name: 'Open', value: 'OPEN' }]
    },
    zenodoLogo () {
      return require('assets/img/zenodo-logo.png')
    },
    loadingColor () {
      return this.error ? 'red lighten-2' : 'primary'
    }
  },
  mounted () {
    this.loadData()
  },
  methods: {
    async loadData () {
      try {
        this.loading = true
        const res = await this.$axios.get(`/api/database/${this.databaseId}/metadata/query/${this.queryId}`)
        this.query = res.data
        console.debug('query data', res.data)
        this.loading = false
      } catch (err) {
        this.error = true
        this.$toast.error('Could not load table data.')
      }
    },
    async submit () {
      this.$refs.form.validate()
      console.debug('form', this.data)
      this.loading = true
      const res = await this.$axios.post(`/api/database/${this.databaseId}/cite/query/${this.queryId}`)
      console.debug('create deposit', res.data)
    },
    addAuthor () {
      this.data.metadata.creators.push({
        name: null,
        affiliation: null,
        orcid: null
      })
    },
    removeAuthor (index) {
      this.data.metadata.creators.splice(index, 1)
    }
  }
}
</script>

<style lang="scss" scoped>
/* these are taked from solarized-light (plugins/vendors.js), to override the
main.scss file from vuetify, because it paints it red */
::v-deep code {
  background: #fdf6e3;
  color: #657b83;
}

.spacer {
  display: flex;
  flex: 0 1;
}

#zenodo-logo {
  background-size: cover;
  background-position: center center;
  background-color: #0656b4;
  width: 5rem;
}
</style>
