<template>
  <div v-if="db">
    <DBToolbar />
    <v-tabs-items v-model="tab">
      <v-tab-item>
        <v-card flat>
          <v-card-title>
            <span>{{ db.internalName }}</span>
            <v-progress-circular v-if="loading" :size="20" :width="3" indeterminate color="primary" />
          </v-card-title>
          <v-card-subtitle>
            {{ publisher }}, {{ db.image.repository }}:{{ db.image.tag }}
          </v-card-subtitle>
          <v-card-text>
            <blockquote>
              <p>{{ description }}</p>
            </blockquote>
            <span>
              Created {{ db.created }}
            </span>
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
      loading: false
    }
  },
  computed: {
    tab () {
      return 0
    },
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
  mounted () {
    this.init()
  },
  methods: {
    async init () {
      if (this.db != null) {
        return
      }
      try {
        this.loading = true
        const res = await this.$axios.get(`/api/database/${this.$route.params.database_id}`)
        console.debug('database', res.data)
        this.$store.commit('SET_DATABASE', res.data)
        this.loading = false
      } catch (err) {
        this.$toast.error('Could not load database.')
        this.loading = false
      }
    }
  }
}
</script>
