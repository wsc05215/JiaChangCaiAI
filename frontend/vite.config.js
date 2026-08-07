import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    host: '0.0.0.0',
    port: 3001,
    allowedHosts: ['do8mi8132279.vicp.fun', '127969crhh494.vicp.fun', '.vicp.fun'],
    proxy: {
      '/user': 'http://localhost:8081',
      '/follow': 'http://localhost:8081',
      '/recipe': 'http://localhost:8081',
      '/product': 'http://localhost:8081',
      '/favorite': 'http://localhost:8081',
      '/search': 'http://localhost:8081',
      '/member': 'http://localhost:8081',
      '/comment': 'http://localhost:8081',
      '/upload': 'http://localhost:8081',
      '/uploads': 'http://localhost:8081',
      '/chat': 'http://localhost:8081',
      '/RecipechatAI': 'http://localhost:8081',
      '/MyRecipeChatOfAI': 'http://localhost:8081',
      '/cart': 'http://localhost:8081',
      '/address': 'http://localhost:8081',
      '/order-item': 'http://localhost:8081',
      '/meal-plan': 'http://localhost:8081',
      '/ingredient': 'http://localhost:8081',
      '/custom': 'http://localhost:8081',
    },
  },
  build: {
    outDir: '../src/main/resources/static',
  },
})
