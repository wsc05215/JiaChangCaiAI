<template>
  <div class="detail-page">
    <!-- 导航 -->
    <div class="nav-bar">
      <div class="back-btn" @click="$goBack()">
        <svg viewBox="0 0 24 24" width="22" height="22">
          <path d="M15 18l-6-6 6-6" stroke="#5a524c" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <span class="nav-title">订单详情</span>
      <div class="nav-placeholder"></div>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="loading-wrap">
      <p>加载中...</p>
    </div>

    <!-- 订单详情 -->
    <template v-else-if="order">
      <!-- 发货地址 -->
      <div class="delivery-card" v-if="order.deliveryAddress">
        <div class="delivery-header">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="none">
            <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7z" stroke="#FF6B35" stroke-width="1.8"/>
            <circle cx="12" cy="9" r="2.5" stroke="#FF6B35" stroke-width="1.8"/>
          </svg>
          <span class="delivery-title">发货地址</span>
        </div>
        <div class="delivery-address">{{ order.deliveryAddress }}</div>
        <div class="delivery-badge">24小时内发货</div>
      </div>

      <!-- 订单信息 -->
      <div class="info-card">
        <div class="info-row">
          <span class="info-label">订单编号</span>
          <span class="info-value mono">{{ order.orderId }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">下单时间</span>
          <span class="info-value">{{ order.createTime }}</span>
        </div>
      </div>

      <!-- 商品列表 -->
      <div class="items-card">
        <div class="section-title">商品信息</div>
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
      </div>

      <!-- 合计 -->
      <div class="total-card">
        <span class="total-label">合计</span>
        <span class="total-price">¥{{ order.total }}</span>
      </div>
    </template>

    <!-- 订单不存在 -->
    <div v-else class="loading-wrap">
      <p>订单不存在</p>
    </div>

    <transition name="fade">
      <div v-if="toast.show" class="toast" :class="toast.type">{{ toast.msg }}</div>
    </transition>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getOrderDetail } from '../api/shop'

const route = useRoute()
const order = ref(null)
const loading = ref(true)
const toast = reactive({ show: false, msg: '', type: 'success' })

onMounted(async () => {
  const orderId = route.params.id
  if (!orderId) {
    loading.value = false
    return
  }
  try {
    const res = await getOrderDetail(orderId)
    if (res.data) {
      order.value = res.data
      if (order.value.total != null) {
        order.value.total = Number(order.value.total).toFixed(2)
      }
    }
  } catch (e) {
    console.error('获取订单详情失败:', e)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: #FBF8F4;
  padding-bottom: 40px;
}

/* 导航 */
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

.loading-wrap {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 60vh;
  color: var(--text-muted);
  font-size: 15px;
}

/* 发货地址卡片 */
.delivery-card {
  margin: 12px 16px;
  background: linear-gradient(135deg, #FFF7F0, #FFF3E8);
  border-radius: 16px;
  padding: 18px 20px;
  border: 1px solid rgba(255, 107, 53, 0.12);
}

.delivery-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 10px;
}

.delivery-title {
  font-size: 13px;
  font-weight: 600;
  color: #FF6B35;
}

.delivery-address {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.5;
  margin-bottom: 10px;
}

.delivery-badge {
  display: inline-block;
  font-size: 11px;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(135deg, #FF6B35, #FF8C5A);
  padding: 4px 12px;
  border-radius: 20px;
  letter-spacing: 0.5px;
}

/* 订单信息卡片 */
.info-card {
  margin: 0 16px 12px;
  background: #fff;
  border-radius: 14px;
  padding: 16px 18px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-row + .info-row {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #F5F0E8;
}

.info-label {
  font-size: 13px;
  color: var(--text-muted);
  font-weight: 500;
}

.info-value {
  font-size: 13px;
  color: var(--text-primary);
  font-weight: 600;
}

.info-value.mono {
  font-family: monospace;
  font-size: 12px;
}

/* 商品列表 */
.items-card {
  margin: 0 16px 12px;
  background: #fff;
  border-radius: 14px;
  padding: 16px 0;
}

.section-title {
  font-family: var(--font-heading);
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
  padding: 0 16px 12px;
  border-bottom: 1px solid #F5F0E8;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-bottom: 1px solid #FAF7F2;
}

.order-item:last-of-type { border-bottom: none; }

.item-img {
  width: 56px;
  height: 56px;
  border-radius: 10px;
  object-fit: cover;
  background: #F5F0E8;
  flex-shrink: 0;
}

.item-img-placeholder {
  width: 56px;
  height: 56px;
  border-radius: 10px;
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

.item-price-qty {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 4px;
}

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

/* 合计卡片 */
.total-card {
  margin: 0 16px;
  background: #fff;
  border-radius: 14px;
  padding: 16px 18px;
  display: flex;
  justify-content: space-between;
  align-items: baseline;
}

.total-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-secondary);
}

.total-price {
  font-family: var(--font-heading);
  font-size: 22px;
  font-weight: 800;
  color: var(--primary);
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

.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
