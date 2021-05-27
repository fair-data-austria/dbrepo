<template>
  <div class="mb-5">
    <div v-if="!value.length" class="text-center">
      <v-btn @click="addFirst">Add filter</v-btn>
    </div>
    <div v-for="(clause, idx) in value" :key="idx">
      <v-row dense>
        <v-col cols="2">
          <v-select v-model="clause.type" class="mt-2" :items="types" />
        </v-col>
        <v-col>
          <v-row dense>
            <v-col>
              <v-select v-model="clause.params[0]" :items="columns" />
            </v-col>
            <v-col cols="2">
              <v-select v-model="clause.params[1]" :items="operators" />
            </v-col>
            <v-col>
              <v-text-field v-model="clause.params[2]" />
            </v-col>
          </v-row>
        </v-col>
        <v-col class="acitons" cols="3">
          <v-btn depressed tile @click="addAnd">AND</v-btn>
          <v-btn depressed tile @click="addOr">OR</v-btn>
          <v-btn class="float-right" icon @click="remove"><v-icon>mdi-delete</v-icon></v-btn>
        </v-col>
      </v-row>
    </div>
  </div>
</template>

<script>
const types = ['where']
const { operators } = require('@/server-middleware/query/operators')

export default {
  components: {
  },
  props: {
    columns: { type: Array, default: () => [] },
    value: { type: Array, default: () => [] }
  },
  data () {
    return {
      types,
      operators
    }
  },
  computed: {
    db () {
      return this.$store.state.db
    }
  },
  methods: {
    addFirst () {
      const column = this.columns.length ? this.columns[0] : ''
      this.value.push({ type: 'where', params: [column, '=', ''] })
    },
    addAnd () {
    },
    addOr () {
    },
    remove () {
    }
  }
}
</script>
<style scoped>
.text-center {
  text-align: center;
}
.acitons {
  display: flex;
  flex-wrap: nowrap;
  align-items: center;
}
.float-right {
  margin-left: auto;
}
</style>
