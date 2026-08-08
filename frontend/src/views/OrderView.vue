<template>
  <div class="order-page">
    <!-- 导航 -->
    <div class="nav-bar">
      <div class="back-btn" @click="$goBack()">
        <svg viewBox="0 0 24 24" width="22" height="22">
          <path d="M15 18l-6-6 6-6" stroke="#5a524c" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <span class="nav-title">订单管理</span>
      <div class="nav-placeholder"></div>
    </div>

    <!-- 标签切换 -->
    <div class="tabs">
      <div
        class="tab"
        :class="{ active: activeTab === 'all' }"
        @click="activeTab = 'all'; loadOrders()"
      >全部订单</div>
      <div
        class="tab"
        :class="{ active: activeTab === 'return' }"
        @click="activeTab = 'return'; loadReturnOrders()"
      >退货记录</div>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="loading-wrap"><p>加载中...</p></div>

    <!-- 全部订单 -->
    <template v-if="activeTab === 'all'">
      <div v-if="!loading && orders.length === 0" class="empty-wrap">
        <svg viewBox="0 0 24 24" width="56" height="56">
          <rect x="3" y="4" width="18" height="18" rx="2" stroke="#d5cfc7" stroke-width="1.5" fill="none"/>
          <path d="M3 10h18M8 2v4M16 2v4" stroke="#d5cfc7" stroke-width="1.5" fill="none" stroke-linecap="round"/>
        </svg>
        <p class="empty-text">暂无订单</p>
      </div>

      <div v-else class="order-list">
        <div v-for="order in orders" :key="order.orderId" class="order-group" @click="goOrderDetail(order.orderId)">
          <!-- 订单头 -->
          <div class="order-header">
            <span class="order-no">订单号：{{ order.orderId }}</span>
            <span class="order-time">{{ order.createTime }}</span>
          </div>

          <!-- 商品列表 -->
          <div v-for="item in order.items" :key="item.itemId">
            <div class="order-item">
              <img v-if="item.productImage" :src="item.productImage" class="item-img" />
              <div v-else class="item-img-placeholder"></div>
              <div class="item-info">
                <div class="item-name">{{ item.productName }}</div>
                <div class="item-price-qty">
                  <span class="item-price">¥{{ item.price }}</span>
                  <span class="item-qty">×{{ item.quantity }}</span>
                </div>
                <!-- 退货状态标签 -->
                <div v-if="item.returnStatus === 1" class="status-tag status-returning">退货中</div>
                <div v-else-if="item.returnStatus === 2" class="status-tag status-returned">已退货</div>
                <div v-else-if="item.receivedTime" class="status-tag status-received">已收货</div>
              </div>
              <div class="item-total">¥{{ item.totalPrice }}</div>
            </div>
            <!-- 操作按钮（每个商品独立操作） -->
            <div class="order-actions">
              <button
                v-if="!item.receivedTime && item.returnStatus !== 2"
                class="action-btn primary"
                @click.stop="handleConfirmReceive(item)"
              >确认收货</button>
              <button
                v-if="item.receivedTime && item.returnStatus === 0 && getHoursSince(item.receivedTime) < 24"
                class="action-btn danger"
                @click.stop="openReturnModal(item)"
              >申请退货</button>
              <button
                v-if="item.returnStatus === 1"
                class="action-btn"
                @click.stop="handleCancelReturn(item)"
              >取消退货</button>
              <span
                v-if="item.receivedTime && item.returnStatus === 0 && getHoursSince(item.receivedTime) < 24"
                class="timeout-hint"
              >剩余 {{ getRemainingTime(item.receivedTime) }}</span>
              <span
                v-else-if="item.receivedTime && item.returnStatus === 0 && getHoursSince(item.receivedTime) >= 24"
                class="timeout-hint expired"
              >已过退货期限</span>
            </div>
          </div>

          <!-- 批量退货按钮 -->
          <div class="batch-return-section" v-if="showBatchReturnButton(order)">
            <div class="divider"></div>
            <button
              class="action-btn batch-return-btn"
              @click.stop="openBatchReturnModal(order)"
            >整单退货</button>
          </div>

          <!-- 合计 -->
          <div class="order-footer">
            <span class="order-total-label">合计：</span>
            <span class="order-total">¥{{ order.total }}</span>
          </div>
        </div>
      </div>
    </template>

    <!-- 退货记录 -->
    <template v-if="activeTab === 'return'">
      <div v-if="!loading && returnOrders.length === 0" class="empty-wrap">
        <svg viewBox="0 0 24 24" width="56" height="56">
          <path d="M3 12a9 9 0 119 9" stroke="#d5cfc7" stroke-width="1.5" fill="none" stroke-linecap="round"/>
          <path d="M3 12l3-3M3 12l3 3" stroke="#d5cfc7" stroke-width="1.5" fill="none" stroke-linecap="round"/>
        </svg>
        <p class="empty-text">暂无退货记录</p>
      </div>

      <div v-else class="order-list">
        <div v-for="item in returnOrders" :key="item.itemId" class="order-group">
          <div class="order-header">
            <span class="order-no">订单号：{{ item.orderId }}</span>
            <span class="order-time">{{ item.createTime }}</span>
          </div>
          <div class="order-item">
            <img v-if="item.productImage" :src="item.productImage" class="item-img" />
            <div v-else class="item-img-placeholder"></div>
            <div class="item-info">
              <div class="item-name">{{ item.productName }}</div>
              <div class="item-price-qty">
                <span class="item-price">¥{{ item.price }}</span>
                <span class="item-qty">×{{ item.quantity }}</span>
              </div>
              <div v-if="item.returnStatus === 1" class="status-tag status-returning">退货中</div>
              <div v-else-if="item.returnStatus === 2" class="status-tag status-returned">已退货</div>
            </div>
            <div class="item-total">¥{{ item.totalPrice }}</div>
          </div>
          <div v-if="item.returnReason" class="return-reason">
            <span class="reason-label">退货原因：</span>{{ item.returnReason }}
          </div>
          <div v-if="item.returnStatus === 1" class="order-actions">
            <button class="action-btn" @click="handleCancelReturn(item)">取消退货</button>
          </div>
        </div>
      </div>
    </template>

    <!-- 退货原因弹窗 -->
    <transition name="slide-up">
      <div v-if="showReturnModal" class="modal-mask" @click.self="showReturnModal = false">
        <div class="modal-panel">
          <div class="modal-header">
            <span class="modal-title">申请退货</span>
            <div class="modal-close" @click="showReturnModal = false">
              <svg viewBox="0 0 24 24" width="20" height="20">
                <path d="M18 6L6 18M6 6l12 12" stroke="#5a524c" stroke-width="2" fill="none" stroke-linecap="round"/>
              </svg>
            </div>
          </div>

          <div class="modal-body">
            <div class="return-item-preview">
              <img v-if="returnTarget?.productImage" :src="returnTarget.productImage" class="preview-img" />
              <div class="preview-info">
                <div class="preview-name">{{ returnTarget?.productName }}</div>
                <div class="preview-price">¥{{ returnTarget?.price }} × {{ returnTarget?.quantity }}</div>
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">退货原因</label>
              <textarea
                v-model="returnReason"
                class="form-textarea"
                placeholder="请填写退货原因（必填）"
                rows="4"
              ></textarea>
            </div>

            <div class="timeout-notice" v-if="returnTarget?.receivedTime">
              订单已收货，剩余 <strong>{{ getRemainingTime(returnTarget.receivedTime) }}</strong> 可申请退货
            </div>
          </div>

          <div class="modal-footer">
            <button class="save-btn" @click="submitReturn" :disabled="returnLoading">
              {{ returnLoading ? '提交中...' : '提交退货申请' }}
            </button>
          </div>
        </div>
      </div>
    </transition>

    <!-- 批量退货原因弹窗 -->
    <transition name="slide-up">
      <div v-if="showBatchReturnModal" class="modal-mask" @click.self="showBatchReturnModal = false">
        <div class="modal-panel">
          <div class="modal-header">
            <span class="modal-title">整单退货申请</span>
            <div class="modal-close" @click="showBatchReturnModal = false">
              <svg viewBox="0 0 24 24" width="20" height="20">
                <path d="M18 6L6 18M6 6l12 12" stroke="#5a524c" stroke-width="2" fill="none" stroke-linecap="round"/>
              </svg>
            </div>
          </div>

          <div class="modal-body">
            <div class="return-order-summary">
              <div class="order-info">
                <div class="order-summary">订单号：{{ batchReturnOrder?.orderId }}</div>
                <div class="items-count">商品数量：{{ batchReturnOrder?.items.length }}件</div>
              </div>
            </div>

            <div class="batch-items-preview">
              <div class="batch-item" v-for="item in batchReturnOrder?.items" :key="item.itemId">
                <img v-if="item.productImage" :src="item.productImage" class="batch-preview-img" />
                <div class="batch-preview-info">
                  <div class="batch-preview-name">{{ item.productName }}</div>
                  <div class="batch-preview-price">¥{{ item.price }} × {{ item.quantity }}</div>
                  <div v-if="item.returnStatus === 1" class="status-tag status-returning">退货中</div>
                  <div v-else-if="item.returnStatus === 2" class="status-tag status-returned">已退货</div>
                  <div v-else-if="item.receivedTime" class="status-tag status-received">已收货</div>
                </div>
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">退货原因</label>
              <textarea
                v-model="batchReturnReason"
                class="form-textarea"
                placeholder="请填写退货原因（必填）"
                rows="4"
              ></textarea>
            </div>

            <div class="timeout-notice" v-if="batchReturnOrder?.items.length > 0">
              整单包含 {{ batchReturnOrder.items.filter(item => item.receivedTime && item.returnStatus === 0 && getHoursSince(item.receivedTime) < 24).length }} 件商品可以退货
            </div>
          </div>

          <div class="modal-footer">
            <button class="save-btn" @click="submitBatchReturn" :disabled="batchReturnLoading">
              {{ batchReturnLoading ? '提交中...' : '提交整单退货申请' }}
            </button>
          </div>
        </div>
      </div>
    </transition>

    <transition name="fade">
      <div v-if="toast.show" class="toast" :class="toast.type">{{ toast.msg }}</div>
    </transition>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { userStore } from '../store/user'
