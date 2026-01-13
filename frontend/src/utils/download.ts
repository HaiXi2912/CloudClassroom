import type { AxiosResponse } from 'axios'

export function parseFilenameFromContentDisposition(contentDisposition?: string): string | undefined {
  if (!contentDisposition) return undefined

  // RFC 5987: filename*=UTF-8''...
  const star = /filename\*=UTF-8''([^;]+)/i.exec(contentDisposition)
  if (star?.[1]) {
    try {
      return decodeURIComponent(star[1])
    } catch {
      return star[1]
    }
  }

  const normal = /filename="?([^";]+)"?/i.exec(contentDisposition)
  return normal?.[1]
}

export function triggerBrowserDownload(blob: Blob, filename?: string) {
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename || 'download'
  document.body.appendChild(a)
  a.click()
  a.remove()
  window.URL.revokeObjectURL(url)
}

export function downloadFromAxiosResponse(resp: AxiosResponse<Blob>, fallbackName: string) {
  const cd = (resp.headers?.['content-disposition'] as string | undefined) ?? undefined
  const filename = parseFilenameFromContentDisposition(cd) || fallbackName
  triggerBrowserDownload(resp.data, filename)
}
