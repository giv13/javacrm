<template>
  <h1 class="va-h3" :style="{ 'color': color }">javaCRM</h1>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { useColors } from 'vuestic-ui'

const { getColor } = useColors()

const props = withDefaults(
  defineProps<{
    start?: string
  }>(),
  {
    start: 'primary',
  },
)

const color = computed(() => {
  return getColor(props.start)
})
</script>
