import { useToast } from 'vuestic-ui';

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL
const { init } = useToast()

export const api = {
  register: () => `${apiBaseUrl}/auth/register`,
  login: () => `${apiBaseUrl}/auth/login`,
  logout: () => `${apiBaseUrl}/auth/logout`,
  allUsers: () => `${apiBaseUrl}/users`,
  user: (id: string) => `${apiBaseUrl}/users/${id}`,
  users: ({ page, pageSize }: { page: number; pageSize: number }) =>
    `${apiBaseUrl}/users/?page=${page}&pageSize=${pageSize}`,
  allProjects: () => `${apiBaseUrl}/projects`,
  project: (id: string) => `${apiBaseUrl}/projects/${id}`,
  projects: ({ page, pageSize }: { page: number; pageSize: number }) =>
    `${apiBaseUrl}/projects/?page=${page}&pageSize=${pageSize}`,
  avatars: () => `${apiBaseUrl}/avatars`,
}

export const post = (url: string, data = {}, errors = {}) => {
  return request(url, 'POST', data, errors)
}

const request = async (url: string, method = 'GET', data = {}, errors: Record<string, string[]> = {}) => {
  return await fetch(url, {
    method,
    credentials: 'include',
    body: JSON.stringify(data),
    headers: {
      'Content-Type': 'application/json',
    },
  }).then(r => r.json())
    .then(r => {
      if (!r.success) throw r
      return r
    })
    .catch(r => {
      if (typeof r.error === 'string') {
        init({ message: r.error, color: 'danger' })
      } else {
        for (const [field, message] of Object.entries(r.error)) {
          if (field in errors && typeof message === 'string') {
            errors[field] = message.split("|")
          }
        }
      }
      throw r
    })
}
