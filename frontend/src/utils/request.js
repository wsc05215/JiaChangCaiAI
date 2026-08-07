import axios from 'axios'

// Capacitor App 环境使用绝对地址，浏览器环境使用相对地址（走 Vite 代理）
const isNative = typeof window !== 'undefined' && !!(window.Capacitor)

const request = axios.create({
  baseURL: isNative ? 'http://120.26.207.68:8081' : '/',
  timeout: 30000,
})

export default request
