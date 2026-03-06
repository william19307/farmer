import { defineStore } from 'pinia'
import { login as loginApi, logout as logoutApi, getUserInfo } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: null,
    permissions: [],
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    username: (state) => state.userInfo?.username || '',
    realName: (state) => state.userInfo?.realName || '',
  },

  actions: {
    async login(username, password) {
      const res = await loginApi({ username, password })
      this.token = res.data.token
      localStorage.setItem('token', res.data.token)
      this.userInfo = {
        userId: res.data.userId,
        username: res.data.username,
        realName: res.data.realName,
        regionId: res.data.regionId,
        roleId: res.data.roleId,
      }
    },

    async fetchUserInfo() {
      const res = await getUserInfo()
      this.userInfo = res.data
    },

    async logout() {
      try {
        await logoutApi()
      } finally {
        this.token = ''
        this.userInfo = null
        localStorage.removeItem('token')
        router.push('/login')
      }
    },

    clearToken() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
    },
  },
})
