<template>
  <div>
    <v-card>
      <v-progress-linear v-if="loading" :color="loadingColor" :indeterminate="!error" />
      <v-card-title>
        Login
      </v-card-title>
      <v-card-text>
        <v-form ref="form" v-model="valid">
          <v-row>
            <v-col>
              <v-text-field
                v-model="loginAccount.username"
                autocomplete="off"
                :rules="[v => !!v || $t('Required')]"
                label="Username *" />
            </v-col>
          </v-row>
          <v-row>
            <v-col>
              <v-text-field
                v-model="loginAccount.password"
                autocomplete="off"
                type="password"
                :rules="[v => !!v || $t('Required')]"
                label="Password *" />
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn
          class="mb-2"
          @click="cancel">
          Cancel
        </v-btn>
        <v-btn
          id="login"
          class="mb-2"
          :disabled="!valid"
          color="primary"
          @click="login">
          Login
        </v-btn>
      </v-card-actions>
    </v-card>
  </div>
</template>

<script>
export default {
  data () {
    return {
      loading: false,
      error: false,
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
    }
  },
  beforeMount () {
  },
  methods: {
    cancel () {
      this.$parent.$parent.$parent.$parent.loginDialog = false
    },
    async login () {
      const url = '/api/auth'
      try {
        this.loading = true
        const res = await this.$axios.post(url, this.loginAccount)
        console.debug('login user', res.data)
        this.$toast.success('Welcome back!')
        this.cancel()
      } catch (err) {
        console.error('login user failed', err)
        this.$toast.error('Failed to login user')
      }
      this.loading = false
    }
  }
}
</script>
