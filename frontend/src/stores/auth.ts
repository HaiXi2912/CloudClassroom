import { defineStore } from 'pinia'

type Role = 'ADMIN' | 'TEACHER' | 'STUDENT' | ''

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('cc_token') || '',
    role: (localStorage.getItem('cc_role') as Role) || '',
    userId: Number(localStorage.getItem('cc_userId') || '0') || 0,
    nickname: localStorage.getItem('cc_nickname') || ''
  }),
  actions: {
    setLogin(payload: { token: string; role: string; userId: number; nickname: string }) {
      this.token = payload.token
      this.role = payload.role as Role
      this.userId = payload.userId
      this.nickname = payload.nickname
      localStorage.setItem('cc_token', this.token)
      localStorage.setItem('cc_role', this.role)
      localStorage.setItem('cc_userId', String(this.userId))
      localStorage.setItem('cc_nickname', this.nickname)
    },
    logout() {
      this.token = ''
      this.role = ''
      this.userId = 0
      this.nickname = ''
      localStorage.removeItem('cc_token')
      localStorage.removeItem('cc_role')
      localStorage.removeItem('cc_userId')
      localStorage.removeItem('cc_nickname')
    }
  }
})
