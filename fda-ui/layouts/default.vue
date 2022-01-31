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
        class="mr-2 white--text"
        color="blue-grey"
        @click="authenticate">
        <v-icon left>mdi-login</v-icon> Login
      </v-btn>
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
        </v-list>
      </v-menu>
    </v-app-bar>
    <v-main>
      <v-container>
        <nuxt />
      </v-container>
    </v-main>
    <v-footer v-if="sandbox" padless>
      <v-card
        flat
        tile
        width="100%"
        class="primary text-center">
        <v-card-text class="white--text">
          <strong>Sandbox Environment</strong> — <a href="//github.com/fair-data-austria/dbrepo/issues/new" class="white--text">Report a bug</a>
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
  mdiNewspaperVariantOutline
} from '@mdi/js'

export default {
  name: 'DefaultLayout',
  data () {
    return {
      drawer: false,
      countDown: 1,
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
    sandbox () {
      return true
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
    },
    timer () {
      const hours = Math.floor(this.countDown / 3600)
      const minutes = Math.floor((this.countDown - hours * 3600) / 60)
      const seconds = (this.countDown - hours * 3600 - minutes * 60)
      return `${hours}h${minutes}m${seconds}s`
    }
  },
  watch: {
    $route () {
      this.loadDB()
    }
  },
  mounted () {
    this.loadDB()
    this.countDownTimer()
    this.initDownTimer()
  },
  methods: {
    authenticate () {
      window.location.href = 'https://dbrepo.ossdip.at:9097'
    },
    switchTheme () {
      this.$vuetify.theme.dark = !this.$vuetify.theme.dark
    },
    initDownTimer () {
      const two = new Date()
      two.setDate(new Date().getDate() + 1)
      two.setHours(2, 0, 0, 0)
      this.countDown = Math.floor((two - new Date()) / 1000)
    },
    countDownTimer () {
      if (this.countDown > 0) {
        setTimeout(() => {
          this.countDown -= 1
          this.countDownTimer()
        }, 1000)
      }
    },
    async loadDB () {
      if (this.$route.params.db_id && !this.db) {
        try {
          const res = await this.$axios.get(`/api/database/${this.$route.params.db_id}`)
          this.$store.commit('SET_DATABASE', res.data)
        } catch (err) {
          this.$toast.error('Could not load gffff.')
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
