import { computed, ref } from 'vue'
import { useRolesStore } from '../../../stores/roles'

export const useRoles = () => {
  const isLoading = ref(false)
  const rolesStore = useRolesStore()

  const fetch = async () => {
    isLoading.value = true
    try {
      await rolesStore.getAll()
    } finally {
      isLoading.value = false
    }
  }

  fetch()

  const roles = computed(() => {
    return rolesStore.items
  })

  return {
    isLoading,
    roles,
    fetch,
  }
}
