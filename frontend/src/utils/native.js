// 检测是否 Capacitor 原生环境(iOS/Android App)
const isNative = typeof window !== 'undefined' && !!(
  window.Capacitor &&
  (window.Capacitor.isNativePlatform ? window.Capacitor.isNativePlatform() : true)
)

// 后端地址(原生 App 环境使用;浏览器环境由 Vite 代理或 nginx 转发,保持相对路径)
const BASE = 'http://120.26.207.68:8081'

/**
 * 把相对路径的资源 URL(图片、视频、API 等)转为绝对地址
 * 浏览器环境:保持相对路径(由代理/nginx 处理)
 * 原生 App 环境:/uploads、/recipes、/chat、/member 等相对路径必须补上后端地址
 */
function resolveUrl(url) {
  if (!url) return ''
  if (typeof url !== 'string') return url
  // 已经是绝对地址(http/https 或协议相对),直接返回
  if (/^(https?:)?\/\//.test(url)) return url
  // 原生环境补上后端地址
  if (isNative && url.startsWith('/')) return BASE + url
  return url
}

/**
 * 兼容旧用法:资源(图片/视频)URL 转绝对地址
 */
function resolveImageUrl(url) {
  return resolveUrl(url)
}

export { isNative, BASE, resolveUrl, resolveImageUrl }
