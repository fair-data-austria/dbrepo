<template>
  <div v-if="db">
    <DBToolbar />
    <v-tabs-items v-model="tab">
      <v-tab-item>
        <v-card flat>
          <v-card-title>
            {{ db.internalName }}
          </v-card-title>
          <v-card-subtitle>
            {{ publisher }}
          </v-card-subtitle>
          <v-card-text>
            {{ description }}
          </v-card-text>
          <v-card-text>
            <v-chip class="ma-2" label>
              <v-icon left>
                mdi-label
              </v-icon>
              {{ db.image.repository }}
            </v-chip>
            <v-chip class="ma-2" label>
              <v-icon left>
                mdi-label
              </v-icon>
              {{ db.image.tag }}
            </v-chip>
          </v-card-text>
        </v-card>
      </v-tab-item>
    </v-tabs-items>
  </div>
</template>

<script>
import DBToolbar from '@/components/DBToolbar'

export default {
  components: {
    DBToolbar
  },
  data () {
    return {
      tab: 0
    }
  },
  computed: {
    db () {
      return this.$store.state.db
    },
    description () {
      return this.db.description === null ? '(no description)' : this.db.description
    },
    publisher () {
      return this.db.publisher === null ? '(no publisher)' : this.db.publisher
    }
  },
  async mounted () {
    try {
      const res = await this.$axios.get(`/api/database/${this.$route.params.db_id}`)
      console.debug('database', res.data)
      this.$store.commit('SET_DATABASE', res.data)
    } catch (err) {
      this.$toast.error('Could not load database.')
    }
  }
}
</script>
