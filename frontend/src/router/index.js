import { createRouter, createWebHashHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import HomeView from '../views/HomeView.vue'
import ProfileView from '../views/ProfileView.vue'

const routes = [
  { path: '/', name: 'Login', component: LoginView },
  { path: '/home', name: 'Home', component: HomeView },
  { path: '/profile', name: 'Profile', component: ProfileView },
  { path: '/member', name: 'Member', component: { template: '<div style="padding-top:200px;text-align:center;color:#B99E8E;">会员开发中...</div>' } },
  { path: '/favorites', name: 'Favorites', component: { template: '<div style="padding-top:200px;text-align:center;color:#B99E8E;">收藏开发中...</div>' } },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

export default router
