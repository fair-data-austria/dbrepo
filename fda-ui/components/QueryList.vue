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
            Query {{ item.id }}
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
                        ID: {{ queryDetails.id }}
                      </v-list-item-title>
                    </v-list-item-content>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-icon>
                      <v-icon>mdi-table-of-contents</v-icon>
                    </v-list-item-icon>
                    <v-list-item-content>
                      <v-list-item-title>
                        Result Number: <code>{{ queryDetails.result_number }}</code>
                      </v-list-item-title>
                    </v-list-item-content>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-icon>
                      <v-icon>mdi-clock-outline</v-icon>
                    </v-list-item-icon>
                    <v-list-item-content>
                      <v-list-item-title>
                        Execution Timestamp: <code>{{ queryDetails.execution }}</code>
                      </v-list-item-title>
                    </v-list-item-content>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-icon>
                      <v-icon>mdi-content-save</v-icon>
                    </v-list-item-icon>
                    <v-list-item-content>
                      <v-list-item-title>
                        Query: <code>{{ queryDetails.query }}</code>
                      </v-list-item-title>
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
        this.loading = false
      } catch (err) {
        this.$toast.error('Could not list queries.')
      }
    },
    details (query) {
      this.queryDetails = query
    }
  }
}
</script>

<style>

</style>
