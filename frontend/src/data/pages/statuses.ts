import { Status } from '../../pages/projects/types'
import { api, get } from '../../services/api'

export const getStatuses = async () => {
  const statuses: Status[] = await get(api.allStatuses());
  return {
    data: statuses,
  }
}
