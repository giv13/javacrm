import { User } from '../../pages/users/types'
import { api, get, post, patch, put } from '../../services/api'

export type Pagination = {
  page: number
  perPage: number
  total: number
}

export type Sorting = {
  sortBy: keyof User | undefined
  sortingOrder: 'asc' | 'desc' | null
}

export type Filters = {
  isActive: boolean
  search: string
}

export const getUsers = async (filters: Partial<Filters & Pagination & Sorting>) => {
  const { isActive, search } = filters
  let filteredUsers: User[] = await get(api.allUsers());

  filteredUsers = filteredUsers.filter((user) => user.active === isActive)

  if (search) {
    filteredUsers = filteredUsers.filter((user) => user.name.toLowerCase().includes(search.toLowerCase()) || user.username.toLowerCase().includes(search.toLowerCase()))
  }

  const { page = 1, perPage = 10 } = filters || {}
  return {
    data: filteredUsers,
    pagination: {
      page,
      perPage,
      total: filteredUsers.length,
    },
  }
}

export const addUser = async (user: User, errors: Object) => {
  return await post(api.allUsers(), user, errors)
}

export const updateUser = async (user: User, errors: Object) => {
  return await put(api.user(user.id), user, errors)
}

export const removeUser = async (user: User) => {
  return fetch(api.user(user.id), { method: 'DELETE' })
}

export const uploadAvatar = async (user: User, body: FormData) => {
  return await patch(api.user(user.id), body)
}
