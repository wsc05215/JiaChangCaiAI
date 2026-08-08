import axios from 'axios'
import { isNative, BASE, resolveImageUrl } from './native'

const request = axios.create({
  baseURL: isNative ? BASE : '/',
  timeout: 30000,
})

// 原生 App 环境下:把后端返回的相对资源路径(/uploads、/recipes 等)自动补全为绝对地址,
// 否则 capacitor://localhost 下图片无法加载。递归遍历对象/数组,只改字符串字段。
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
          data[key] = resolveImageUrl(v)
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
