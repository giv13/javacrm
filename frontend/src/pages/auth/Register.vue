<template>
  <VaForm ref="form" @submit.prevent="submit">
    <h1 class="font-semibold text-4xl mb-4">Регистрация</h1>
    <p class="text-base mb-4 leading-5">
      Уже есть аккаунт?
      <RouterLink :to="{ name: 'login' }" class="font-semibold text-primary">Войти</RouterLink>
    </p>
    <VaInput
      v-model="formData.email"
      :error="formErrors.email.length > 0"
      :errorMessages="formErrors.email"
      @input="formErrors.email = []"
      class="mb-4"
      label="E-mail"
      type="email"
    />
    <VaInput
      v-model="formData.username"
      :error="formErrors.username.length > 0"
      :errorMessages="formErrors.username"
      @input="formErrors.username = []"
      class="mb-4"
      label="Имя пользователя"
      type="text"
    />
    <VaInput
      v-model="formData.name"
      :error="formErrors.name.length > 0"
      :errorMessages="formErrors.name"
      @input="formErrors.name = []"
      class="mb-4"
      label="Имя"
      type="text"
    />
    <VaValue v-slot="isPasswordVisible" :default-value="false">
      <VaInput
        ref="password1"
        v-model="formData.password"
        :error="formErrors.password.length > 0"
        :errorMessages="formErrors.password"
        :errorCount="3"
        @input="formErrors.password = []"
        :type="isPasswordVisible.value ? 'text' : 'password'"
        class="mb-4"
        label="Пароль"
        messages="От 8 символов, должен содержать латинские буквы в нижнем и верхнем регистре, цифры и спецсимволы"
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
      <VaInput
        ref="password2"
        v-model="formData.passwordConfirmation"
        :error="formErrors.passwordConfirmation.length > 0"
        :errorMessages="formErrors.passwordConfirmation"
        @input="formErrors.passwordConfirmation = []"
        :type="isPasswordVisible.value ? 'text' : 'password'"
        class="mb-4"
        label="Повторите пароль"
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
      <VaButton class="w-full" @click="submit"> Создать аккаунт</VaButton>
    </div>
  </VaForm>
</template>

<script lang="ts" setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useForm, useToast } from 'vuestic-ui'
import { api, post } from '../../services/api';
import { useUserStore } from '../../stores/user-store';

const { validate } = useForm('form')
const { push } = useRouter()
const { init } = useToast()
const { login } = useUserStore()

const formData = reactive({
  email: '',
  username: '',
  name: '',
  password: '',
  passwordConfirmation: '',
})

const formErrors = reactive({
  email: [],
  username: [],
  name: [],
  password: [],
  passwordConfirmation: [],
});

const submit = () => {
  if (validate()) {
    return post(api.register(), formData, formErrors).then(r => {
      login(r)
      init({ message: 'Вы зарегистрировались в системе', color: 'success' })
      push('/')
    })
  }
}
</script>
