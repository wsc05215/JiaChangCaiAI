<template>
  <div class="checkout-page">
    <!-- 导航 -->
    <div class="nav-bar">
      <div class="back-btn" @click="$goBack()">
        <svg viewBox="0 0 24 24" width="22" height="22">
          <path d="M15 18l-6-6 6-6" stroke="#5a524c" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <span class="nav-title">确认订单</span>
      <div class="nav-placeholder"></div>
    </div>

    <!-- 收货地址 -->
    <div class="section">
      <div class="section-header">
        <span class="section-title">收货地址</span>
        <button class="add-addr-btn" @click="openAddrModal">+ 新增地址</button>
      </div>

      <div v-if="addressList.length > 0" class="address-list">
        <div
          v-for="addr in addressList" :key="addr.addressId"
          class="address-card" :class="{ selected: selectedAddrId === addr.addressId }"
          @click="selectedAddrId = addr.addressId"
        >
          <div class="addr-top">
            <span class="addr-receiver">{{ addr.receiver }}</span>
            <span class="addr-phone">{{ addr.phone }}</span>
            <span v-if="addr.isDefault === 1" class="default-tag">默认</span>
          </div>
          <div class="addr-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }} {{ addr.detail }}</div>
        </div>
      </div>
      <div v-else class="no-addr">暂未添加收货地址</div>
    </div>

    <!-- 商品列表 -->
    <div class="section">
      <div class="section-title">商品信息</div>
      <div v-for="item in cartItems" :key="item.cartId" class="goods-item">
        <img v-if="item.productImage" :src="item.productImage" class="goods-img" />
        <div v-else class="goods-img-placeholder"></div>
        <div class="goods-info">
          <div class="goods-name">{{ item.productName }}</div>
          <div class="goods-price">¥{{ item.productPrice }}</div>
        </div>
        <span class="goods-qty">×{{ item.quantity }}</span>
      </div>
    </div>

    <!-- 合计 -->
    <div class="total-bar">
      <span>合计：</span>
      <span class="total-price">¥{{ totalPrice }}</span>
    </div>

    <!-- 提交按钮 -->
    <div class="bottom-bar">
      <button class="submit-btn" @click="handleSubmit">提交订单</button>
    </div>

    <!-- 新增地址弹窗 -->
    <transition name="slide-up">
      <div v-if="showAddrModal" class="modal-mask" @click.self="showAddrModal = false">
        <div class="modal-panel">
          <div class="modal-header">
            <span class="modal-title">新增收货地址</span>
            <div class="modal-close" @click="showAddrModal = false">
              <svg viewBox="0 0 24 24" width="20" height="20">
                <path d="M18 6L6 18M6 6l12 12" stroke="#5a524c" stroke-width="2" fill="none" stroke-linecap="round"/>
              </svg>
            </div>
          </div>

          <div class="modal-body">
            <!-- 省市区滚轮选择 -->
            <div class="picker-wrap">
              <RegionPicker
                v-model:province="addrProvince"
                v-model:city="addrCity"
                v-model:district="addrDistrict"
              />
            </div>

            <!-- 手动输入字段 -->
            <div class="form-group">
              <input v-model="form.receiver" placeholder="收货人姓名" class="form-input" />
            </div>
            <div class="form-group">
              <input v-model="form.phone" placeholder="手机号码" class="form-input" />
            </div>
            <div class="form-group">
              <input v-model="form.detail" placeholder="详细地址（街道/门牌号）" class="form-input" />
            </div>
          </div>

          <div class="modal-footer">
            <button class="save-btn" @click="submitAddress" :disabled="addrLoading">
              {{ addrLoading ? '保存中...' : '保存' }}
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
import { ref, computed, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCart, addAddress, getAddressList, addOrder } from '../api/shop'
import { userStore } from '../store/user'
import RegionPicker from '../components/RegionPicker.vue'

const router = useRouter()
const cartItems = ref([])
const addressList = ref([])
const selectedAddrId = ref(null)
const addrLoading = ref(false)
const showAddrModal = ref(false)
const toast = reactive({ show: false, msg: '', type: 'success' })

const addrProvince = ref('')
const addrCity = ref('')
const addrDistrict = ref('')

const form = reactive({
  userId: userStore.user?.userId,
  receiver: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
})

const totalPrice = computed(() => {
  return cartItems.value.reduce((sum, i) => sum + (i.productPrice || 0) * i.quantity, 0).toFixed(2)
})

function showToast(msg, type = 'success') {
  toast.msg = msg
  toast.type = type
  toast.show = true
  setTimeout(() => { toast.show = false }, 2000)
}

function openAddrModal() {
  form.receiver = ''
  form.phone = ''
  form.detail = ''
  addrProvince.value = ''
  addrCity.value = ''
  addrDistrict.value = ''
  showAddrModal.value = true
}

