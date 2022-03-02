<template>
  <div>
    <v-progress-linear v-if="loading" :indeterminate="!error" />
    <v-tabs-items>
      <v-card v-if="!loading && queries.length === 0" flat>
        <v-card-title>
          (no queries)
        </v-card-title>
      </v-card>
      <v-expansion-panels v-if="!loading && queries.length > 0" accordion>
        <v-expansion-panel v-for="(item, i) in queries" :key="i" @click="details(item)">
          <v-expansion-panel-header>
            <span v-bind:class="{'font-weight-black': item.identifier !== undefined}">{{ title(item) }}</span>
          </v-expansion-panel-header>
          <v-expansion-panel-content>
            <v-row dense>
              <v-col>
                <v-list dense>
                  <v-list-item>
                    <v-list-item-icon>
                      <v-icon>mdi-information-variant</v-icon>
                    </v-list-item-icon>
                    <v-list-item-content>
                      <v-list-item-title>
                        ID
                      </v-list-item-title>
                      <v-list-item-content>
                        {{ queryDetails.id }}
                      </v-list-item-content>
                      <v-list-item-title v-if="queryDetails.identifier !== undefined">
                        Persistent ID
                      </v-list-item-title>
                      <v-list-item-content v-if="queryDetails.identifier !== undefined">
                        https://dbrepo.ossdip.at/pid/{{ queryDetails.identifier.id }}
                      </v-list-item-content>
                    </v-list-item-content>
                  </v-list-item>
                  <v-list-item v-if="queryDetails.identifier !== undefined">
                    <v-list-item-icon>
                      <v-icon>mdi-text</v-icon>
                    </v-list-item-icon>
                    <v-list-item-content>
                      <v-list-item-title>
                        Description
                      </v-list-item-title>
                      <v-list-item-content>
                        {{ queryDetails.identifier.description }}
                      </v-list-item-content>
                    </v-list-item-content>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-icon>
                      <v-icon>mdi-clock-outline</v-icon>
                    </v-list-item-icon>
                    <v-list-item-content>
                      <v-list-item-title>
                        Execution Timestamp
                      </v-list-item-title>
                      <v-list-item-content>
                        {{ queryDetails.execution }}
                      </v-list-item-content>
                    </v-list-item-content>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-icon>
                      <v-icon>mdi-content-save</v-icon>
                    </v-list-item-icon>
                    <v-list-item-content>
                      <v-list-item-title>
                        Query
                      </v-list-item-title>
                      <v-list-item-content>
                        <code>{{ queryDetails.query }}</code>
                      </v-list-item-content>
                    </v-list-item-content>
                  </v-list-item>
                </v-list>
              </v-col>
            </v-row>
            <v-row dense>
              <v-col>
                <v-btn color="blue-grey white--text" :to="`/container/${$route.params.container_id}/database/${databaseId}/query/${item.id}`">
                  More
                </v-btn>
              </v-col>
            </v-row>
          </v-expansion-panel-content>
        </v-expansion-panel>
      </v-expansion-panels>
    </v-tabs-items>
  </div>
</template>

<script>
export default {
  data () {
    return {
      loading: false,
      queries: [],
      identifiers: [],
      queryDetails: {
        id: null,
        doi: null,
        queryHash: null,
        executionTimestamp: null,
        columns: []
      }
    }
  },
  computed: {
    databaseId () {
      return this.$route.params.database_id
    }
  },
  mounted () {
    this.$root.$on('query-create', this.refresh)
    this.refresh()
  },
  methods: {
    async refresh () {
      // XXX same as in QueryBuilder
      let res
      try {
        this.loading = true
        res = await this.$axios.get(`/api/container/${this.$route.params.container_id}/database/${this.databaseId}/query`)
        this.queries = res.data
        console.debug('queries', this.queries)
        try {
          this.loading = true
          const res = await this.$axios.get(`/api/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/identifier`)
          this.identifiers = res.data
          console.debug('identifiers', this.identifiers)
          this.queries.forEach((query) => {
            const id = this.identifiers.find(id => id.qid === query.id)
            console.debug('id', id)
            if (id !== undefined) {
              query.identifier = id
            }
          })
        } catch (err) {
          console.error('Failed to get identifiers', err)
        }
        this.loading = false
      } catch (err) {
        this.$toast.error('Could not list queries.')
      }
    },
    title (query) {
      if (query.identifier !== undefined) {
        return query.identifier.title
      }
      return `Query ${query.id}`
    },
    details (query) {
      this.queryDetails = query
    }
  }
}
</script>

<style>

</style>
