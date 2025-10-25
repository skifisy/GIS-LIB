import { createApp } from 'vue'
import App from './App.vue'
import 'leaflet/dist/leaflet.css'
import 'element-plus/dist/index.css'
import ElementPlus from 'element-plus'
import { createRouter, createWebHashHistory } from 'vue-router'
import HomePage from './pages/HomePage.vue'
import AdminPage from './pages/AdminPage.vue'

const routes = [
  { path: '/', component: HomePage },
  { path: '/admin', component: AdminPage }
]

// Use hash history so refreshing nested routes works on static hosts without server rewrite rules.
const router = createRouter({ history: createWebHashHistory(), routes })

createApp(App).use(router).use(ElementPlus).mount('#app')