onMounted(async () => {
  const uid = userStore.user?.userId
  if (!uid) return
  try {
    const [cartRes, addrRes] = await Promise.all([getCart(uid), getAddressList(uid)])
    cartItems.value = (cartRes.data || []).filter(i => i.selected === 1)
    addressList.value = addrRes.data || []
    const def = addressList.value.find(a => a.isDefault === 1)
    if (def) selectedAddrId.value = def.addressId
  } catch { /* ignore */ }
})

async function submitAddress() {
  if (!addrProvince.value) return showToast('请选择省份', 'error')
  if (!addrCity.value) return showToast('请选择城市', 'error')
  if (!form.receiver.trim()) return showToast('请填写收货人', 'error')
  if (!form.phone.trim()) return showToast('请填写手机号', 'error')
  if (!form.detail.trim()) return showToast('请填写详细地址', 'error')

  form.province = addrProvince.value
  form.city = addrCity.value
  form.district = addrDistrict.value

  addrLoading.value = true
  try {
    const res = await addAddress(form)
    if (res.data === 'ok') {
      showToast('地址保存成功')
      showAddrModal.value = false
      const uid = userStore.user?.userId
      const addrRes = await getAddressList(uid)
      addressList.value = addrRes.data || []
      if (addressList.value.length > 0) {
        selectedAddrId.value = addressList.value[addressList.value.length - 1].addressId
      }
    }
  } catch { showToast('保存失败', 'error') }
  finally { addrLoading.value = false }
}

async function handleSubmit() {
  if (cartItems.value.length === 0) return showToast('没有待结算商品', 'error')
  if (!selectedAddrId.value) return showToast('请选择收货地址', 'error')

  const uid = userStore.user?.userId
  const orderId = Date.now()
  const items = cartItems.value

  try {
    for (const item of items) {
      await addOrder({
        userId: uid,
        orderId: orderId,
        productId: item.productId,
        productName: item.productName,
        productImage: item.productImage || '',
        price: item.productPrice,
        quantity: item.quantity,
        totalPrice: (item.productPrice * item.quantity).toFixed(2),
      })
    }
    router.push(`/pay?orderId=${orderId}&total=${totalPrice.value}`)
  } catch {
    showToast('下单失败', 'error')
  }
}
</script>

<style scoped>
.checkout-page {
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

.section { margin: 12px 16px; }

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.section-title {
  font-family: var(--font-heading);
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
}

.add-addr-btn {
  font-size: 13px;
  font-weight: 600;
  color: var(--primary);
  padding: 6px 14px;
  border-radius: 16px;
  background: rgba(255,122,51,0.08);
  letter-spacing: 1px;
  transition: all 0.2s;
}

.add-addr-btn:active { background: rgba(255,122,51,0.16); }

.no-addr {
  text-align: center;
  padding: 24px;
  color: var(--text-muted);
  font-size: 14px;
}

/* 地址卡片 */
.address-list { display: flex; flex-direction: column; gap: 8px; }

.address-card {
  background: #fff;
  border-radius: 12px;
  padding: 14px;
  border: 1.5px solid transparent;
  cursor: pointer;
  transition: border-color 0.2s;
}

.address-card.selected { border-color: var(--primary); }

.addr-top { display: flex; align-items: center; gap: 8px; margin-bottom: 6px; }

.addr-receiver { font-weight: 700; font-size: 15px; color: var(--text-primary); }
.addr-phone { font-size: 13px; color: var(--text-secondary); }

.default-tag {
  background: rgba(255, 122, 51, 0.1);
  color: var(--primary);
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 10px;
}

.addr-detail { font-size: 13px; color: var(--text-secondary); }

/* 商品列表 */
.goods-item {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #fff;
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 8px;
}

.goods-img { width: 56px; height: 56px; border-radius: 8px; object-fit: cover; background: #F5F0E8; }
.goods-img-placeholder { width: 56px; height: 56px; border-radius: 8px; background: #F5F0E8; flex-shrink: 0; }

.goods-info { flex: 1; min-width: 0; }
.goods-name { font-weight: 600; font-size: 14px; color: var(--text-primary); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.goods-price { font-family: var(--font-heading); font-size: 15px; font-weight: 700; color: var(--primary); margin-top: 4px; }

.goods-qty { font-size: 14px; font-weight: 600; color: var(--text-secondary); flex-shrink: 0; }

/* 合计 */
.total-bar {
  display: flex;
  align-items: baseline;
  justify-content: flex-end;
  padding: 12px 20px;
  font-size: 15px;
  font-weight: 600;
}

.total-price {
  font-family: var(--font-heading);
  font-size: 22px;
  font-weight: 800;
  color: var(--primary);
}

/* 底部 */
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

.submit-btn {
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

.submit-btn:active { transform: scale(0.96); }

/* 弹窗 */
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

.picker-wrap {
  margin-bottom: 12px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}

.form-group {
  margin-bottom: 10px;
}

.form-input {
  width: 100%;
  height: 48px;
  border: 1.5px solid var(--border);
  border-radius: 12px;
  padding: 0 14px;
  font-size: 15px;
  background: #fff;
  box-sizing: border-box;
}

.form-input:focus { border-color: var(--primary); outline: none; }

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
