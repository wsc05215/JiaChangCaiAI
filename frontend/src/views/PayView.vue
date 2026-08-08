<template>
  <div class="pay-page">
    <!-- 导航 -->
    <div class="nav-bar">
      <div class="back-btn" @click="$goBack()">
        <svg viewBox="0 0 24 24" width="22" height="22">
          <path d="M15 18l-6-6 6-6" stroke="#5a524c" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <span class="nav-title">确认支付</span>
      <div class="nav-placeholder"></div>
    </div>

    <div class="pay-content">
      <!-- 订单金额 -->
      <div class="amount-card">
        <div class="amount-label">支付金额</div>
        <div class="amount-value">¥{{ total }}</div>
        <div class="order-info">订单号：{{ orderId }}</div>
      </div>

      <!-- 支付方式 -->
      <div class="section-title">选择支付方式</div>
      <div class="pay-methods">
        <div
          class="pay-card"
          :class="{ selected: payType === 1 }"
          @click="payType = 1"
        >
          <div class="pay-left">
            <svg class="pay-icon" viewBox="0 0 32 32" width="36" height="36">
              <rect x="2" y="6" width="28" height="20" rx="3" fill="#1677FF"/>
              <text x="16" y="21" text-anchor="middle" fill="#fff" font-size="11" font-weight="800">微</text>
            </svg>
            <div>
              <div class="pay-name">微信支付</div>
              <div class="pay-desc">推荐安装微信客户端的用户使用</div>
            </div>
          </div>
          <div class="pay-radio" :class="{ on: payType === 1 }">
            <svg v-if="payType === 1" viewBox="0 0 24 24" width="16" height="16">
              <circle cx="12" cy="12" r="10" fill="var(--primary)"/>
              <path d="M9 12l2 2 4-4" stroke="#fff" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
        </div>

        <div
          class="pay-card"
          :class="{ selected: payType === 2 }"
          @click="payType = 2"
        >
          <div class="pay-left">
            <svg class="pay-icon" viewBox="0 0 32 32" width="36" height="36">
              <rect x="2" y="6" width="28" height="20" rx="3" fill="#108EE9"/>
              <text x="16" y="21" text-anchor="middle" fill="#fff" font-size="10" font-weight="800">支</text>
            </svg>
            <div>
              <div class="pay-name">支付宝</div>
              <div class="pay-desc">推荐有支付宝账号的用户使用</div>
            </div>
          </div>
          <div class="pay-radio" :class="{ on: payType === 2 }">
            <svg v-if="payType === 2" viewBox="0 0 24 24" width="16" height="16">
              <circle cx="12" cy="12" r="10" fill="var(--primary)"/>
              <path d="M9 12l2 2 4-4" stroke="#fff" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- 支付按钮 -->
    <div class="bottom-bar">
      <button class="pay-btn" @click="handlePay" :disabled="paying">
        {{ paying ? '支付中...' : '确认支付' }}
      </button>
    </div>

    <!-- 支付成功弹窗 -->
    <transition name="fade">
      <div v-if="showSuccess" class="success-mask" @click.self="goOrders">
        <div class="success-card">
          <svg viewBox="0 0 24 24" width="56" height="56">
            <circle cx="12" cy="12" r="11" fill="#52c41a"/>
            <path d="M8 12l3 3 5-5" stroke="#fff" stroke-width="2.5" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <div class="success-text">支付成功</div>
          <button class="go-btn" @click="goOrders">查看订单</button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const orderId = ref('')
const total = ref('0.00')
const payType = ref(1)
const paying = ref(false)
const showSuccess = ref(false)

onMounted(() => {
  orderId.value = route.query.orderId || ''
  total.value = route.query.total || '0.00'
})

function handlePay() {
  paying.value = true
  setTimeout(() => {
    paying.value = false
    showSuccess.value = true
  }, 1200)
}

function goOrders() {
  router.push('/orders')
}
</script>

<style scoped>
.pay-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: #FBF8F4;
  padding-top: max(env(safe-area-inset-top), 0px);
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

.pay-content { padding: 16px; }

.amount-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  text-align: center;
  margin-bottom: 20px;
}

.amount-label { font-size: 14px; color: var(--text-secondary); margin-bottom: 8px; }

.amount-value {
  font-family: var(--font-heading);
  font-size: 36px;
  font-weight: 900;
  color: var(--primary);
}

.order-info {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 10px;
}

.section-title {
  font-family: var(--font-heading);
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 10px;
}

.pay-methods { display: flex; flex-direction: column; gap: 10px; }

.pay-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-radius: 14px;
  padding: 16px;
  border: 1.5px solid transparent;
  cursor: pointer;
  transition: border-color 0.2s;
}

.pay-card.selected { border-color: var(--primary); }

.pay-left { display: flex; align-items: center; gap: 12px; }

.pay-icon { flex-shrink: 0; }

.pay-name { font-size: 15px; font-weight: 700; color: var(--text-primary); }

.pay-desc {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 3px;
}

.pay-radio {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  border: 2px solid var(--border);
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pay-radio.on { border-color: var(--primary); }

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 20px;
  padding-bottom: calc(12px + env(safe-area-inset-bottom, 0px));
  background: rgba(255,255,255,0.95);
  backdrop-filter: blur(20px);
  box-shadow: 0 -1px 8px rgba(0,0,0,0.04);
}

.pay-btn {
  width: 100%;
  height: 48px;
  border-radius: 24px;
  background: var(--gradient-primary);
  color: #fff;
  font-family: var(--font-heading);
  font-size: 18px;
  font-weight: 800;
  letter-spacing: 4px;
  box-shadow: var(--shadow-primary);
  transition: all 0.2s;
}

.pay-btn:active { transform: scale(0.96); }
.pay-btn:disabled { opacity: 0.5; }

/* 支付成功 */
.success-mask {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.4);
  z-index: 200;
  display: flex;
  align-items: center;
  justify-content: center;
}

.success-card {
  background: #fff;
  border-radius: 20px;
  padding: 36px 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.success-text {
  font-family: var(--font-heading);
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
}

.go-btn {
  margin-top: 8px;
  padding: 10px 36px;
  border-radius: 22px;
  background: var(--gradient-primary);
  color: #fff;
  font-family: var(--font-heading);
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 2px;
  box-shadow: var(--shadow-primary);
  transition: all 0.2s;
}

.go-btn:active { transform: scale(0.95); }

.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
