<script setup lang="ts">
import { PropType, computed, ref, watch, reactive } from 'vue'
import { User, EmptyUser } from '../types'
import UserAvatar from './UserAvatar.vue'
import { useRoles } from '../composables/useRoles'

const props = defineProps({
  user: {
    type: Object as PropType<User | null>,
    default: null,
  },
  saveButtonLabel: {
    type: String,
    default: 'Save',
  },
})

const defaultNewUser: EmptyUser = {
  name: '',
  username: '',
  email: '',
  password: '',
  passwordConfirmation: '',
  notes: '',
  avatar: '',
  active: true,
  roles: [],
}

const formErrors = reactive({
  name: [],
  username: [],
  email: [],
  password: [],
  passwordConfirmation: [],
});

const newUser = ref({ ...defaultNewUser })

const isFormHasUnsavedChanges = computed(() => {
  return Object.keys(newUser.value).some((key) => {
    if (key === 'avatar' || key === 'roles') {
      return false
    }

    return (
      newUser.value[key as keyof EmptyUser] !== (props.user ?? defaultNewUser)?.[key as keyof EmptyUser]
    )
  })
})

defineExpose({
  isFormHasUnsavedChanges,
})

const { roles } = useRoles()

watch(
  [() => props.user, roles],
  () => {
    if (!props.user) {
      return
    }

    newUser.value = {
      ...props.user,
      roles: props.user.roles.filter(role => roles.value.find(({ id }) => id === role.id)).map(role => role.id),
      avatar: props.user.avatar || '',
    }
  },
  { immediate: true },
)

const avatar = ref<File>()

const makeAvatarBlobUrl = (avatar: File) => {
  return URL.createObjectURL(avatar)
}

watch(avatar, (newAvatar) => {
  newUser.value.avatar = newAvatar ? makeAvatarBlobUrl(newAvatar) : ''
})

const emit = defineEmits(['save', 'close'])
</script>

<template>
  <VaForm v-slot="{ isValid, validate }" ref="add-user-form" class="flex-col justify-start items-start gap-4 inline-flex w-full">
    <VaFileUpload
      v-model="avatar"
      type="single"
      hide-file-list
      class="self-stretch justify-start items-center gap-4 inline-flex"
    >
      <UserAvatar :user="newUser" size="large" />
      <VaButton preset="primary" size="small">Добавить изображение</VaButton>
      <VaButton
        v-if="avatar || newUser.avatar"
        preset="primary"
        color="danger"
        size="small"
        icon="delete"
        class="z-10"
        @click.stop="avatar = undefined; newUser.avatar = ''"
      />
    </VaFileUpload>
    <div class="self-stretch flex-col justify-start items-start gap-4 flex">
      <div class="flex gap-4 flex-col sm:flex-row w-full">
        <VaInput
          :error="formErrors.name.length > 0"
          :errorMessages="formErrors.name"
          @input="formErrors.name = []"
          v-model="newUser.name"
          label="Имя"
          class="w-full sm:w-1/2"
          name="name"
        />
        <VaInput
          :error="formErrors.username.length > 0"
          :errorMessages="formErrors.username"
          @input="formErrors.username = []"
          v-model="newUser.username"
          label="Имя пользователя"
          class="w-full sm:w-1/2"
          name="username"
        />
      </div>
      <div class="flex gap-4 flex-col sm:flex-row w-full">
        <VaInput
          :error="formErrors.email.length > 0"
          :errorMessages="formErrors.email"
          @input="formErrors.email = []"
          v-model="newUser.email"
          label="E-mail"
          class="w-full sm:w-1/2"
          name="email"
        />
        <VaSelect
          v-model="newUser.roles"
          label="Роли"
          class="w-full sm:w-1/2"
          :options="roles"
          value-by="id"
          text-by="displayName"
          name="roles"
          multiple
          :max-visible-options="2"
        />
      </div>

      <div class="flex gap-4 flex-col sm:flex-row w-full">
        <VaValue v-slot="isPasswordVisible" :default-value="false">
          <VaInput
            v-model="newUser.password"
            :error="formErrors.password.length > 0"
            :errorMessages="formErrors.password"
            :errorCount="3"
            @input="formErrors.password = []"
            :type="isPasswordVisible.value ? 'text' : 'password'"
            class="w-full sm:w-1/2"
            label="Новый пароль"
            name="password"
            @clickAppendInner.stop="isPasswordVisible.value = !isPasswordVisible.value"
          >
            <template #appendInner>
              <VaIcon
                :name="isPasswordVisible.value ? 'mso-visibility_off' : 'mso-visibility'"
                class="cursor-pointer"
                color="secondary"
              />
            </template>
          </VaInput>
        </VaValue>
        <VaValue v-slot="isPasswordVisible" :default-value="false">
          <VaInput
            v-model="newUser.passwordConfirmation"
            :error="formErrors.passwordConfirmation.length > 0"
            :errorMessages="formErrors.passwordConfirmation"
            @input="formErrors.passwordConfirmation = []"
            :type="isPasswordVisible.value ? 'text' : 'password'"
            class="w-full sm:w-1/2"
            label="Повторите новый пароль"
            name="passwordConfirmation"
            @clickAppendInner.stop="isPasswordVisible.value = !isPasswordVisible.value"
          >
            <template #appendInner>
              <VaIcon
                :name="isPasswordVisible.value ? 'mso-visibility_off' : 'mso-visibility'"
                class="cursor-pointer"
                color="secondary"
              />
            </template>
          </VaInput>
        </VaValue>
      </div>

      <VaCheckbox v-model="newUser.active" label="Активный" class="w-full" name="active" />

      <VaTextarea v-model="newUser.notes" label="Notes" class="w-full" name="notes" />
      <div class="flex gap-2 flex-col-reverse items-stretch justify-end w-full sm:flex-row sm:items-center">
        <VaButton preset="secondary" color="secondary" @click="emit('close')">Отмена</VaButton>
        <VaButton :disabled="!isValid" @click="validate() && emit('save', newUser, formErrors)">{{ saveButtonLabel }}</VaButton>
      </div>
    </div>
  </VaForm>
</template>
