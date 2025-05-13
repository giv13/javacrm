<script setup lang="ts">
import { ref, provide } from 'vue'
import { useLocalStorage } from '@vueuse/core'
import { useProjects } from './composables/useProjects'
import ProjectCards from './widgets/ProjectCards.vue'
import ProjectTable from './widgets/ProjectsTable.vue'
import EditProjectForm from './widgets/EditProjectForm.vue'
import { Project } from './types'
import { useModal, useToast } from 'vuestic-ui'
import { useProjectUsers } from './composables/useProjectUsers'
import { useStatuses } from './composables/useStatuses'
import { useUserStore } from '../../stores/user-store'

const userStore = useUserStore()
useStatuses()

const doShowAsCards = useLocalStorage('projects-view', true)

const { projects, update, add, isLoading, remove, pagination, sorting } = useProjects()

const { users, getParticipantsOptions, getUserById } = useProjectUsers()

provide('ProjectsPage', {
  users,
  getParticipantsOptions,
  getUserById,
})

const projectToEdit = ref<Project | null>(null)
const doShowProjectFormModal = ref(false)

const editProject = (project: Project) => {
  projectToEdit.value = project
  doShowProjectFormModal.value = true
}

const createNewProject = () => {
  projectToEdit.value = null
  doShowProjectFormModal.value = true
}

const { init: notify } = useToast()

const onProjectSaved = async (project: Project, errors: object, ok: () => void) => {
  if (projectToEdit.value) {
    await update(project, errors)
    notify({
      message: `Проект ${project.name} обновлен`,
      color: 'success',
    })
  } else {
    await add(project, errors)
    notify({
      message: `Проект ${project.name} добавлен`,
      color: 'success',
    })
  }
  ok()
}

const { confirm } = useModal()

const onProjectDeleted = async (project: Project) => {
  const response = await confirm({
    title: 'Удалить проект',
    message: `Вы уверены, что хотите удалить проект "${project.name}"?`,
    okText: 'Удалить',
    size: 'small',
    maxWidth: '380px',
  })

  if (!response) {
    return
  }

  await remove(project)
  notify({
    message: 'Проект удален',
    color: 'success',
  })
}

const editFormRef = ref()

const beforeEditFormModalClose = async (hide: () => unknown) => {
  if (editFormRef.value.isFormHasUnsavedChanges) {
    const agreed = await confirm({
      maxWidth: '380px',
      message: 'Форма содержит несохраненные изменения. Вы уверены, что хотите ее закрыть?',
      size: 'small',
    })
    if (agreed) {
      hide()
    }
  } else {
    hide()
  }
}
</script>

<template>
  <h1 class="page-title">Проекты</h1>

  <VaCard>
    <VaCardContent>
      <div class="flex flex-col md:flex-row gap-2 mb-2 justify-between">
        <div class="flex flex-col md:flex-row gap-2 justify-start">
          <VaButtonToggle
            v-model="doShowAsCards"
            color="background-element"
            border-color="background-element"
            :options="[
              { label: 'Карточки', value: true },
              { label: 'Таблица', value: false },
            ]"
          />
        </div>
        <VaButton v-show="userStore.hasAuthorities(['PROJECT_CREATE'])" icon="add" @click="createNewProject"
          >Добавить проект</VaButton
        >
      </div>

      <ProjectCards
        v-if="doShowAsCards"
        :projects="projects"
        :loading="isLoading"
        @edit="editProject"
        @delete="onProjectDeleted"
      />
      <ProjectTable
        v-else
        v-model:sort-by="sorting.sortBy"
        v-model:sorting-order="sorting.sortingOrder"
        v-model:pagination="pagination"
        :projects="projects"
        :loading="isLoading"
        @edit="editProject"
        @delete="onProjectDeleted"
      />
    </VaCardContent>

    <VaModal
      v-slot="{ cancel, ok }"
      v-model="doShowProjectFormModal"
      size="small"
      mobile-fullscreen
      close-button
      stateful
      hide-default-actions
      :before-cancel="beforeEditFormModalClose"
    >
      <h1 class="va-h5 mb-4">{{ projectToEdit ? 'Обновить' : 'Добавить' }} проект</h1>
      <EditProjectForm
        ref="editFormRef"
        :project="projectToEdit"
        :save-button-label="projectToEdit ? 'Сохранить' : 'Добавить'"
        @close="cancel"
        @save="
          (project, errors) => {
            onProjectSaved(project, errors, ok)
          }
        "
      />
    </VaModal>
  </VaCard>
</template>
