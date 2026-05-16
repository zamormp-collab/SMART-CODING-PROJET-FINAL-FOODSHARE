import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite' // Import du plugin

export default defineConfig({
  plugins: [
    react(),
    tailwindcss(), // Activation de Tailwind v4
  ],
})