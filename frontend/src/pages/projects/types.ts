import { User } from '../users/types'

export type Status = {
  id: number
  name: string
  displayName: string
}

export type Project = {
  id: number
  name: string
  description: string
  status: Status
  responsible: User['id']
  participants: User['id'][]
  createdAt: string
}

export type EmptyProject = Omit<Project, 'id' | 'status' | 'responsible' | 'createdAt'> & {
  status: Status['id'] | undefined
  responsible: User['id'] | undefined
}
