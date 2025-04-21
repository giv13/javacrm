<script setup lang="ts">
import { computed, ref, watch, reactive } from 'vue'
import { EmptyProject, Project } from '../types'
import ProjectStatusBadge from '../components/ProjectStatusBadge.vue'
import UserAvatar from '../../users/widgets/UserAvatar.vue'
import { useUsersStore } from '../../../stores/users'
import { useStatuses } from '../composables/useStatuses'

const props = defineProps<{
  project: Project | null
  saveButtonLabel: string
}>()

const emit = defineEmits(['save', 'close'])

const defaultNewProject: EmptyProject = {
  name: '',
  description: '',
  status: undefined,
  responsible: undefined,
  participants: [],
}

const formErrors = reactive({
  name: [],
  status: [],
  responsible: []
});

const newProject = ref({ ...defaultNewProject })

const isFormHasUnsavedChanges = computed(() => {
  return Object.keys(newProject.value).some((key) => {
    if (key === 'participants' || key === 'status') {
      return false
    }

    return (
      newProject.value[key as keyof EmptyProject] !== (props.project ?? defaultNewProject)?.[key as keyof EmptyProject]
    )
  })
})

defineExpose({
  isFormHasUnsavedChanges,
})

const usersStore = useUsersStore()

const { statuses } = useStatuses()

watch(
  () => props.project,
  () => {
    if (!props.project) {
      return
    }

    newProject.value = {
      ...props.project,
      status: props.project.status.id,
    }
  },
  { immediate: true },
)

const ownerFiltersSearch = ref('')
const participantsFiltersSearch = ref('')
</script>

<template>
  <VaForm v-slot="{ isValid, validate }" class="flex flex-col gap-2">
    <VaInput
      :error="formErrors.name.length > 0"
      :errorMessages="formErrors.name"
      @input="formErrors.name = []"
      v-model="newProject.name"
      label="Наименование"
      name="name"
    />
    <VaTextarea v-model="newProject.description" label="Описание" name="description" />
    <VaSelect
      :error="formErrors.responsible.length > 0"
      :errorMessages="formErrors.responsible"
      @update:modelValue="formErrors.responsible = []"
      v-model="newProject.responsible"
      v-model:search="ownerFiltersSearch"
      searchable
      label="Ответственный"
      value-by="id"
      text-by="name"
      :options="usersStore.items"
      name="responsible"
    >
      <template #content="{ value: user }">
        <div v-if="user" :key="user.id" class="flex items-center gap-1 mr-4">
          <UserAvatar v-if="false" :user="user" size="18px" />
          {{ user.name }}
        </div>
      </template>
    </VaSelect>
    <VaSelect
      v-model="newProject.participants"
      v-model:search="participantsFiltersSearch"
      searchable
      label="Участники"
      value-by="id"
      text-by="name"
      multiple
      :options="usersStore.items"
      :max-visible-options="3"
      name="participants"
    >
      <template #content="{ valueArray }">
        <template v-if="valueArray?.length">
          <div v-for="(user, index) in valueArray" :key="user.id" class="flex items-center gap-1 mr-2">
            <UserAvatar v-if="user" :user="user" size="18px" />
            {{ user.name }}{{ index < valueArray.length - 1 ? ',' : '' }}
          </div>
        </template>
      </template>
    </VaSelect>
    <VaSelect
      :error="formErrors.status.length > 0"
      :errorMessages="formErrors.status"
      @update:modelValue="formErrors.status = []"
      v-model="newProject.status"
      label="Статус"
      value-by="id"
      text-by="displayName"
      :options="statuses"
      name="status"
    >
      <template #content="{ value }">
        <ProjectStatusBadge v-if="value" :status="value" />
      </template>
    </VaSelect>
    <div class="flex justify-end flex-col-reverse sm:flex-row mt-4 gap-2">
      <VaButton preset="secondary" color="secondary" @click="emit('close')">Отмена</VaButton>
      <VaButton :disabled="!isValid" @click="validate() && emit('save', newProject, formErrors)">{{ saveButtonLabel }}</VaButton>
    </div>
  </VaForm>
</template>

<style lang="scss" scoped>
.va-select-content__autocomplete {
  flex: 1;
}

.va-input-wrapper__text {
  gap: 0.2rem;
}
</style>
