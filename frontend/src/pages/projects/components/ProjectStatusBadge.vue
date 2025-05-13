<script setup lang="ts">
import { PropType } from 'vue'
import { Status } from '../types'

defineProps({
  status: {
    type: Object as PropType<Status>,
    required: true,
  },
})

const badgeColorMap: Record<Status['name'], string> = {
  NEW: 'secondary',
  IN_PROGRESS: 'primary',
  COMPLETED: 'success',
  ARCHIVED: 'warning',
}
</script>

<template>
  <VaBadge square :color="badgeColorMap[$props.status.name]" :text="$props.status.displayName" />
</template>
