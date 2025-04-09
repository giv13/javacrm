import { defineStore } from 'pinia'
import { getRoles } from '../data/pages/roles'
import { Role } from '../pages/users/types'

export const useRolesStore = defineStore('roles', {
  state: () => {
    return {
      items: [] as Role[],
    }
  },

  actions: {
    async getAll() {
      const { data } = await getRoles()
      this.items = data
    },
  },
})
