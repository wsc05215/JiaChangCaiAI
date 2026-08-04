<template>
  <div class="order-page">
    <!-- 导航 -->
    <div class="nav-bar">
      <div class="back-btn" @click="$router.back()">
        <svg viewBox="0 0 24 24" width="22" height="22">
          <path d="M15 18l-6-6 6-6" stroke="#5a524c" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <span class="nav-title">订单管理</span>
      <div class="nav-placeholder"></div>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="loading-wrap"><p>加载中...</p></div>

    <!-- 空订单 -->
    <div v-else-if="orders.length === 0" class="empty-wrap">
      <svg viewBox="0 0 24 24" width="56" height="56">
        <rect x="3" y="4" width="18" height="18" rx="2" stroke="#d5cfc7" stroke-width="1.5" fill="none"/>
        <path d="M3 10h18M8 2v4M16 2v4" stroke="#d5cfc7" stroke-width="1.5" fill="none" stroke-linecap="round"/>
      </svg>
      <p class="empty-text">暂无订单</p>
    </div>

    <!-- 订单列表 -->
    <div v-else class="order-list">
      <div v-for="order in orders" :key="order.orderId" class="order-group">
        <!-- 订单头 -->
        <div class="order-header">
          <span class="order-no">订单号：{{ order.orderId }}</span>
          <span class="order-time">{{ order.createTime }}</span>
        </div>

        <!-- 商品列表 -->
        <div v-for="item in order.items" :key="item.itemId" class="order-item">
          <img v-if="item.productImage" :src="item.productImage" class="item-img" />
          <div v-else class="item-img-placeholder"></div>
          <div class="item-info">
            <div class="item-name">{{ item.productName }}</div>
            <div class="item-price-qty">
              <span class="item-price">¥{{ item.price }}</span>
              <span class="item-qty">×{{ item.quantity }}</span>
            </div>
          </div>
          <div class="item-total">¥{{ item.totalPrice }}</div>
        </div>

        <!-- 合计 -->
        <div class="order-footer">
          <span class="order-total-label">合计：</span>
          <span class="order-total">¥{{ order.total }}</span>
        </div>
      </div>
    </div>

    <transition name="fade">
      <div v-if="toast.show" class="toast" :class="toast.type">{{ toast.msg }}</div>
    </transition>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { userStore } from '../store/user'
import { getOrderItems } from '../api/shop'

const orders = ref([])
const loading = ref(true)
const toast = reactive({ show: false, msg: '', type: 'success' })

onMounted(async () => {
  const uid = userStore.user?.userId
  if (!uid) { loading.value = false; return }
  try {
    const res = await getOrderItems(uid)
    const items = res.data || []
    // 按 orderId 分组
    const map = new Map()
    for (const item of items) {
      const oid = item.orderId
      if (!map.has(oid)) {
        map.set(oid, {
          orderId: oid,
          createTime: item.createTime || '',
          items: [],
          total: 0,
        })
      }
      const group = map.get(oid)
      group.items.push(item)
      group.total += parseFloat(item.totalPrice || 0)
    }
    // 按时间倒序排列
    orders.value = Array.from(map.values()).sort((a, b) => b.orderId - a.orderId)
    // 格式化金额
    for (const o of orders.value) {
      o.total = o.total.toFixed(2)
    }
  } catch {
    toast.msg = '加载失败'
    toast.type = 'error'
    toast.show = true
    setTimeout(() => { toast.show = false }, 2000)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.order-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: #FBF8F4;
  padding-bottom: 40px;
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
  height: 50vh;
  gap: 12px;
}

.empty-text { font-size: 15px; color: var(--text-muted); }

.order-list { padding: 12px 16px; display: flex; flex-direction: column; gap: 12px; }

.order-group {
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 14px;
  border-bottom: 1px solid #f5f0ea;
}

.order-no { font-size: 13px; font-weight: 600; color: var(--text-primary); }
.order-time { font-size: 12px; color: var(--text-muted); }

.order-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 14px;
  border-bottom: 1px solid #faf7f2;
}

.order-item:last-of-type { border-bottom: none; }

.item-img {
  width: 52px;
  height: 52px;
  border-radius: 8px;
  object-fit: cover;
  background: #F5F0E8;
  flex-shrink: 0;
}

.item-img-placeholder {
  width: 52px;
  height: 52px;
  border-radius: 8px;
  background: #F5F0E8;
  flex-shrink: 0;
}

.item-info { flex: 1; min-width: 0; }

.item-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-price-qty { display: flex; align-items: center; gap: 6px; margin-top: 4px; }

.item-price {
  font-family: var(--font-heading);
  font-size: 13px;
  font-weight: 700;
  color: var(--text-secondary);
}

.item-qty { font-size: 12px; color: var(--text-muted); }

.item-total {
  font-family: var(--font-heading);
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
  flex-shrink: 0;
}

.order-footer {
  display: flex;
  align-items: baseline;
  justify-content: flex-end;
  padding: 10px 14px 14px;
}

.order-total-label { font-size: 13px; font-weight: 600; color: var(--text-secondary); }

.order-total {
  font-family: var(--font-heading);
  font-size: 18px;
  font-weight: 800;
  color: var(--primary);
}

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
