<script setup lang="ts">
import { ref, watchEffect } from 'vue'
import UsersTable from './widgets/UsersTable.vue'
import EditUserForm from './widgets/EditUserForm.vue'
import { User } from './types'
import { useUsers } from './composables/useUsers'
import { useModal, useToast } from 'vuestic-ui'
import { useProjectsStore } from "../../stores/projects";
import { useUserStore } from '../../stores/user-store'
import { useUsersStore } from '../../stores/users'

const userStore = useUserStore()
const usersStore = useUsersStore()

const doShowEditUserModal = ref(false)

const { users, isLoading, filters, sorting, pagination, error, ...usersApi } = useUsers()
const projects = useProjectsStore().items

const userToEdit = ref<User | null>(null)

const showEditUserModal = (user: User) => {
  userToEdit.value = user
  doShowEditUserModal.value = true
}

const showAddUserModal = () => {
  userToEdit.value = null
  doShowEditUserModal.value = true
}

const { init: notify } = useToast()

watchEffect(() => {
  if (error.value) {
    notify({
      message: error.value.message,
      color: 'danger',
    })
  }
})

const onUserSaved = async (user: User, errors: Object, ok: Function) => {
  if (userToEdit.value) {
    await usersApi.update(user, errors)
    notify({
      message: `Пользователь ${user.username} обновлен`,
      color: 'success',
    })
  } else {
    const { id } = await usersApi.add(user, errors)
    user.id = id
    notify({
      message: `Пользователь ${user.username} добавлен`,
      color: 'success',
    })
  }
  if (user.avatar.startsWith('blob:') || (!user.avatar && usersStore.items.find(({ id }) => id === user.id)?.avatar)) {
    const avatar = user.avatar.startsWith('blob:') ? await fetch(user.avatar).then((r) => r.blob()) : "";
    user.avatar = await usersApi.uploadAvatar(user, avatar)
    notify({
      message: `Изображение для пользователя ${user.username} обновлено`,
      color: 'success',
    })
  }
  ok()
}

const onUserDelete = async (user: User) => {
  await usersApi.remove(user)
  notify({
    message: `Пользователь ${user.username} удален`,
    color: 'success',
  })
}

const editFormRef = ref()

const { confirm } = useModal()

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
  <h1 class="page-title">Пользователи</h1>

  <VaCard>
    <VaCardContent>
      <div class="flex flex-col md:flex-row gap-2 mb-2 justify-between">
        <div class="flex flex-col md:flex-row gap-2 justify-start">
          <VaButtonToggle
            v-model="filters.isActive"
            color="background-element"
            border-color="background-element"
            :options="[
              { label: 'Активные', value: true },
              { label: 'Неактивные', value: false },
            ]"
          />
          <VaInput v-model="filters.search" placeholder="Поиск">
            <template #prependInner>
              <VaIcon name="search" color="secondary" size="small" />
            </template>
          </VaInput>
        </div>
        <VaButton @click="showAddUserModal" v-show="userStore.hasAuthorities(['USER_CREATE'])">Добавить пользователя</VaButton>
      </div>

      <UsersTable
        v-model:sort-by="sorting.sortBy"
        v-model:sorting-order="sorting.sortingOrder"
        :users="users"
        :projects="projects"
        :loading="isLoading"
        :pagination="pagination"
        @editUser="showEditUserModal"
        @deleteUser="onUserDelete"
      />
    </VaCardContent>
  </VaCard>

  <VaModal
    v-slot="{ cancel, ok }"
    v-model="doShowEditUserModal"
    size="small"
    mobile-fullscreen
    close-button
    hide-default-actions
    :before-cancel="beforeEditFormModalClose"
  >
    <h1 class="va-h5">{{ userToEdit ? 'Обновить' : 'Добавить' }} пользователя</h1>
    <EditUserForm
      ref="editFormRef"
      :user="userToEdit"
      :save-button-label="userToEdit ? 'Сохранить' : 'Добавить'"
      @close="cancel"
      @save="
        (user, errors) => {
          onUserSaved(user, errors, ok)
        }
      "
    />
  </VaModal>
</template>
