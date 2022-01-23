<template>
  <div v-if="db">
    <DBToolbar />
    <v-tabs-items v-if="!loading" v-model="tab">
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
              Created {{ formatDate(db.created) }}
            </span>
          </v-card-text>
        </v-card>
      </v-tab-item>
    </v-tabs-items>
    <v-breadcrumbs :items="items" class="pa-0 mt-2" />
  </div>
</template>

<script>
import DBToolbar from '@/components/DBToolbar'
import { format } from 'date-fns'

export default {
  components: {
    DBToolbar
  },
  data () {
    return {
      loading: false,
      items: [
        { text: 'Databases', href: '/container' },
        { text: `${this.$route.params.database_id}`, href: `/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}/info` }
      ]
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
      this.loading = true
      if (this.db != null && this.db.id === this.$route.params.database_id) {
        this.loading = false
        return
      }
      try {
        const res = await this.$axios.get(`/api/container/${this.$route.params.container_id}/database/${this.$route.params.database_id}`)
        console.debug('database', res.data)
        this.$store.commit('SET_DATABASE', res.data)
        this.loading = false
      } catch (err) {
        this.$toast.error('Could not load database.')
        this.loading = false
      }
    },
    formatDate (d) {
      return format(new Date(d), 'dd.MM.yyyy HH:mm:ss.SSS')
    }
  }
}
</script>
