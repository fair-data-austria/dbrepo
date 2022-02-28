<template>
  <v-app dark>
    <v-navigation-drawer v-model="drawer" fixed app>
      <v-img
        contain
        class="tu-logo"
        src="/tu_logo_512.png" />
      <v-img
        contain
        class="univie-logo"
        src="/univie_logo_512.png" />
      <v-list>
        <v-list-item
          v-for="(item, i) in filteredItems"
          :key="i"
          :to="item.to"
          router>
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
      <v-btn
        v-if="!token"
        class="mr-2 white--text"
        color="blue-grey"
        to="/login">
        <v-icon left>mdi-login</v-icon> Login
      </v-btn>
      <v-btn
        v-if="!token"
        class="mr-2 white--text"
        color="primary"
        to="/signup">
        <v-icon left>mdi-account-plus</v-icon> Signup
      </v-btn>
      {{ username }}
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
          <v-list-item
            @click="switchTheme()">
            {{ nextTheme }} Theme
          </v-list-item>
          <v-list-item
            v-if="token"
            @click="logout">
            Logout
          </v-list-item>
        </v-list>
      </v-menu>
    </v-app-bar>
    <v-main>
      <v-container>
        <nuxt />
      </v-container>
    </v-main>
    <v-footer padless>
      <v-card
        flat
        tile
        width="100%"
        class="red lighten-1 text-center">
        <v-card-text class="black--text">
          This is a <strong>TEST</strong> environment, do not use production/confidential data! — <a href="//github.com/fair-data-austria/dbrepo/issues/new" class="black--text">Report a bug</a>
        </v-card-text>
      </v-card>
    </v-footer>
  </v-app>
</template>

<script>
import {
  mdiDatabase,
  mdiTable,
  mdiFileDelimited,
  mdiDatabaseSearch,
  mdiHome,
  mdiNewspaperVariantOutline,
  mdiCog
} from '@mdi/js'

export default {
  name: 'DefaultLayout',
  data () {
    return {
      drawer: false,
      items: [
        {
          icon: mdiHome,
          title: 'Home',
          to: '/'
        },
        {
          icon: mdiDatabase,
          title: 'Databases',
          to: '/container'
        },
        {
          icon: mdiNewspaperVariantOutline,
          title: 'Publications',
          to: '/publications'
        },
        {
          icon: mdiCog,
          title: 'Privacy',
          to: '/privacy'
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
          to: '/queries',
          needsContainer: true
        }
      ],
      title: 'FAIR Data Austria — Database Repository'
    }
  },
  computed: {
    availableLocales () {
      return this.$i18n.locales.filter(i => i.code !== this.$i18n.locale)
    },
    token () {
      return this.$store.state.token
    },
    username () {
      return this.$store.state.user && this.$store.state.user.username
    },
    nextTheme () {
      return this.$vuetify.theme.dark ? 'Light' : 'Dark'
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
    switchTheme () {
      this.$vuetify.theme.dark = !this.$vuetify.theme.dark
    },
    logout () {
      this.$store.commit('SET_TOKEN', null)
      this.$store.commit('SET_USER', null)
      this.$toast.success('Logged out')
      this.$router.push('/container')
    },
    async loadDB () {
      if (this.$route.params.db_id && !this.db) {
        try {
          const res = await this.$axios.get(`/api/database/${this.$route.params.db_id}`)
          this.$store.commit('SET_DATABASE', res.data)
        } catch (err) {
          console.error('Failed to load database', err)
        }
      }
    }
  }
}
</script>
<style scoped>
.tu-logo {
  margin: 1em 1em 0;
}
.univie-logo {
  margin: 1em 1em .5em;
}
</style>
