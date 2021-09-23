<template>
  <div>
    <v-card v-if="queries.length === 0" flat>
      <v-card-title>
        (no queries)
      </v-card-title>
    </v-card>
    <v-expansion-panels v-if="queries.length > 0" accordion>
      <v-expansion-panel v-for="(item, i) in queries" :key="i" @click="details(item)">
        <v-expansion-panel-header>
          {{ item.query }}
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
                    <v-icon>mdi-fingerprint</v-icon>
                  </v-list-item-icon>
                  <v-list-item-content>
                    <v-list-item-title>
                      DOI: <code v-if="queryDetails.doi">{{ queryDetails.doi }}</code>
                      <span v-if="!queryDetails.doi">(no identifier issued)</span>
                    </v-list-item-title>
                  </v-list-item-content>
                </v-list-item>
                <v-list-item>
                  <v-list-item-icon>
                    <v-icon>mdi-api</v-icon>
                  </v-list-item-icon>
                  <v-list-item-content>
                    <v-list-item-title>
                      Query Hash: <code>{{ queryDetails.queryHash }}</code>
                    </v-list-item-title>
                  </v-list-item-content>
                </v-list-item>
                <v-list-item>
                  <v-list-item-icon>
                    <v-icon>mdi-clock-outline</v-icon>
                  </v-list-item-icon>
                  <v-list-item-content>
                    <v-list-item-title>
                      Execution Timestamp: {{ queryDetails.executionTimestamp }}
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
              <v-btn color="primary" :to="`/databases/${$route.params.database_id}/queries/${item.id}`">
                <v-icon left>mdi-run</v-icon> Execute Again
              </v-btn>
            </v-col>
          </v-row>
        </v-expansion-panel-content>
      </v-expansion-panel>
    </v-expansion-panels>
  </div>
</template>

<script>
export default {
  data () {
    return {
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
  mounted () {
    this.$root.$on('query-create', this.refresh)
    this.refresh()
  },
  methods: {
    async refresh () {
      // XXX same as in QueryBuilder
      let res
      try {
        res = await this.$axios.get(`/api/database/${this.$route.params.database_id}/querystore`)
        console.debug('queries', res)
        this.queries = res.data
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
