// 检测是否 Capacitor 原生环境
const isNative = typeof window !== 'undefined' && !!(window.Capacitor)

// 后端地址（原生 App 环境使用）
const BASE = 'http://120.26.207.68:8081'

/**
 * 把相对路径的图片 URL 转为绝对地址
 * 浏览器环境：相对路径由 Vite 代理或 nginx 转发
 * 原生 App 环境：必须用绝对地址
 */
export function resolveImageUrl(url) {
  if (!url) return ''
  // 已经是绝对地址，直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) return url
  // 原生环境补上后端地址
  if (isNative) return BASE + url
  // 浏览器环境保持相对路径
  return url
}
