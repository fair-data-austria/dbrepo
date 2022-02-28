<template>
  <div>
    <v-form ref="form" v-model="valid" @submit.prevent="submit">
      <v-card v-if="!token">
        <v-progress-linear v-if="loading" :color="loadingColor" :indeterminate="!error" />
        <v-card-title>
          Login
        </v-card-title>
        <v-card-text>
          <v-alert
            border="left"
            color="amber lighten-4 black--text">
            If you need an account, create one <a @click="signup">here</a>.
          </v-alert>
          <v-row>
            <v-col cols="6">
              <v-text-field
                v-model="loginAccount.username"
                autocomplete="off"
                autofocus
                required
                :rules="[v => !!v || $t('Required')]"
                label="Username *" />
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="6">
              <v-text-field
                v-model="loginAccount.password"
                autocomplete="off"
                type="password"
                required
                :rules="[v => !!v || $t('Required')]"
                label="Password *" />
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions>
          <v-btn
            id="login"
            class="mb-2 ml-2"
            :disabled="!valid"
            color="primary"
            type="submit"
            @click="login">
            Login
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-form>
    <p v-if="token">Already logged-in</p>
  </div>
</template>

<script>
export default {
  data () {
    return {
      loading: false,
      error: false, // XXX: `error` is never changed
      valid: false,
      loginAccount: {
        username: null,
        password: null
      }
    }
  },
  computed: {
    loadingColor () {
      return this.error ? 'red lighten-2' : 'primary'
    },
    token () {
      return this.$store.state.token
    }
  },
  beforeMount () {
  },
  methods: {
    submit () {
      this.$refs.form.validate()
    },
    async login () {
      const url = '/api/auth'
      try {
        this.loading = true
        const res = await this.$axios.post(url, this.loginAccount)
        console.debug('login user', res.data)
        this.$store.commit('SET_TOKEN', res.data.token)
        const user = { ...res.data }
        delete user.token
        this.$store.commit('SET_USER', user)
        this.$toast.success('Welcome back!')
        this.$router.push('/container')
      } catch (err) {
        console.error('login user failed', err)
        this.$toast.error('Failed to login user')
      }
      this.loading = false
    },
    signup () {
      this.$router.push('/signup')
    }
  }
}
</script>
