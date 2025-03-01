import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => {
    return {
      auth: !!localStorage.getItem('user'),
      user: JSON.parse(localStorage.getItem('user') || 'null'),
    }
  },

  actions: {
    login(userData: object) {
      this.auth = true;
      this.user = userData;
      localStorage.setItem('user', JSON.stringify(userData));
    },
    logout() {
      this.auth = false;
      this.user = null;
      localStorage.removeItem('user');
    },
  },
})