import { getOrderItems, confirmReceive, requestReturn, cancelReturn, getReturnOrders, requestBatchReturn } from '../api/shop'

const router = useRouter()

const orders = ref([])
const returnOrders = ref([])
const loading = ref(true)
const activeTab = ref('all')
const toast = reactive({ show: false, msg: '', type: 'success' })

// 退货弹窗
const showReturnModal = ref(false)
const returnTarget = ref(null)
const returnReason = ref('')
const returnLoading = ref(false)

// 批量退货相关
const showBatchReturnModal = ref(false)
const batchReturnOrder = ref(null)
const batchReturnReason = ref('')
const batchReturnLoading = ref(false)

// 定时器，用于刷新倒计时
let timer = null
const now = ref(Date.now())

onMounted(() => {
  loadOrders()
  // 每分钟更新当前时间戳，触发倒计时重新渲染
  timer = setInterval(() => {
    now.value = Date.now()
  }, 60000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

function showToast(msg, type = 'success') {
  toast.msg = msg
  toast.type = type
  toast.show = true
  setTimeout(() => { toast.show = false }, 2000)
}

function showBatchReturnButton(order) {
  // 判断订单中是否有商品满足单个商品退货条件，但不是全部在退货状态
  return order.items.some(item =>
    item.receivedTime &&
    item.returnStatus === 0 &&
    getHoursSince(item.receivedTime) < 24
  ) && order.items.length > 1;
}

async function loadOrders() {
  const uid = userStore.user?.userId
  if (!uid) { loading.value = false; return }
  loading.value = true
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
    showToast('加载失败', 'error')
  } finally {
    loading.value = false
  }
}

async function loadReturnOrders() {
  const uid = userStore.user?.userId
  if (!uid) { loading.value = false; return }
  loading.value = true
  try {
    const res = await getReturnOrders(uid)
    returnOrders.value = res.data || []
  } catch {
    showToast('加载失败', 'error')
  } finally {
    loading.value = false
  }
}

// 计算收货后的小时数（使用响应式时间戳触发重新计算）
function getHoursSince(receivedTime) {
  if (!receivedTime) return 99
  const received = new Date(receivedTime)
  return (now.value - received) / (1000 * 60 * 60)
}

// 计算剩余可退货时间
function getRemainingTime(receivedTime) {
  if (!receivedTime) return ''
  const received = new Date(receivedTime)
  const remainingMs = 24 * 60 * 60 * 1000 - (now.value - received)
  if (remainingMs <= 0) return '已过期'
  const hours = Math.floor(remainingMs / (1000 * 60 * 60))
  const minutes = Math.floor((remainingMs % (1000 * 60 * 60)) / (1000 * 60))
  return `${hours}小时${minutes}分钟`
}

async function handleConfirmReceive(item) {
  try {
    const res = await confirmReceive(item.itemId)
    if (res.data === '确认收货成功') {
      showToast('确认收货成功')
      await loadOrders()
    }
  } catch (e) {
    showToast(e.response?.data?.message || e.response?.data || '操作失败', 'error')
  }
}

function openReturnModal(item) {
  returnTarget.value = item
  returnReason.value = ''
  showReturnModal.value = true
}

async function submitReturn() {
  if (!returnReason.value.trim()) {
    return showToast('请填写退货原因', 'error')
  }
  returnLoading.value = true
  try {
    const res = await requestReturn(returnTarget.value.itemId, returnReason.value.trim())
    if (res.data === '退货申请已提交') {
      showToast('退货申请已提交')
      showReturnModal.value = false
      await loadOrders()
    }
  } catch (e) {
    showToast(e.response?.data?.message || e.response?.data || '操作失败', 'error')
  } finally {
    returnLoading.value = false
  }
}

async function handleCancelReturn(item) {
  try {
    const res = await cancelReturn(item.itemId)
    if (res.data === '退货申请已取消') {
      showToast('退货申请已取消')
      await loadOrders()
      if (activeTab.value === 'return') {
        await loadReturnOrders()
      }
    }
  } catch (e) {
    showToast(e.response?.data?.message || e.response?.data || '操作失败', 'error')
  }
}

function goOrderDetail(orderId) {
  router.push('/order/' + orderId)
}

function openBatchReturnModal(order) {
  batchReturnOrder.value = order
  batchReturnReason.value = ''
  showBatchReturnModal.value = true
}

async function submitBatchReturn() {
  if (!batchReturnReason.value.trim()) {
    return showToast('请填写退货原因', 'error')
  }

  batchReturnLoading.value = true
  try {
    // 获取所有符合条件的itemIds
    const itemIds = batchReturnOrder.value.items.filter(item =>
      item.receivedTime &&
      item.returnStatus === 0 &&
      getHoursSince(item.receivedTime) < 24
    ).map(item => item.itemId)

    if (itemIds.length === 0) {
      showToast('没有符合条件的商品可退货', 'error')
      return
    }

    const res = await requestBatchReturn(itemIds, batchReturnReason.value.trim())
    if (res.data === '批量退货申请已提交') {
      showToast('批量退货申请已提交')
      showBatchReturnModal.value = false
      await loadOrders()
    }
  } catch (e) {
    showToast(e.response?.data?.message || e.response?.data || '操作失败', 'error')
  } finally {
    batchReturnLoading.value = false
  }
}
</script>

<style scoped>
.order-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: #FBF8F4;
  padding-top: max(env(safe-area-inset-top), 0px);
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

/* 标签切换 */
.tabs {
  display: flex;
  background: #fff;
  margin: 0 16px;
  border-radius: 12px;
  overflow: hidden;
  margin-top: 12px;
}

.tab {
  flex: 1;
  text-align: center;
  padding: 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-muted);
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.tab.active {
  color: var(--primary);
  background: rgba(255,122,51,0.06);
}

.tab.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 3px;
  border-radius: 2px;
  background: var(--primary);
}

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
  cursor: pointer;
  transition: all 0.2s;
}

