<template>
  <div>
    <v-card>
      <v-progress-linear v-if="loading" :color="loadingColor" :indeterminate="!error" />
      <v-card-title>
        Create Account
      </v-card-title>
      <v-card-text>
        <v-alert
          border="left"
          color="amber lighten-4 black--text">
          Before you can use the repository sandbox, you will need to <i>confirm</i> your email address, make sure to check your spam folder.
        </v-alert>
        <v-alert
          border="left"
          color="red lighten-1 black--text">
          This is a <strong>TEST</strong> environment, do not use production/confidential data!
        </v-alert>
        <v-form ref="form" v-model="valid">
          <v-row>
            <v-col>
              <v-text-field
                v-model="createAccount.email"
                type="email"
                autocomplete="off"
                :rules="[v => !!v || $t('Required')]"
                hint="e.g. max.mustermann@work.com"
                label="Work E-Mail Address *" />
            </v-col>
          </v-row>
          <v-row>
            <v-col>
              <v-text-field
                v-model="createAccount.username"
                autocomplete="off"
                :rules="[v => !!v || $t('Required')]"
                hint="e.g. mmustermann"
                label="Username *" />
            </v-col>
          </v-row>
          <v-row>
            <v-col>
              <v-text-field
                v-model="createAccount.password"
                autocomplete="off"
                :rules="[v => !!v || $t('Required')]"
                type="password"
                label="Password *" />
            </v-col>
          </v-row>
          <v-row>
            <v-col>
              <v-checkbox
                v-model="consent"
                :rules="[v => !!v || $t('Required')]"
                label="I understand the warning and do not use production data" />
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
          @click="register">
          Submit
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
      consent: false,
      createAccount: {
        username: null,
        email: null,
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
    async register () {
      const url = '/api/user'
      try {
        this.loading = true
        const res = await this.$axios.post(url, this.createAccount)
        console.debug('create user', res.data)
        this.$toast.success('Success. Check your inbox!')
      } catch (err) {
        console.error('create user failed', err)
        this.$toast.error('Failed to create user')
      }
      this.loading = false
    }
  }
}
</script>
