import { Ref, ref, unref, computed } from 'vue'
import { Sorting, Pagination } from '../../../data/pages/projects'
import { Project } from '../types'
import { useProjectsStore } from '../../../stores/projects'
import { useProjectUsers } from './useProjectUsers'

const makePaginationRef = () => ref<Pagination>({ page: 1, perPage: 10, total: 0 })
const makeSortingRef = () => ref<Sorting>({ sortBy: 'createdAt', sortingOrder: 'desc' })

export const useProjects = (options?: { sorting?: Ref<Sorting>; pagination?: Ref<Pagination> }) => {
  const isLoading = ref(false)
  const projectsStore = useProjectsStore()
  const { getUserById } = useProjectUsers()

  const { sorting = makeSortingRef(), pagination = makePaginationRef() } = options ?? {}

  const fetch = async () => {
    isLoading.value = true
    await projectsStore.getAll({
      sorting: unref(sorting),
      pagination: unref(pagination),
    })
    pagination.value = projectsStore.pagination

    isLoading.value = false
  }

  const projects = computed(() => {
    const paginated = projectsStore.items.slice(
      (pagination.value.page - 1) * pagination.value.perPage,
      pagination.value.page * pagination.value.perPage,
    )

    const getSortItem = (obj: any, sortBy: Sorting['sortBy']) => {
      if (sortBy === 'responsible') {
        return getUserById(obj.responsible)?.name
      }

      if (sortBy === 'participants') {
        return obj.participants.map((user: any) => getUserById(user)?.name || '').join(', ')
      }

      if (sortBy === 'createdAt') {
        return new Date(obj[sortBy])
      }

      return obj[sortBy]
    }

    if (sorting.value.sortBy && sorting.value.sortingOrder) {
      paginated.sort((a, b) => {
        a = getSortItem(a, sorting.value.sortBy!)
        b = getSortItem(b, sorting.value.sortBy!)

        if (a < b) {
          return sorting.value.sortingOrder === 'asc' ? -1 : 1
        }
        if (a > b) {
          return sorting.value.sortingOrder === 'asc' ? 1 : -1
        }
        return 0
      })
    }

    return paginated
  })

  fetch()

  return {
    isLoading,

    projects,

    fetch,

    async add(project: Omit<Project, 'id' | 'createdAt'>, errors: object) {
      isLoading.value = true
      try {
        await projectsStore.add(project, errors)
        await fetch()
      } finally {
        isLoading.value = false
      }
    },

    async update(project: Omit<Project, 'createdAt'>, errors: object) {
      isLoading.value = true
      try {
        await projectsStore.update(project, errors)
        await fetch()
      } finally {
        isLoading.value = false
      }
    },

    async remove(project: Project) {
      isLoading.value = true
      try {
        await projectsStore.remove(project)
        await fetch()
      } finally {
        isLoading.value = false
      }
    },

    pagination,
    sorting,
  }
}
