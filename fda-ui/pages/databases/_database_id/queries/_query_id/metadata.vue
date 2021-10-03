<template>
  <div>
    <v-form
      ref="form"
      lazy-validation>
      <v-card>
        <v-card-title v-if="!loading">
          Cite Query No. {{ queryId }}
        </v-card-title>
        <v-card-subtitle v-if="!loading">
          Executed {{ query.execution_timestamp }}
        </v-card-subtitle>
        <v-card-text>
          <p>
            This service allows researchers to publish their dataset on <a href="https://zenodo.org/">Zenodo.org</a>, a
            open science preservation service provided by European Organization for Nuclear Research CERN with address
            in Geneva, Switzerland. Note that once published, your dataset cannot be deleted anymore and a DOI is issued.
          </p>
          <v-alert
            border="left"
            color="amber lighten-4">
            <pre>{{ query.query }}</pre>
          </v-alert>
          <v-select
            :items="accessRights"
            class="col-lg-4 col-md-6 pa-0"
            label="Access Right"></v-select>
          <v-text-field
            v-model="title"
            class="pa-0"
            :rules="[rules.required]"
            label="Query Title"
            required></v-text-field>
          <v-textarea
            v-model="description"
            class="pa-0"
            :rules="[rules.required, rules.descriptionMin]"
            label="Query Description"
            counter
            rows="2"
            hint="Minimum 100 Characters"
            required></v-textarea>
        </v-card-text>
      </v-card>
      <v-btn color="blue-grey" class="mt-4 mb-4 white--text" x-small>
        <v-icon left>mdi-plus</v-icon>
        Add Author
      </v-btn>
      <v-card class="space">
        <v-card-text>
          <v-row>
            <v-col
              cols="12"
              md="4">
              <v-text-field
                :rules="[rules.required]"
                class="pa-0"
                label="Firstname Surname"
                required></v-text-field>
            </v-col>
            <v-col
              cols="12"
              md="4">
              <v-text-field
                :rules="[rules.required]"
                class="pa-0"
                label="Affiliation"
                required></v-text-field>
            </v-col>
            <v-col
              cols="12"
              md="4">
              <v-text-field
                class="pa-0"
                label="ORCiD"
                required></v-text-field>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>
      <v-btn color="primary" class="mt-4">
        <v-icon left>mdi-publish</v-icon>
        Publish
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
      title: null,
      description: null,
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
    queryId () {
      return this.$route.params.query_id
    },
    databaseId () {
      return this.$route.params.database_id
    },
    accessRights () {
      return ['open']
    }
  },
  mounted () {
    this.loadData()
  },
  methods: {
    async loadData () {
      try {
        const res = await this.$axios.get(`/api/database/${this.databaseId}/querystore/${this.queryId}`)
        this.query = res.data
        console.debug('query data', res.data)
      } catch (err) {
        this.$toast.error('Could not load table data.')
      }
      this.loading = false
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
</style>
