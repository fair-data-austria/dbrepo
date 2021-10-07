<template>
  <div>
    <v-toolbar flat>
      <v-toolbar-title>Welcome</v-toolbar-title>
      <v-spacer />
      <v-toolbar-title>
        <v-btn color="primary" href="https://login.tuwien.ac.at/portal">
          <v-icon left>mdi-login</v-icon> TU Wien
        </v-btn>
        <v-btn color="primary" disabled>
          <v-icon left>mdi-login</v-icon> Uni Vie
        </v-btn>
      </v-toolbar-title>
    </v-toolbar>
    <v-card flat>
      <v-card-title>FAIR Data</v-card-title>
      <v-card-subtitle>
        Findable, Accessible, Interoperable, Reusable
      </v-card-subtitle>
      <v-card-text>
        text
      </v-card-text>
    </v-card>
  </div>
</template>

<script>
export default {
  components: {}
}
</script>
