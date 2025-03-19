<template>
  <VaForm ref="form" @submit.prevent="submit">
    <h1 class="font-semibold text-4xl mb-4">Вход</h1>
    <p class="text-base mb-4 leading-5">
      Нет аккаунта?
      <RouterLink :to="{ name: 'register' }" class="font-semibold text-primary">Создать аккаунт</RouterLink>
    </p>
    <VaInput
      v-model="formData.username"
      :error="formErrors.username.length > 0"
      :errorMessages="formErrors.username"
      @input="formErrors.username = []"
      class="mb-4"
      label="Имя пользователя"
      type="text"
    />
    <VaValue v-slot="isPasswordVisible" :default-value="false">
      <VaInput
        v-model="formData.password"
        :error="formErrors.password.length > 0"
        :errorMessages="formErrors.password"
        @input="formErrors.password = []"
        :type="isPasswordVisible.value ? 'text' : 'password'"
        class="mb-4"
        label="Пароль"
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

    <div class="flex justify-center mt-4">
      <VaButton class="w-full" @click="submit"> Войти</VaButton>
    </div>
  </VaForm>
</template>

<script lang="ts" setup>
import { reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useForm, useToast } from 'vuestic-ui'
import { api, post } from '../../services/api'
import { useUserStore } from '../../stores/user-store'

const { validate } = useForm('form')
const { push } = useRouter()
const { init } = useToast()
const { login } = useUserStore()
const { query } = useRoute()

const formData = reactive({
  username: '',
  password: '',
})

const formErrors = reactive({
  username: [],
  password: [],
});

const submit = () => {
  if (validate()) {
    return post(api.login(), formData, formErrors).then(r => {
      login(r)
      init({ message: 'Вы вошли в систему', color: 'success' })
      const redirectPath = Array.isArray(query.redirect) ? query.redirect[0] : query.redirect;
      push(redirectPath || '/')
    })
  }
}
</script>
