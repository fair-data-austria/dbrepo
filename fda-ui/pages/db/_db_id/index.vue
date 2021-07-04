<template>
  <div v-if="db">
    <DBToolbar />
    <v-tabs-items v-model="tab">
      <v-tab-item>
        <v-card flat>
          <v-card-title>
            {{ publisher }}
          </v-card-title>
          <v-card-subtitle>
            {{ description }}
          </v-card-subtitle>
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
      return this.db.description === null ? '(no description)' : ''
    },
    publisher () {
      return this.db.publisher === null ? '(no publisher)' : ''
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
