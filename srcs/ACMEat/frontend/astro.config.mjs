// @ts-check
import { defineConfig } from 'astro/config';
import dotenv from 'dotenv';
dotenv.config();

const PUBLIC_API_HOST =  process.env.PUBLIC_API_HOST;
console.log("PUBLIC_API_HOST",PUBLIC_API_HOST)

// https://astro.build/config
export default defineConfig({
    vite: {
       server: {
            proxy: {
                // Regex per matchare percorsi che iniziano con /api
                '^/api/.*': {
                    target: PUBLIC_API_HOST,
                    changeOrigin: true,
                    secure: false,
                    rewrite: (path) => path, // Mantiene il path originale
                }
            }
        }
    }
});