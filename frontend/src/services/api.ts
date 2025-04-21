import { useToast } from 'vuestic-ui';

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL
const { init } = useToast()

export const api = {
  register: () => `${apiBaseUrl}/auth/register`,
  login: () => `${apiBaseUrl}/auth/login`,
  logout: () => `${apiBaseUrl}/auth/logout`,
  allRoles: () => `${apiBaseUrl}/roles`,
  allUsers: () => `${apiBaseUrl}/users`,
  user: (id: number) => `${apiBaseUrl}/users/${id}`,
  users: ({ page, pageSize }: { page: number; pageSize: number }) =>
    `${apiBaseUrl}/users/?page=${page}&pageSize=${pageSize}`,
  allStatuses: () => `${apiBaseUrl}/statuses`,
  allProjects: () => `${apiBaseUrl}/projects`,
  project: (id: number) => `${apiBaseUrl}/projects/${id}`,
  projects: ({ page, pageSize }: { page: number; pageSize: number }) =>
    `${apiBaseUrl}/projects/?page=${page}&pageSize=${pageSize}`,
}

export const get = (url: string) => {
  return request(url, 'GET')
}

export const post = (url: string, data = {}, errors = {}) => {
  return request(url, 'POST', data, errors)
}

export const patch = (url: string, data = {}, errors = {}, isJson = false) => {
  return request(url, 'PATCH', data, errors, isJson)
}

export const put = (url: string, data = {}, errors = {}) => {
  return request(url, 'PUT', data, errors)
}

export const del = (url: string) => {
  return request(url, 'DELETE')
}

const request = async (url: string, method = 'GET', data = {}, errors: Record<string, string[]> = {}, isJson = true) => {
  const options: RequestInit = {
    method,
    credentials: 'include',
  }
  if (isJson) {
    options.headers = { 'Content-Type': 'application/json' }
    if (Object.keys(data).length > 0) {
      options.body = JSON.stringify(data)
    }
  } else {
    options.body = data as FormData
  }
  return await fetch(url, options)
    .then(r => r.json())
    .then(r => {
      if (!r.success) throw r
      return r.data
    })
    .catch(r => {
      if (typeof r.error === 'string') {
        init({ message: r.error, color: 'danger' })
      } else if (typeof r.error === 'object') {
        for (const [field, message] of Object.entries(r.error)) {
          if (field in errors && typeof message === 'string') {
            errors[field] = message.split("|")
          }
        }
      }
      throw r
    })
}