.order-group:active { transform: scale(0.98); }

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

/* 状态标签 */
.status-tag {
  display: inline-block;
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 10px;
  margin-top: 4px;
}

.status-received {
  background: #e8f5e9;
  color: #2e7d32;
}

.status-returning {
  background: #fff3e0;
  color: #e65100;
}

.status-returned {
  background: #f3e5f5;
  color: #6a1b9a;
}

/* 操作按钮 */
.order-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 14px;
  border-top: 1px solid #f5f0ea;
  flex-wrap: wrap;
}

.action-btn {
  padding: 6px 16px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
  border: 1.5px solid var(--border);
  color: var(--text-secondary);
  background: #fff;
  transition: all 0.2s;
  cursor: pointer;
}

.action-btn:active { transform: scale(0.95); }

.action-btn.primary {
  background: var(--gradient-primary);
  color: #fff;
  border: none;
  box-shadow: var(--shadow-primary);
}

.action-btn.danger {
  border-color: #ff4d4f;
  color: #ff4d4f;
}

.timeout-hint {
  font-size: 11px;
  color: var(--text-muted);
  margin-left: auto;
}

.timeout-hint.expired {
  color: #ff4d4f;
}

/* 退货原因展示 */
.return-reason {
  padding: 10px 14px;
  font-size: 12px;
  color: var(--text-secondary);
  background: #faf7f2;
  border-top: 1px solid #f5f0ea;
}

