import { defineStore } from 'pinia'
import Cookies from 'js-cookie'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: Cookies.get('token') || '',
    userInfo: null
  }),
  
  actions: {
    setToken(token) {
      this.token = token
      Cookies.set('token', token, { expires: 7 }) // 7天过期
    },
    
    setUserInfo(userInfo) {
      this.userInfo = userInfo
    },
    
    logout() {
      this.token = ''
      this.userInfo = null
      Cookies.remove('token')
    }
  }
})
