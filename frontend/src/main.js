import { createApp } from 'vue'
import App from './App.vue'
import 'leaflet/dist/leaflet.css'
import { createRouter, createWebHistory } from 'vue-router'
import HomePage from './pages/HomePage.vue'
import AdminPage from './pages/AdminPage.vue'

const routes = [
  { path: '/', component: HomePage },
  { path: '/admin', component: AdminPage }
]

const router = createRouter({ history: createWebHistory(), routes })

createApp(App).use(router).mount('#app')
