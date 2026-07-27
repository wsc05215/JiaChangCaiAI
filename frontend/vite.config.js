import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3001,
    proxy: {
      '/user': 'http://localhost:8080',
      '/follow': 'http://localhost:8080',
      '/recipe': 'http://localhost:8080',
    },
  },
  build: {
    outDir: '../src/main/resources/static',
  },
})
