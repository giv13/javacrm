import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => {
    return {
      userName: 'Vasili Savitski',
      email: 'vasili@gmail.com',
      memberSince: '8/12/2020',
      pfp: 'https://picsum.photos/id/22/200/300',
      is2FAEnabled: false,
    }
  },

  actions: {
    changeUserName(userName: string) {
      this.userName = userName
    },
  },
})
