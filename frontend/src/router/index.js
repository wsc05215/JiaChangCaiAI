import { createRouter, createWebHashHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import HomeView from '../views/HomeView.vue'
import ProfileView from '../views/ProfileView.vue'
import RecipeDetail from '../views/RecipeDetail.vue'
import MyRecipesView from '../views/MyRecipesView.vue'
import AiChatView from '../views/AiChatView.vue'
import FavoritesView from '../views/FavoritesView.vue'

const routes = [
  { path: '/', name: 'Login', component: LoginView },
  { path: '/home', name: 'Home', component: HomeView },
  { path: '/profile', name: 'Profile', component: ProfileView },
  { path: '/recipe/:id', name: 'RecipeDetail', component: RecipeDetail },
  { path: '/my-recipes', name: 'MyRecipes', component: MyRecipesView },
  { path: '/ai-chat', name: 'AiChat', component: AiChatView },
  { path: '/member', name: 'Member', component: { template: '<div style="padding-top:200px;text-align:center;color:#B99E8E;">会员开发中...</div>' } },
  { path: '/favorites', name: 'Favorites', component: FavoritesView },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

export default router
