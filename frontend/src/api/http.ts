import axios from 'axios'
import { ElMessage } from 'element-plus'
import type { ApiResponse } from '@/types/api'
import { useAuthStore } from '@/stores/auth'

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || ''
})

let lastErrorToastAt = 0
function toastErrorOnce(message: string) {
  const now = Date.now()
  if (now - lastErrorToastAt < 1200) return
  lastErrorToastAt = now
  ElMessage.error(message)
}

http.interceptors.request.use((config) => {
  const auth = useAuthStore()
  if (auth.token) {
    config.headers = config.headers ?? {}
    config.headers.Authorization = `Bearer ${auth.token}`
  }
  return config
})

http.interceptors.response.use(
  (resp) => {
    const data = resp.data as ApiResponse<unknown>
    if (data && typeof data.code === 'number' && data.code !== 0) {
      if (data.code === 40100) {
        const auth = useAuthStore()
        auth.logout()
        window.location.href = '/login'
        return Promise.reject(new Error(data.message || '未登录'))
      }
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message || '请求失败'))
    }
    return resp
  },
  (err) => {
    if (axios.isAxiosError(err)) {
      if (err.response) {
        const status = err.response.status
        const configUrl = err.config?.url || ''

        // Vite dev server 代理到后端失败时，常见会直接返回 500（并非后端业务 500）
        const originPort = typeof window !== 'undefined' ? window.location?.port : ''
        const respData = err.response.data as unknown
        const respText =
          typeof respData === 'string'
            ? respData
            : respData
              ? JSON.stringify(respData)
              : ''

        // Vite 代理失败的 500 有时不是标准的 ECONNREFUSED 文本，可能是空响应/HTML/“proxying request”提示
        const looksLikeProxyError =
          originPort === '5174' &&
          configUrl.startsWith('/api') &&
          status === 500 &&
          (!respText || /ECONNREFUSED|ETIMEDOUT|socket hang up|connect|proxy(ing)?|Error occurred while proxying request|AggregateError/i.test(respText))

        if (looksLikeProxyError) {
          toastErrorOnce('网络错误：后端未启动或 /api 代理失败（8081）')
        } else {
          toastErrorOnce(`请求失败（HTTP ${status}）：${configUrl || '未知接口'}`)
        }

        try {
          // eslint-disable-next-line no-console
          console.warn('[http] response error', {
            status,
            url: configUrl,
            baseURL: err.config?.baseURL,
            data: err.response.data
          })
        } catch {
          // ignore
        }

        return Promise.reject(err)
      }

      const configUrl = err.config?.url || ''
      const configBase = err.config?.baseURL || ''

      const isFile = typeof window !== 'undefined' && window.location?.protocol === 'file:'
      const isDevProxy =
        typeof window !== 'undefined' &&
        window.location?.port === '5174' &&
        !import.meta.env.VITE_API_BASE &&
        configUrl.startsWith('/api')

      let hint = '网络错误：无法连接后端'
      if (isFile) {
        hint = '网络错误：请用 `npm run dev` 访问前端，不要直接打开 dist/index.html'
      } else if (!isDevProxy && !import.meta.env.VITE_API_BASE && configUrl.startsWith('/api')) {
        hint = '网络错误：当前前端未走 /api 反代（需要 Vite dev 或反向代理）'
      }

      // 控制台输出更细的定位信息（避免 toast 太长）
      try {
        // eslint-disable-next-line no-console
        console.warn('[http] network error', {
          message: err.message,
          code: err.code,
          url: configUrl,
          baseURL: configBase,
          origin: typeof window !== 'undefined' ? window.location?.origin : undefined
        })
      } catch {
        // ignore
      }

      toastErrorOnce(hint)
      return Promise.reject(err)
    }

    toastErrorOnce('网络错误')
    return Promise.reject(err)
  }
)
