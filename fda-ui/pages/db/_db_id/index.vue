<template>
  <div v-if="db">
    <DBToolbar />
    <v-tabs-items v-model="tab">
      <v-tab-item>
        <v-card flat>
          <v-card-text>
            {{ db.name }}
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
    }
  }
}
</script>
