import axios from 'axios'
import { isNative, BASE, resolveUrl } from './native'

const request = axios.create({
  baseURL: isNative ? BASE : '/',
  timeout: 30000,
})

// 原生 App 环境下:把后端返回的相对资源路径(/uploads、/recipes 等)自动补全为绝对地址,
// 否则 capacitor://localhost 下图片/视频无法加载。
// 说明:coverImages 这类字段是 JSON 数组字符串(如 ["/uploads/a.jpg",...]),
// 需把数组里的每一项相对路径也一并改写成绝对地址。

// 处理 JSON 数组字符串里的相对路径元素(封面图、多图、视频等)
function rewriteJsonPaths(v) {
  try {
    const arr = JSON.parse(v)
    if (Array.isArray(arr)) {
      let changed = false
      const out = arr.map((item) => {
        if (typeof item === 'string' && item.startsWith('/')) {
          changed = true
          return resolveUrl(item)
        }
        return item
      })
      return changed ? JSON.stringify(out) : v
    }
  } catch (e) {
    // 不是合法 JSON,保持原样
  }
  return v
}

function rewritePaths(data) {
  if (Array.isArray(data)) {
    for (let i = 0; i < data.length; i++) {
      data[i] = rewritePaths(data[i])
    }
  } else if (data && typeof data === 'object') {
    for (const key of Object.keys(data)) {
      const v = data[key]
      if (typeof v === 'string') {
        if (v.startsWith('/uploads') || v.startsWith('/recipes')) {
          data[key] = resolveUrl(v)
        } else if (v.startsWith('[') && v.indexOf('"/') !== -1) {
          // JSON 数组字符串里带相对路径(如 coverImages 的 ["/uploads/..."])
          data[key] = rewriteJsonPaths(v)
        }
      } else if (v && typeof v === 'object') {
        data[key] = rewritePaths(v)
      }
    }
  }
  return data
}

if (isNative) {
  request.interceptors.response.use((resp) => {
    if (resp && resp.data) rewritePaths(resp.data)
    return resp
  })
}

export default request
