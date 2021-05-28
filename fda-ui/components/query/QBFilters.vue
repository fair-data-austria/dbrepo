<template>
  <div class="mb-5">
    <div v-if="!value.length" class="text-center">
      <v-btn @click="addFirst">Add filter</v-btn>
    </div>
    <div v-for="(clause, idx) in value" :key="idx">
      <v-row v-if="clause.type === 'and'" class="connector" dense>
        and
      </v-row>
      <v-row v-else-if="clause.type === 'or'" class="connector" dense>
        or
      </v-row>
      <v-row v-else dense>
        <v-col cols="2">
          <v-select v-model="clause.type" hide-details disabled :items="types" />
        </v-col>
        <v-col>
          <v-row dense>
            <v-col>
              <v-select v-model="clause.params[0]" hide-details :items="columns" />
            </v-col>
            <v-col cols="2">
              <v-autocomplete v-model="clause.params[1]" auto-select-first hide-details :items="operators" />
            </v-col>
            <v-col>
              <v-text-field v-model="clause.params[2]" hide-details />
            </v-col>
          </v-row>
        </v-col>
        <v-col class="acitons mt-1" cols="3">
          <v-btn :disabled="!canAdd(idx)" class="mr-1" depressed tile @click="addAnd">AND</v-btn>
          <v-btn :disabled="!canAdd(idx)" class="mr-1" depressed tile @click="addOr">OR</v-btn>
          <v-btn depressed @click="remove"><v-icon>mdi-delete</v-icon></v-btn>
        </v-col>
      </v-row>
    </div>
  </div>
</template>

<script>
const { operators, types } = require('@/server-middleware/query/operators')

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
    canAdd (idx) {
      return idx === this.value.length - 1
    },
    addFirst () {
      const column = (this.columns && this.columns.length) ? this.columns[0] : ''
      this.value.push({ type: 'where', params: [column, '=', ''] })
    },
    addAnd () {
      this.value.push({ type: 'and' })
      this.addFirst()
    },
    addOr () {
      this.value.push({ type: 'or' })
      this.addFirst()
    },
    remove () {
    },
    getPrefix (clause) {
      return 'FOO'
    }
  }
}
</script>
<style scoped>
.connector {
  justify-content: center;
  font-weight: 600;
}
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
.v-input {
  padding-top: 0;
}
.col {
  padding-top: 0;
}
</style>
