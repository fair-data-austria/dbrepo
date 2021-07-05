<template>
  <v-app dark>
    <v-navigation-drawer v-model="drawer" fixed app>
      <v-list>
        <v-list-item
          v-for="(item, i) in filteredItems"
          :key="i"
          :to="item.to"
          router
          exact>
          <v-list-item-action>
            <v-icon>{{ item.icon }}</v-icon>
          </v-list-item-action>
          <v-list-item-content>
            <v-list-item-title v-text="item.title" />
          </v-list-item-content>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>
    <v-app-bar fixed app>
      <v-app-bar-nav-icon @click.stop="drawer = !drawer" />
      <v-toolbar-title v-text="title" />
      <v-spacer />
      <v-menu bottom offset-y left>
        <template v-slot:activator="{ on, attrs }">
          <v-btn
            icon
            v-bind="attrs"
            v-on="on">
            <v-icon>mdi-dots-vertical</v-icon>
          </v-btn>
        </template>

        <v-list>
          <v-list-item
            v-for="locale in availableLocales"
            :key="locale.code"
            :to="switchLocalePath(locale.code)">
            <v-list-item-title>{{ locale.name }}</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </v-app-bar>
    <v-main>
      <v-container>
        <nuxt />
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import {
  mdiDatabase,
  mdiTable,
  mdiFileDelimited,
  mdiDatabaseSearch
} from '@mdi/js'

export default {
  name: 'DefaultLayout',
  data () {
    return {
      drawer: false,
      items: [
        {
          icon: 'mdi-home',
          title: 'Home',
          to: '/'
        },
        {
          icon: mdiDatabase,
          title: 'Databases',
          to: '/databases'
        },
        {
          icon: mdiTable,
          title: 'Tables',
          to: '/tables',
          needsContainer: true
        },
        {
          icon: mdiFileDelimited,
          title: 'Import CSV',
          to: '/import_csv',
          needsContainer: true
        },
        {
          icon: mdiDatabaseSearch,
          title: 'SQL Query',
          to: '/sql',
          needsContainer: true
        }
      ],
      title: 'FAIR Data Repository'
    }
  },
  computed: {
    themeLabel () {
      return this.dark ? 'Dark' : 'Light'
    },
    availableLocales () {
      return this.$i18n.locales.filter(i => i.code !== this.$i18n.locale)
    },
    container () {
      return this.$store.state.container
    },
    filteredItems () {
      return this.items.filter((x) => {
        if (x.needsContainer && !this.container) { return false }
        return true
      })
    },
    db () {
      return this.$store.state.db
    }
  },
  watch: {
    $route () {
      this.loadDB()
    }
  },
  mounted () {
    this.loadDB()
  },
  methods: {
    async loadDB () {
      // this route already loads the db itself
      if (this.$route.name.startsWith('db-db_id___')) {
        return
      }

      if (this.$route.params.db_id && !this.db) {
        try {
          const res = await this.$axios.get(`/api/database/${this.$route.params.db_id}`)
          this.$store.commit('SET_DATABASE', res.data)
        } catch (err) {
          this.$toast.error('Could not load database.')
        }
      }
    }
  }
}
</script>
