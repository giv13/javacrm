import { Project } from '../../pages/projects/types'
import { api, get, post, put, del } from '../../services/api'

export type Pagination = {
  page: number
  perPage: number
  total: number
}

export type Sorting = {
  sortBy: 'responsible' | 'participants' | 'createdAt'
  sortingOrder: 'asc' | 'desc' | null
}

export const getProjects = async (options: Partial<Sorting> & Pagination) => {
  const projects: Project[] = await get(api.allProjects())

  return {
    data: projects,
    pagination: {
      page: options.page,
      perPage: options.perPage,
      total: projects.length,
    },
  }
}

export const addProject = async (project: Omit<Project, 'id' | 'createdAt'>, errors: object) => {
  return post(api.allProjects(), project, errors)
}

export const updateProject = async (project: Omit<Project, 'createdAt'>, errors: object) => {
  return put(api.project(project.id), project, errors)
}

export const removeProject = async (project: Project) => {
  return del(api.project(project.id))
}
