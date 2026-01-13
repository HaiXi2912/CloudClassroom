import { http } from './http'
import type { ApiResponse } from '@/types/api'

export type LoginResponse = {
  userId: number
  nickname: string
  role: string
  token: string
}

export async function loginApi(username: string, password: string): Promise<LoginResponse> {
  const resp = await http.post<ApiResponse<LoginResponse>>('/api/auth/login', { username, password })
  return resp.data.data
}

export async function registerApi(username: string, password: string, nickname: string, role: 'STUDENT' | 'TEACHER'): Promise<void> {
  await http.post<ApiResponse<void>>('/api/auth/register', { username, password, nickname, role })
}
