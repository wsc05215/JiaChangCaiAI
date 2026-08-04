<template>
  <div class="cart-page">
    <!-- 导航 -->
    <div class="nav-bar">
      <div class="back-btn" @click="$router.back()">
        <svg viewBox="0 0 24 24" width="22" height="22">
          <path d="M15 18l-6-6 6-6" stroke="#5a524c" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <span class="nav-title">购物车</span>
      <div class="nav-placeholder"></div>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="loading-wrap"><p>加载中...</p></div>

    <!-- 空购物车 -->
    <div v-else-if="cartList.length === 0" class="empty-wrap">
      <svg viewBox="0 0 24 24" width="64" height="64">
        <circle cx="9" cy="21" r="1" fill="#d5cfc7"/><circle cx="20" cy="21" r="1" fill="#d5cfc7"/>
        <path d="M1 1h4l2.68 13.39a2 2 0 002 1.61h9.72a2 2 0 002-1.61L23 6H6" stroke="#d5cfc7" stroke-width="1.5" fill="none" stroke-linecap="round"/>
      </svg>
      <p class="empty-text">购物车为空</p>
      <button class="go-shop-btn" @click="$router.push('/home')">去逛逛</button>
    </div>

    <!-- 商品列表 -->
    <div v-else class="cart-list">
      <div v-for="item in cartList" :key="item.cartId" class="cart-item">
        <!-- 勾选框 -->
        <div class="check-box" :class="{ checked: item.selected === 1 }" @click="toggleSelect(item)">
          <svg v-if="item.selected === 1" viewBox="0 0 24 24" width="16" height="16">
            <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" fill="#fff"/>
          </svg>
        </div>

        <!-- 商品图片 -->
        <img v-if="item.productImage" :src="item.productImage" class="item-img" />
        <div v-else class="item-img-placeholder"></div>

        <!-- 商品信息 -->
        <div class="item-info">
          <div class="item-name">{{ item.productName }}</div>
          <div class="item-price">¥{{ item.productPrice }}</div>
        </div>

        <!-- 数量控制 -->
        <div class="qty-ctrl">
          <button class="qty-btn" @click="decrease(item)">−</button>
          <span class="qty-num">{{ item.quantity }}</span>
          <button class="qty-btn" @click="increase(item)">+</button>
        </div>
      </div>
    </div>

    <!-- 底部结算栏 -->
    <div v-if="cartList.length > 0" class="bottom-bar">
      <div class="total-row">
        <span>合计：</span>
        <span class="total-price">¥{{ totalPrice }}</span>
      </div>
      <button class="checkout-btn" @click="handleCheckout">结算</button>
    </div>

    <transition name="fade">
      <div v-if="toast.show" class="toast" :class="toast.type">{{ toast.msg }}</div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCart } from '../api/shop'
import { userStore } from '../store/user'

const router = useRouter()
const cartList = ref([])
const loading = ref(true)
const toast = reactive({ show: false, msg: '', type: 'success' })

const totalPrice = computed(() => {
  return cartList.value
    .filter(i => i.selected === 1)
    .reduce((sum, i) => sum + (i.productPrice || 0) * i.quantity, 0)
    .toFixed(2)
})

function showToast(msg, type = 'success') {
  toast.msg = msg
  toast.type = type
  toast.show = true
  setTimeout(() => { toast.show = false }, 2000)
}

onMounted(async () => {
  const uid = userStore.user?.userId
  if (!uid) {
    loading.value = false
    return
  }
  try {
    const res = await getCart(uid)
    cartList.value = res.data || []
  } catch {
    showToast('加载失败', 'error')
  } finally {
    loading.value = false
  }
})

function toggleSelect(item) {
  item.selected = item.selected === 1 ? 0 : 1
}

function increase(item) {
  item.quantity++
}

function decrease(item) {
  if (item.quantity > 1) item.quantity--
}

function handleCheckout() {
  const selected = cartList.value.filter(i => i.selected === 1)
  if (selected.length === 0) return showToast('请选择商品', 'error')
  router.push('/checkout')
}
</script>

<style scoped>
.cart-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: #FBF8F4;
  padding-bottom: 80px;
}

.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: rgba(255,255,255,0.9);
  backdrop-filter: blur(20px);
  position: sticky;
  top: 0;
  z-index: 10;
}

.back-btn { width: 36px; height: 36px; display: flex; align-items: center; justify-content: center; cursor: pointer; }
.nav-title { font-family: var(--font-heading); font-size: 17px; font-weight: 700; color: var(--text-primary); }
.nav-placeholder { width: 36px; }

.loading-wrap { display: flex; justify-content: center; align-items: center; height: 60vh; color: var(--text-muted); }

.empty-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 60vh;
  gap: 16px;
}

.empty-text { font-size: 15px; color: var(--text-muted); }

.go-shop-btn {
  padding: 10px 32px;
  border-radius: 24px;
  background: var(--gradient-primary);
  color: #fff;
  font-family: var(--font-heading);
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 2px;
  box-shadow: var(--shadow-primary);
}

.cart-list {
  padding: 12px 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #fff;
  border-radius: 16px;
  padding: 14px;
}

.check-box {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  border: 2px solid var(--border);
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.check-box.checked {
  background: var(--primary);
  border-color: var(--primary);
}

.item-img {
  width: 72px;
  height: 72px;
  border-radius: 10px;
  object-fit: cover;
  flex-shrink: 0;
  background: #F5F0E8;
}

.item-img-placeholder {
  width: 72px;
  height: 72px;
  border-radius: 10px;
  flex-shrink: 0;
  background: #F5F0E8;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-name {
  font-family: var(--font-heading);
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-price {
  font-family: var(--font-heading);
  font-size: 17px;
  font-weight: 800;
  color: var(--primary);
  margin-top: 6px;
}

.qty-ctrl {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.qty-btn {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: 1.5px solid var(--border);
  background: #fff;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.qty-btn:active { border-color: var(--primary); color: var(--primary); }

.qty-num {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
  min-width: 20px;
  text-align: center;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 20px;
  padding-bottom: calc(14px + env(safe-area-inset-bottom, 0px));
  background: rgba(255,255,255,0.95);
  backdrop-filter: blur(20px);
  box-shadow: 0 -1px 8px rgba(0,0,0,0.04);
}

.total-row {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
}

.total-price {
  font-family: var(--font-heading);
  font-size: 22px;
  font-weight: 800;
  color: var(--primary);
}

.checkout-btn {
  padding: 12px 32px;
  border-radius: 24px;
  background: var(--gradient-primary);
  color: #fff;
  font-family: var(--font-heading);
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 2px;
  box-shadow: var(--shadow-primary);
  transition: all 0.2s;
}

.checkout-btn:active { transform: scale(0.95); }

.toast {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  padding: 14px 36px;
  border-radius: 14px;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  z-index: 999;
  pointer-events: none;
  letter-spacing: 1px;
  font-family: var(--font-heading);
}

.toast.success { background: rgba(18, 30, 31, 0.88); }
.toast.error { background: rgba(180, 60, 20, 0.9); }

.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
