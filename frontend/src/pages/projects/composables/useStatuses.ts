import { computed, ref } from 'vue'
import { useStatusesStore } from '../../../stores/statuses'

export const useStatuses = () => {
  const isLoading = ref(false)
  const statusesStore = useStatusesStore()

  const fetch = async () => {
    isLoading.value = true
    try {
      await statusesStore.getAll()
    } finally {
      isLoading.value = false
    }
  }

  fetch()

  const statuses = computed(() => {
    return statusesStore.items
  })

  return {
    isLoading,
    statuses,
    fetch,
  }
}
