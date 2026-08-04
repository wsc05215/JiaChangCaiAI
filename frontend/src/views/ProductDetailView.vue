<template>
  <div class="detail-page">
    <!-- 顶部导航 -->
    <div class="nav-bar">
      <div class="back-btn" @click="$router.back()">
        <svg viewBox="0 0 24 24" width="22" height="22">
          <path d="M15 18l-6-6 6-6" stroke="#5a524c" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <span class="nav-title">商品详情</span>
      <div class="nav-placeholder"></div>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="loading-wrap">
      <p>加载中...</p>
    </div>

    <!-- 商品信息 -->
    <template v-else-if="product">
      <!-- 商品图片 -->
      <div class="image-area">
        <img v-if="currentImage" :src="currentImage" class="main-image" />
        <div v-else class="image-placeholder">
          <svg viewBox="0 0 24 24" width="48" height="48">
            <rect x="2" y="2" width="20" height="20" rx="3" stroke="#ccc" stroke-width="1.5" fill="none"/>
            <circle cx="8.5" cy="8.5" r="1.5" fill="#ccc"/>
            <path d="M22 16l-5-5-5 5-3-3-7 7" stroke="#ccc" stroke-width="1.5" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <!-- 图集切换 -->
        <div v-if="imageList.length > 1" class="image-dots">
          <span v-for="(img, i) in imageList" :key="i"
                class="dot" :class="{ active: i === currentImageIndex }"
                @click="currentImageIndex = i"></span>
        </div>
      </div>

      <!-- 基本信息 -->
      <div class="info-card">
        <div class="product-name">{{ product.name }}</div>
        <div v-if="product.subtitle" class="product-subtitle">{{ product.subtitle }}</div>

        <!-- 标签 -->
        <div v-if="product.tags" class="tag-row">
          <span v-for="tag in tagList" :key="tag" class="tag">{{ tag }}</span>
        </div>

        <!-- 价格 -->
        <div class="price-row">
          <span class="price">¥{{ product.price }}</span>
          <span v-if="product.originPrice" class="origin-price">¥{{ product.originPrice }}</span>
        </div>

        <!-- 销量/库存 -->
        <div class="meta-row">
          <span>已售 {{ product.sales || 0 }}{{ product.unit ? '/' + product.unit : '' }}</span>
          <span v-if="product.stock != null" :class="product.stock > 0 ? 'in-stock' : 'out-stock'">
            {{ product.stock > 0 ? '库存' + product.stock + (product.unit || '件') : '暂时缺货' }}
          </span>
        </div>
      </div>

      <!-- 底部操作栏 -->
      <div class="bottom-bar">
        <button class="btn-cart" @click="handleAddCart" :disabled="cartLoading">
          {{ cartLoading ? '处理中...' : '加入购物车' }}
        </button>
        <button class="btn-buy" @click="handleBuy">
          立即购买
        </button>
      </div>
    </template>

    <!-- 商品不存在 -->
    <div v-else class="loading-wrap">
      <p>商品不存在</p>
    </div>

    <!-- Toast -->
    <transition name="fade">
      <div v-if="toast.show" class="toast" :class="toast.type">{{ toast.msg }}</div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductById, addToCart, addOrder } from '../api/shop'
import { userStore } from '../store/user'

const route = useRoute()
const router = useRouter()
const product = ref(null)
const loading = ref(true)
const cartLoading = ref(false)
const currentImageIndex = ref(0)
const toast = reactive({ show: false, msg: '', type: 'success' })

const imageList = computed(() => {
  if (!product.value) return []
  if (product.value.images) {
    return product.value.images.split(',').map(s => s.trim()).filter(Boolean)
  }
  if (product.value.coverImage) return [product.value.coverImage]
  return []
})

const currentImage = computed(() => {
  if (imageList.value.length > 0) return imageList.value[currentImageIndex.value]
  return product.value?.coverImage || ''
})

const tagList = computed(() => {
  if (!product.value?.tags) return []
  return product.value.tags.split(',').map(s => s.trim()).filter(Boolean)
})

function showToast(msg, type = 'success') {
  toast.msg = msg
  toast.type = type
  toast.show = true
  setTimeout(() => { toast.show = false }, 2000)
}

onMounted(async () => {
  const id = route.params.id
  if (!id) {
    loading.value = false
    return
  }
  try {
    const res = await getProductById(id)
    if (res.data) {
      product.value = res.data
    }
  } catch (e) {
    console.error('获取商品详情失败:', e)
  } finally {
    loading.value = false
  }
})

