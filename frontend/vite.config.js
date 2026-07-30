import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    host: '0.0.0.0',
    port: 3001,
    allowedHosts: ['do8mi8132279.vicp.fun', '.vicp.fun'],
    proxy: {
      '/user': 'http://localhost:8080',
      '/follow': 'http://localhost:8080',
      '/recipe': 'http://localhost:8080',
      '/product': 'http://localhost:8080',
      '/favorite': 'http://localhost:8080',
      '/chat': 'http://localhost:8080',
    },
  },
  build: {
    outDir: '../src/main/resources/static',
  },
})
