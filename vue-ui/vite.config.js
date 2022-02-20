import vue from '@vitejs/plugin-vue'
import { defineConfig } from 'vite'

// export default {
//     base: '../file-resolver/src/main/resources/static/',
//     plugins: [vue()],
//     optimizeDeps: {
//         include: ['schart.js']
//     }
// }
export default defineConfig({
    build: {
        outDir: '../file-resolver/src/main/resources/static/' 
    },
    base: '../file-resolver/src/main/resources/static/',
    plugins: [vue()],
    optimizeDeps: {
        include: ['schart.js']
    }
})