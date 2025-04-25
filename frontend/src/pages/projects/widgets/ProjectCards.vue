<script setup lang="ts">
import { type PropType, inject } from 'vue'
import { type Project } from '../types'
import ProjectStatusBadge from '../components/ProjectStatusBadge.vue'
import { useUserStore } from '../../../stores/user-store'

const userStore = useUserStore()

defineProps({
  projects: {
    type: Array as PropType<Project[]>,
    required: true,
  },
  loading: {
    type: Boolean,
    required: true,
  },
})

defineEmits<{
  (event: 'edit', project: Project): void
  (event: 'delete', project: Project): void
}>()

const { getUserById, getParticipantsOptions } = inject<any>('ProjectsPage')
</script>

<template>
  <VaInnerLoading
    v-if="projects.length > 0 || loading"
    :loading="loading"
    class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 min-h-[4rem]"
  >
    <VaCard
      v-for="project in projects"
      :key="project.name"
      :style="{ '--va-card-outlined-border': '1px solid var(--va-background-element)' }"
      outlined
    >
      <VaCardContent class="flex flex-col h-full">
        <div class="text-[var(--va-secondary)]">{{ new Date(project.createdAt).toLocaleDateString() }}</div>
        <div class="flex flex-col items-center gap-4 grow">
          <h4 class="va-h4 text-center self-stretch overflow-hidden line-clamp-2 text-ellipsis">
            {{ project.name }}
          </h4>
          <p>
            <span class="text-[var(--va-secondary)]">Ответственный: </span>
            <span v-if="getUserById(project.responsible)">{{ getUserById(project.responsible)!.name }}</span>
          </p>
          <VaAvatarGroup class="my-4" :options="getParticipantsOptions(project.participants)" :max="5" />
          <ProjectStatusBadge :status="project.status" />
        </div>
        <VaDivider class="my-6" />
        <div class="flex justify-between">
          <VaButton preset="secondary" icon="mso-edit" color="secondary" @click="$emit('edit', project)" v-show="userStore.hasAuthorities(['PROJECT_UPDATE'])" />
          <VaButton preset="secondary" icon="mso-delete" color="danger" @click="$emit('delete', project)" v-show="userStore.hasAuthorities(['PROJECT_DELETE'])" />
        </div>
      </VaCardContent>
    </VaCard>
  </VaInnerLoading>
  <div v-else class="p-4 flex justify-center items-center text-[var(--va-secondary)]">Нет проектов</div>
</template>
