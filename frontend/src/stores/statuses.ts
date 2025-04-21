import { defineStore } from 'pinia'
import { getStatuses } from '../data/pages/statuses'
import { Status } from '../pages/projects/types'

export const useStatusesStore = defineStore('statuses', {
  state: () => {
    return {
      items: [] as Status[],
    }
  },

  actions: {
    async getAll() {
      const { data } = await getStatuses()
      this.items = data
    },
  },
})
