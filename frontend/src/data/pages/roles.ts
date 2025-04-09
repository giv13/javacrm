import { Role } from '../../pages/users/types'
import { api, get } from '../../services/api'

export const getRoles = async () => {
  const roles: Role[] = await get(api.allRoles());
  return {
    data: roles,
  }
}
