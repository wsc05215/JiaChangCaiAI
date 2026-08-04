import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    host: '0.0.0.0',
    port: 3001,
    allowedHosts: ['do8mi8132279.vicp.fun', '127969crhh494.vicp.fun', '.vicp.fun'],
    proxy: {
      '/user': 'http://localhost:8080',
      '/follow': 'http://localhost:8080',
      '/recipe': 'http://localhost:8080',
      '/product': 'http://localhost:8080',
      '/favorite': 'http://localhost:8080',
      '/search': 'http://localhost:8080',
      '/member': 'http://localhost:8080',
      '/comment': 'http://localhost:8080',
      '/upload': 'http://localhost:8080',
      '/uploads': 'http://localhost:8080',
      '/chat': 'http://localhost:8080',
      '/RecipechatAI': 'http://localhost:8080',
      '/MyRecipeChatOfAI': 'http://localhost:8080',
      '/cart': 'http://localhost:8080',
      '/address': 'http://localhost:8080',
      '/order-item': 'http://localhost:8080',
      '/meal-plan': 'http://localhost:8080',
    },
  },
  build: {
    outDir: '../src/main/resources/static',
  },
})
