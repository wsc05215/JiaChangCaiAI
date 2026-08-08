import { createApp } from 'vue'
import App from './App.vue'
import router, { goBack } from './router'
import './styles/main.css'

const app = createApp(App)
app.use(router)

// 全局安全回退方法：手动记录上一页路由，用 push 代替 history.back()，避免 Capacitor WebView 回退失效/刷新
app.config.globalProperties.$goBack = function (fallback) {
  if (goBack()) return
  this.$router.push(fallback || '/home')
}

app.mount('#app')
