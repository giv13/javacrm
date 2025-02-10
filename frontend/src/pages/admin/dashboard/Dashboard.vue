<script lang="ts" setup>
import ProjectTable from './cards/ProjectTable.vue'
</script>

<template>
  <h1 class="page-title font-bold">Dashboard</h1>
  <section class="flex flex-col gap-4">
    <div class="flex flex-col md:flex-row gap-4">
      <ProjectTable class="w-full md:w-1/2" />
    </div>
  </section>
</template>