.reason-label {
  font-weight: 600;
  color: var(--text-primary);
}

.batch-return-section {
  padding: 10px 14px;
  border-top: 1px solid #f5f0ea;
  text-align: right;
}

.divider {
  height: 1px;
  background: #f5f0ea;
  margin: 10px 0;
}

.batch-return-btn {
  padding: 8px 16px;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 600;
  border: 1.5px solid #ff4d4f;
  color: #ff4d4f;
  background: #fff;
  transition: all 0.2s;
  cursor: pointer;
}

.batch-return-btn:active {
  transform: scale(0.95);
}

/* 批量退货预览 */
.return-order-summary {
  background: #fff;
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 12px;
}

.order-summary {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.items-count {
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: 4px;
}

.batch-items-preview {
  max-height: 200px;
  overflow-y: auto;
  margin-bottom: 16px;
}

.batch-item {
  display: flex;
  align-items: center;
  gap: 10px;
  background: #faf7f2;
  border-radius: 10px;
  padding: 10px;
  margin-bottom: 8px;
}

.batch-preview-img {
  width: 40px;
  height: 40px;
  border-radius: 6px;
  object-fit: cover;
  background: #F5F0E8;
  flex-shrink: 0;
}

.batch-preview-info {
  flex: 1;
  min-width: 0;
}

.batch-preview-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
}

