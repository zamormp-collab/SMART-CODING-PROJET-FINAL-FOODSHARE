import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite' // Import du plugin

export default defineConfig({
  plugins: [
    react(),
    tailwindcss(), // Activation de Tailwind v4
  ],
  server: {
    port: 5173,
    strictPort: true,
    proxy: {
      // Dès que React appelle une URL commençant par /api
      '/api': {
        target: 'https://chance-casino-jumble.ngrok-free.dev/',
        changeOrigin: true,
        secure: false,
      }
    }
  }
})