async function handleAddCart() {
  const uid = userStore.user?.userId
  if (!uid) {
    showToast('请先登录', 'error')
    return
  }
  cartLoading.value = true
  try {
    const res = await addToCart(product.value.id, uid)
    if (res.data === 'ok') {
      showToast('已加入购物车')
    } else {
      showToast(res.data || '添加失败', 'error')
    }
  } catch {
    showToast('网络错误', 'error')
  } finally {
    cartLoading.value = false
  }
}

async function handleBuy() {
  const uid = userStore.user?.userId
  if (!uid) { showToast('请先登录', 'error'); return }
  if (!product.value) { showToast('商品信息异常', 'error'); return }

  const p = product.value
  const orderId = Date.now()
  const total = (p.price * 1).toFixed(2)

  try {
    await addOrder({
      userId: uid,
      orderId: orderId,
      productId: p.id,
      productName: p.name,
      productImage: p.coverImage || '',
      price: p.price,
      quantity: 1,
      totalPrice: total,
    })
    router.push(`/pay?orderId=${orderId}&total=${total}`)
  } catch {
    showToast('下单失败', 'error')
  }
}
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: #FBF8F4;
  padding-bottom: 80px;
}

/* 导航 */
.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: rgba(255,255,255,0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  position: sticky;
  top: 0;
  z-index: 10;
}

.back-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.nav-title {
  font-family: var(--font-heading);
  font-size: 17px;
  font-weight: 700;
  color: var(--text-primary);
}

.nav-placeholder { width: 36px; }

.loading-wrap {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 60vh;
  color: var(--text-muted);
  font-size: 15px;
}

/* 图片区 */
.image-area {
  width: 100%;
  aspect-ratio: 1 / 1;
  background: #fff;
  position: relative;
  overflow: hidden;
}

.main-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F5F0E8;
}

.image-dots {
  position: absolute;
  bottom: 12px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(255,255,255,0.5);
  cursor: pointer;
  transition: all 0.2s;
}

.dot.active {
  background: var(--primary);
  width: 20px;
  border-radius: 4px;
}

/* 信息区 */
.info-card {
  background: #fff;
  margin: 12px;
  border-radius: 16px;
  padding: 20px;
}

.product-name {
  font-family: var(--font-heading);
  font-size: 20px;
  font-weight: 800;
  color: var(--text-primary);
  line-height: 1.3;
}

.product-subtitle {
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: 6px;
}

.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 14px;
}

.tag {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 600;
  color: var(--primary);
  background: rgba(255, 122, 51, 0.08);
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-top: 18px;
  padding-top: 16px;
  border-top: 1px solid #F0EDE8;
}

.price {
  font-family: var(--font-heading);
  font-size: 28px;
  font-weight: 800;
  color: var(--primary);
}

.origin-price {
  font-size: 14px;
  color: var(--text-muted);
  text-decoration: line-through;
}

.meta-row {
  display: flex;
  justify-content: space-between;
  margin-top: 12px;
  font-size: 12px;
  color: var(--text-muted);
}

.in-stock { color: #5B9A5B; }
.out-stock { color: #D14B4B; }

/* 底部操作栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  gap: 12px;
  padding: 12px 16px;
  padding-bottom: calc(12px + env(safe-area-inset-bottom, 0px));
  background: rgba(255,255,255,0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  box-shadow: 0 -1px 8px rgba(0,0,0,0.04);
}

.btn-cart {
  flex: 1;
  height: 48px;
  border-radius: 24px;
  border: 2px solid var(--primary);
  background: #fff;
  color: var(--primary);
  font-family: var(--font-heading);
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 2px;
  transition: all 0.2s;
}

.btn-cart:active {
  background: rgba(255, 122, 51, 0.06);
}

.btn-cart:disabled {
  opacity: 0.5;
}

.btn-buy {
  flex: 1;
  height: 48px;
  border-radius: 24px;
  border: none;
  background: var(--gradient-primary);
  color: #fff;
  font-family: var(--font-heading);
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 2px;
  box-shadow: var(--shadow-primary);
  transition: all 0.2s;
}

.btn-buy:active {
  transform: scale(0.96);
}

/* Toast */
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

.fade-enter-active,
.fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from,
.fade-leave-to { opacity: 0; }
</style>
