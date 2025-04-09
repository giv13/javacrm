import { useUsers } from '../../users/composables/useUsers'
import { Project } from '../types'

export function useProjectUsers() {
  const { users } = useUsers()

  const getUserById = (userId: number) => {
    return users.value.find(({ id }) => userId === id)
  }

  const avatarColor = (userName: string) => {
    const colors = ['primary', '#FFD43A', '#ADFF00', '#262824', 'danger']
    const index = userName.charCodeAt(0) % colors.length
    return colors[index]
  }

  const getParticipantsOptions = (participants: Project['participants']) => {
    return participants.reduce(
      (acc, userId) => {
        const user = getUserById(userId)

        if (user) {
          acc.push({
            label: user.name,
            src: user.avatar,
            fallbackText: user.name[0],
            color: avatarColor(user.name),
          })
        }

        return acc
      },
      [] as Record<string, string>[],
    )
  }

  return {
    users,
    getUserById,
    getParticipantsOptions,
  }
}
