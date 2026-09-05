import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig(() => ({
  plugins: [
    react(),
    tailwindcss(),
  ],
  server: {
    allowedHosts: true as const,
    hmr: false,
    proxy: {
      '/api/analyze': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
      '/api/reports': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
    },
  },
}))
