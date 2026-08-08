<template>
  <div class="addr-page">
    <!-- 导航 -->
    <div class="nav-bar">
      <div class="back-btn" @click="$goBack()">
        <svg viewBox="0 0 24 24" width="22" height="22">
          <path d="M15 18l-6-6 6-6" stroke="#5a524c" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <span class="nav-title">收货地址</span>
      <div class="nav-placeholder"></div>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="loading-wrap"><p>加载中...</p></div>

    <!-- 地址列表 -->
    <template v-else>
      <div v-if="addressList.length > 0" class="addr-list">
        <div v-for="addr in addressList" :key="addr.addressId" class="addr-card">
          <div class="card-body" @click="selectAddr(addr)">
            <div class="addr-top">
              <span class="addr-receiver">{{ addr.receiver }}</span>
              <span class="addr-phone">{{ addr.phone }}</span>
              <span v-if="addr.isDefault === 1" class="default-tag">默认</span>
            </div>
            <div class="addr-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }} {{ addr.detail }}</div>
          </div>
          <div class="card-actions">
            <button class="del-btn" @click="confirmDelete(addr)">
              <svg viewBox="0 0 24 24" width="18" height="18">
                <path d="M3 6h18M8 6V4a2 2 0 012-2h4a2 2 0 012 2v2m3 0v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6h14" stroke="#c44" stroke-width="1.5" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-wrap">
        <svg viewBox="0 0 24 24" width="56" height="56">
          <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7z" stroke="#d5cfc7" stroke-width="1.5" fill="none"/>
          <circle cx="12" cy="9" r="2.5" stroke="#d5cfc7" stroke-width="1.5" fill="none"/>
        </svg>
        <p class="empty-text">暂无收货地址</p>
      </div>
    </template>

    <!-- 底部添加按钮 -->
    <div class="bottom-bar">
      <button class="add-btn" @click="openAddModal">新增收货地址</button>
    </div>

    <!-- 新增地址弹窗 -->
    <transition name="slide-up">
      <div v-if="showModal" class="modal-mask" @click.self="showModal = false">
        <div class="modal-panel">
          <div class="modal-header">
            <span class="modal-title">新增收货地址</span>
            <div class="modal-close" @click="showModal = false">
              <svg viewBox="0 0 24 24" width="20" height="20">
                <path d="M18 6L6 18M6 6l12 12" stroke="#5a524c" stroke-width="2" fill="none" stroke-linecap="round"/>
              </svg>
            </div>
          </div>

          <div class="modal-body">
            <div class="picker-wrap">
              <RegionPicker
                v-model:province="addrProvince"
                v-model:city="addrCity"
                v-model:district="addrDistrict"
              />
            </div>
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

    <!-- 删除确认弹窗 -->
    <transition name="fade">
      <div v-if="delTarget" class="confirm-overlay" @click.self="delTarget = null">
        <div class="confirm-card">
          <p class="confirm-msg">确定删除该收货地址吗？</p>
          <div class="confirm-btns">
            <button class="btn-cancel" @click="delTarget = null">取消</button>
            <button class="btn-confirm" @click="doDelete" :disabled="delLoading">
              {{ delLoading ? '删除中...' : '确定' }}
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
import { ref, reactive, onMounted } from 'vue'
import { userStore } from '../store/user'
import { getAddressList, addAddress, deleteAddress } from '../api/shop'
import RegionPicker from '../components/RegionPicker.vue'

const addressList = ref([])
const loading = ref(true)
const showModal = ref(false)
const addrLoading = ref(false)
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

const delTarget = ref(null)
const delLoading = ref(false)

function showToast(msg, type = 'success') {
  toast.msg = msg
  toast.type = type
  toast.show = true
  setTimeout(() => { toast.show = false }, 2000)
}

async function loadAddresses() {
  const uid = userStore.user?.userId
  if (!uid) return
  try {
    const res = await getAddressList(uid)
    addressList.value = res.data || []
  } catch { /* ignore */ }
  finally { loading.value = false }
}

onMounted(loadAddresses)

function openAddModal() {
  form.receiver = ''
  form.phone = ''
  form.detail = ''
  addrProvince.value = ''
  addrCity.value = ''
  addrDistrict.value = ''
  showModal.value = true
}

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
      showModal.value = false
      await loadAddresses()
    }
  } catch { showToast('保存失败', 'error') }
  finally { addrLoading.value = false }
}

function selectAddr(addr) {
  // 可选：设为默认地址等操作
}

function confirmDelete(addr) {
  delTarget.value = addr
}

async function doDelete() {
  if (!delTarget.value) return
  delLoading.value = true
  try {
    await deleteAddress(delTarget.value.addressId)
    showToast('已删除')
    delTarget.value = null
    await loadAddresses()
  } catch { showToast('删除失败', 'error') }
  finally { delLoading.value = false }
}
</script>

<style scoped>
.addr-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: #FBF8F4;
  padding-top: max(env(safe-area-inset-top), 0px);
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
  height: 50vh;
  gap: 12px;
}

.empty-text { font-size: 15px; color: var(--text-muted); }

/* 地址列表 */
.addr-list { padding: 12px 16px; display: flex; flex-direction: column; gap: 10px; }

.addr-card {
  display: flex;
  align-items: stretch;
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
}

.card-body {
  flex: 1;
  padding: 16px;
  cursor: pointer;
  min-width: 0;
}

.card-actions {
  display: flex;
  align-items: center;
  padding: 0 12px;
  flex-shrink: 0;
  border-left: 1px solid #f5f0ea;
}

.del-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}

.del-btn:active { background: rgba(200, 60, 40, 0.08); }

.addr-top { display: flex; align-items: center; gap: 8px; margin-bottom: 6px; }

.addr-receiver { font-weight: 700; font-size: 16px; color: var(--text-primary); }
.addr-phone { font-size: 13px; color: var(--text-secondary); }

.default-tag {
  background: rgba(255, 122, 51, 0.1);
  color: var(--primary);
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 10px;
}

.addr-detail { font-size: 13px; color: var(--text-secondary); line-height: 1.4; }

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

.add-btn {
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

.add-btn:active { transform: scale(0.96); }

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

.form-group { margin-bottom: 10px; }

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

/* 确认弹窗 */
.confirm-overlay {
  position: fixed;
  inset: 0;
  background: rgba(18, 30, 31, 0.45);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 200;
  padding: 24px;
}

.confirm-card {
  width: 100%;
  max-width: 300px;
  background: #fff;
  border-radius: 18px;
  padding: 28px 24px 20px;
  text-align: center;
}

.confirm-msg {
  font-family: var(--font-heading);
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 22px;
}

.confirm-btns {
  display: flex;
  gap: 12px;
}

.btn-cancel, .btn-confirm {
  flex: 1;
  height: 42px;
  border-radius: 21px;
  font-family: var(--font-heading);
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 1px;
  transition: all 0.2s;
}

.btn-cancel {
  background: #f5f0ea;
  color: var(--text-secondary);
}

.btn-confirm {
  background: var(--gradient-primary);
  color: #fff;
  box-shadow: var(--shadow-primary);
}

.btn-confirm:disabled { opacity: 0.5; }
.btn-cancel:active, .btn-confirm:active { transform: scale(0.95); }

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
