import { useUsers } from '../../users/composables/useUsers'
import { Project } from '../types'
import { ref } from "vue";
import type { Filters } from "../../../data/pages/users";

export function useProjectUsers() {
  const { users } = useUsers({
    filters: ref<Partial<Filters>>({ isActive: undefined })
  })

  const getUserById = (userId: number) => {
    return users.value.find(({ id }) => userId === id)
  }

  const avatarColor = (userName: string) => {
    const colors = ['primary', '#FFD43A', '#ADFF00', '#262824', 'danger']
    const index = userName.charCodeAt(0) % colors.length
    return colors[index]
  }

  const avatarPath = (avatar: string) => {
    if (!avatar) return ''
    if (/^([0-9a-zA-Z+/]{4})*(([0-9a-zA-Z+/]{2}==)|([0-9a-zA-Z+/]{3}=))?$/.test(avatar)) return 'data:image/jpeg;base64,' + avatar
    return avatar
  }

  const getParticipantsOptions = (participants: Project['participants']) => {
    return participants.reduce(
      (acc, userId) => {
        const user = getUserById(userId)

        if (user) {
          acc.push({
            label: user.name,
            src: avatarPath(user.avatar),
            fallbackText: user.name[0],
            color: avatarColor(user.name),
            title: user.name,
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
