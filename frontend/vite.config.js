import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import { VitePWA } from 'vite-plugin-pwa'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), 
    VitePWA({
      registerType: 'autoUpdate',
      includeAssets: ['favicon.ico', 'robots.txt', 'apple-touch-icon.png', 'pwa-192x192.png', 'pwa-512x512.png', 'pwa-512x512-maskable.png'],
      manifest: {
        id: '/',
        name: 'SwiftCart - Ecommerce App',
        short_name: 'SwiftCart',
        description: 'Buy & sell products hassle free online throughout the world',
        start_url: '/',
        scope: '/',
        display: 'standalone',
        theme_color: '#FF9800',
        background_color: '#ffffff',
        orientation: 'portrait',
        lang: 'en-US',
        dir: 'ltr',
        categories: ['shopping', 'lifestyle', 'business', 'productivity'],
        icons: [
          { src: '/pwa-192x192.png', sizes: '192x192', type: 'image/png' },
          { src: '/pwa-512x512.png', sizes: '512x512', type: 'image/png' },
          { src: '/pwa-512x512-maskable.png', sizes: '512x512', type: 'image/png', purpose: 'maskable any' }
        ],
        shortcuts: [
          {
            name: 'My Cart',
            short_name: 'Cart',
            url: '/cart',
            icons: [{ src: '/icons/shortcut-inbox.png', sizes: '96x96' }]
          }
        ]
      },

      workbox: {
        globPatterns: ['**/*.{js,css,html,png,svg,ico,json}'],
        runtimeCaching: [
          {
            urlPattern: /^https?:.*\/api\/.*$/,
            handler: 'NetworkFirst',
            options: {
              cacheName: 'api-cache',
              expiration: { maxEntries: 50, maxAgeSeconds: 60 * 60 * 24 } // 1 day
            }
          }
        ]
      },

      devOptions: {
        enabled: true
      }
    })
  ],
})
