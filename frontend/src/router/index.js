import { createRouter, createWebHashHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import HomeView from '../views/HomeView.vue'
import ProfileView from '../views/ProfileView.vue'
import RecipeDetail from '../views/RecipeDetail.vue'
import MyRecipesView from '../views/MyRecipesView.vue'
import AiChatView from '../views/AiChatView.vue'
import FavoritesView from '../views/FavoritesView.vue'
import SearchView from '../views/SearchView.vue'
import MemberView from '../views/MemberView.vue'
import UploadRecipeView from '../views/UploadRecipeView.vue'
import FindPasswordView from '../views/FindPasswordView.vue'
import ProductDetailView from '../views/ProductDetailView.vue'
import CartView from '../views/CartView.vue'
import CheckoutView from '../views/CheckoutView.vue'
import AddressListView from '../views/AddressListView.vue'
import PayView from '../views/PayView.vue'
import OrderView from '../views/OrderView.vue'
import OrderDetailView from '../views/OrderDetailView.vue'
import MenuPlanView from '../views/MenuPlanView.vue'
import SettingsView from '../views/SettingsView.vue'
import IngredientRecord from '../views/IngredientRecord.vue'
import CustomView from '../views/CustomView.vue'
import UserProfileView from '../views/UserProfileView.vue'
import NotificationsView from '../views/NotificationsView.vue'
import FollowListView from '../views/FollowListView.vue'

const routes = [
  { path: '/address', name: 'AddressList', component: AddressListView },
  { path: '/cart', name: 'Cart', component: CartView },
  { path: '/checkout', name: 'Checkout', component: CheckoutView },
  { path: '/pay', name: 'Pay', component: PayView },
  { path: '/orders', name: 'Orders', component: OrderView },
  { path: '/order/:id', name: 'OrderDetail', component: OrderDetailView },
  { path: '/', name: 'Login', component: LoginView },
  { path: '/find-password', name: 'FindPassword', component: FindPasswordView },
  { path: '/home', name: 'Home', component: HomeView },
  { path: '/profile', name: 'Profile', component: ProfileView },
  { path: '/recipe/:id', name: 'RecipeDetail', component: RecipeDetail },
  { path: '/my-recipes', name: 'MyRecipes', component: MyRecipesView },
  { path: '/ai-chat', name: 'AiChat', component: AiChatView },
  { path: '/member', name: 'Member', component: MemberView },
  { path: '/custom', name: 'Custom', component: CustomView },
  { path: '/favorites', name: 'Favorites', component: FavoritesView },
  { path: '/search', name: 'Search', component: SearchView },
  { path: '/upload-recipe', name: 'UploadRecipe', component: UploadRecipeView },
  { path: '/product/:id', name: 'ProductDetail', component: ProductDetailView },
  { path: '/menu-plan', name: 'MenuPlan', component: MenuPlanView },
  { path: '/settings', name: 'Settings', component: SettingsView },
  { path: '/ingredient-record', name: 'IngredientRecord', component: IngredientRecord },
  { path: '/user/:id', name: 'UserProfile', component: UserProfileView },
  { path: '/notifications', name: 'Notifications', component: NotificationsView },
  { path: '/following/:id', name: 'Following', component: FollowListView },
  { path: '/followers/:id', name: 'Followers', component: FollowListView },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

// 手动记录上一页路由，避免 Capacitor WebView 中 history.back() 导致刷新
const stack = []
let popping = false

router.afterEach((to, from) => {
  if (popping) { popping = false; return }
  if (from.name && from.name !== to.name) {
    stack.push(from)
  }
})

export function goBack() {
  const prev = stack.pop()
  if (prev) {
    popping = true
    router.push(prev.fullPath)
    return true
  }
  return false
}

export default router
