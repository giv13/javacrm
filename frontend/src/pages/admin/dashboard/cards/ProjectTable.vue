<script setup lang="ts">
import { defineVaDataTableColumns } from 'vuestic-ui'
import UserAvatar from '../../../users/widgets/UserAvatar.vue'
import ProjectStatusBadge from '../../../projects/components/ProjectStatusBadge.vue'
import { useProjects } from '../../../projects/composables/useProjects'
import { Pagination } from '../../../../data/pages/projects'
import { ref } from 'vue'
import { useProjectUsers } from '../../../projects/composables/useProjectUsers'

const columns = defineVaDataTableColumns([
  { label: 'Наименование', key: 'name', sortable: true },
  { label: 'Статус', key: 'status', sortable: true },
  { label: 'Ответственный', key: 'responsibleId', sortable: true },
  { label: 'Участники', key: 'participants', sortable: true },
])

const pagination = ref<Pagination>({ page: 1, perPage: 5, total: 0 })
const { projects, isLoading, sorting } = useProjects({
  pagination,
})

const { getParticipantsOptions, getUserById } = useProjectUsers()
</script>

<template>
  <VaCard>
    <VaCardTitle class="flex items-start justify-between">
      <h1 class="card-title text-secondary font-bold uppercase">Проекты</h1>
      <VaButton preset="primary" size="small" to="/projects">Смотреть все проекты</VaButton>
    </VaCardTitle>
    <VaCardContent>
      <div v-if="projects.length > 0">
        <VaDataTable
          v-model:sort-by="sorting.sortBy"
          v-model:sorting-order="sorting.sortingOrder"
          :items="projects"
          :columns="columns"
          :loading="isLoading"
        >
          <template #cell(name)="{ rowData }">
            <div class="ellipsis max-w-[230px] lg:max-w-[450px]">
              {{ rowData.name }}
            </div>
          </template>
          <template #cell(status)="{ rowData }">
            <ProjectStatusBadge :status="rowData.status" />
          </template>
          <template #cell(responsibleId)="{ rowData }">
            <div class="flex items-center gap-2 ellipsis max-w-[230px]">
              <UserAvatar
                v-if="getUserById(rowData.responsibleId)"
                :user="getUserById(rowData.responsibleId)!"
                size="small"
              />
              {{ getUserById(rowData.responsibleId)?.name }}
            </div>
          </template>
          <template #cell(participants)="{ rowData }">
            <VaAvatarGroup size="small" :options="getParticipantsOptions(rowData.participants)" :max="2" />
          </template>
        </VaDataTable>
      </div>
      <div v-else class="p-4 flex justify-center items-center text-[var(--va-secondary)]">Нет проектов</div>
    </VaCardContent>
  </VaCard>
</template>
