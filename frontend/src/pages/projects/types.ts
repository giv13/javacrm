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
  responsibleId: User['id']
  participantIds: User['id'][]
  createdAt: string
}

export type EmptyProject = Omit<Project, 'id' | 'status' | 'responsibleId' | 'createdAt'> & {
  status: Status | undefined
  responsibleId: User['id'] | undefined
}
