import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  // For GitHub Pages project site, BASE_PATH will be like "/<repo-name>/"
  base: process.env.BASE_PATH || '/',
  plugins: [vue()],
  server: {
    proxy: {
      '/api': 'http://localhost:8080'
    }
  }
})
