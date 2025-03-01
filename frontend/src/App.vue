<template>
  <Forbidden v-if="useRoute().meta.forbidden" />
  <RouterView v-else />
</template>

<style lang="scss">
#app {
  font-family: 'Inter', Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

body {
  margin: 0;
  min-width: 20rem;
}
</style>

<script setup>
  import Forbidden from './pages/403.vue';
  import { useRoute } from "vue-router";
</script>