.batch-preview-price {
  font-size: 12px;
  font-weight: 700;
  color: var(--text-secondary);
  margin-top: 2px;
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

/* 退货弹窗 */
.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.4);
  z-index: 100;
  display: flex;
  align-items: flex-end;
}

.modal-panel {
  width: 100%;
  max-height: 85vh;
  background: #FBF8F4;
  border-radius: 20px 20px 0 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px 12px;
  flex-shrink: 0;
}

.modal-title {
  font-family: var(--font-heading);
  font-size: 17px;
  font-weight: 700;
  color: var(--text-primary);
}

.modal-close {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(0,0,0,0.04);
  cursor: pointer;
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 0 16px;
}

/* 退货商品预览 */
.return-item-preview {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #fff;
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 16px;
}

.preview-img {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  object-fit: cover;
  background: #F5F0E8;
  flex-shrink: 0;
}

.preview-info {
  flex: 1;
  min-width: 0;
}

.preview-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.preview-price {
  font-size: 13px;
  font-weight: 700;
  color: var(--text-secondary);
  margin-top: 4px;
}

.form-group {
  margin-bottom: 16px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.form-textarea {
  width: 100%;
  border: 1.5px solid var(--border);
  border-radius: 12px;
  padding: 12px;
  font-size: 14px;
  background: #fff;
  box-sizing: border-box;
  resize: vertical;
  font-family: inherit;
  line-height: 1.5;
}

.form-textarea:focus { border-color: var(--primary); outline: none; }

.timeout-notice {
  font-size: 12px;
  color: var(--text-muted);
  text-align: center;
  padding: 8px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 8px;
}

.timeout-notice strong {
  color: var(--primary);
  font-weight: 700;
}

.modal-footer {
  flex-shrink: 0;
  padding: 12px 20px;
  padding-bottom: calc(12px + env(safe-area-inset-bottom, 0px));
}

.save-btn {
  width: 100%;
  height: 48px;
  border-radius: 24px;
  background: var(--gradient-primary);
  color: #fff;
  font-family: var(--font-heading);
  font-size: 17px;
  font-weight: 700;
  letter-spacing: 2px;
  box-shadow: var(--shadow-primary);
  transition: all 0.2s;
  cursor: pointer;
  border: none;
}

.save-btn:active { transform: scale(0.96); }
.save-btn:disabled { opacity: 0.5; }

/* 动画 */
.slide-up-enter-active { transition: all 0.3s ease-out; }
.slide-up-leave-active { transition: all 0.25s ease-in; }
.slide-up-enter-from .modal-panel { transform: translateY(100%); }
.slide-up-leave-to .modal-panel { transform: translateY(100%); }
.slide-up-enter-from { background: transparent; }
.slide-up-leave-to { background: transparent; }

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