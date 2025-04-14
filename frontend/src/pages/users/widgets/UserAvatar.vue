<script setup lang="ts">
import { type PropType } from 'vue'
import { type User } from '../types'

const avatarColor = (userName: string) => {
  const colors = ['primary', '#FFD43A', '#ADFF00', '#262824', 'danger']
  const index = userName.charCodeAt(0) % colors.length
  return colors[index]
}

defineProps({
  user: {
    type: Object as PropType<User>,
    required: true,
  },
  size: {
    type: String,
    default: 'medium',
  },
})

const avatarPath = (avatar: string) => {
  if (!avatar) return ''
  if (/^([0-9a-zA-Z+/]{4})*(([0-9a-zA-Z+/]{2}==)|([0-9a-zA-Z+/]{3}=))?$/.test(avatar)) return 'data:image/jpeg;base64,' + avatar
  return avatar
}
</script>

<template>
  <VaAvatar
    :size="size"
    :src="avatarPath(user.avatar)"
    :fallback-text="user.name[0]"
    :color="avatarColor(user.name)"
  />
</template>
