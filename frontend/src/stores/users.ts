import { unref, Ref } from 'vue'
import { defineStore } from 'pinia'
import {
  addUser,
  type Filters,
  getUsers,
  Pagination,
  removeUser,
  Sorting,
  updateUser,
  uploadAvatar,
} from '../data/pages/users'
import { User } from '../pages/users/types'

export const useUsersStore = defineStore('users', {
  state: () => {
    return {
      items: [] as User[],
      pagination: { page: 1, perPage: 10, total: 0 },
    }
  },

  actions: {
    async getAll(options: { pagination?: Pagination; sorting?: Sorting; filters?: Partial<Filters> }) {
      const { data, pagination } = await getUsers({
        ...options.filters,
        ...options.sorting,
        ...options.pagination,
      })
      this.items = data
      this.pagination = pagination
    },

    async add(user: User, filters: Ref<Partial<Filters>>, errors: object) {
      const newUser = await addUser(user, errors)
      await this.getAll({ filters: unref(filters) })
      return newUser
    },

    async update(user: User, filters: Ref<Partial<Filters>>, errors: object) {
      const updatedUser = await updateUser(user, errors)
      await this.getAll({ filters: unref(filters) })
      return updatedUser
    },

    async remove(user: User) {
      await removeUser(user)
      const index = this.items.findIndex(({ id }) => id === user.id)
      this.items.splice(index, 1)
    },

    async uploadAvatar(user: User, filters: Ref<Partial<Filters>>, formData: FormData) {
      const updatedUser = await uploadAvatar(user, formData)
      await this.getAll({ filters: unref(filters) })
      return updatedUser
    },
  },
})
