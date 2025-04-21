import { Project } from '../projects/types'

export type Role = {
  id: number
  name: string
  displayName: string
}

export type User = {
  id: number
  name: string
  username: string
  email: string
  password: string
  passwordConfirmation: string
  notes: string
  avatar: string
  active: boolean
  roles: Role[]
  projects: Project['id'][]
}

export type EmptyUser = Omit<User, 'id' | 'roles' | 'projects'> & {
  roles: Role['id'][]
}
