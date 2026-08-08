<template>
  <div class="profile-page">
    <div class="header-section">
      <div class="avatar-wrapper">
        <img v-if="userInfo.avatar" :src="userInfo.avatar" class="avatar-img" @error="userInfo.avatar = ''" />
        <span v-else class="avatar-placeholder">{{ initial }}</span>
        <div class="avatar-ring"></div>
      </div>
      <div class="header-right">
        <div class="nickname-row">
          <span class="nickname">{{ userInfo.nickName || userStore.user?.nickName || '未登录' }}</span>
          <span v-if="isMember" class="vip-tag">VIP</span>
        </div>
        <div class="bio">热爱生活，享受下厨的每一刻</div>
      </div>
      <button class="edit-btn" @click="openEdit">编辑资料</button>
    </div>

    <div class="stats-card">
      <div class="stat-item" @click="goWorks">
        <div class="stat-num">{{ stats.works }}</div>
        <div class="stat-label">作品</div>
      </div>
      <div class="stat-item" @click="goFollowing">
        <div class="stat-num">{{ stats.following }}</div>
        <div class="stat-label">关注</div>
      </div>
      <div class="stat-item" @click="goFollowers">
        <div class="stat-num">{{ stats.followers }}</div>
        <div class="stat-label">粉丝</div>
      </div>
      <div class="stat-item">
        <div class="stat-num">{{ stats.likes }}</div>
        <div class="stat-label">获赞</div>
      </div>
    </div>

    <div class="menu-card">
      <div
        v-for="(item, index) in menuItems"
        :key="item.label"
        class="menu-item"
        @click="handleMenuClick(item)"
      >
        <div class="menu-left">
          <svg class="menu-icon-svg" viewBox="0 0 24 24" width="20" height="20">
            <template v-if="item.icon === 'recipe'">
              <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z" stroke="currentColor" stroke-width="1.5" fill="none"/><path d="M14 2v6h6M16 13H8M16 17H8M10 9H8" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linecap="round"/>
            </template>
            <template v-else-if="item.icon === 'vip'">
              <path d="M12 2l2.5 6.5L21 9l-5 4.5L17.5 21 12 17l-5.5 4L8 13.5 3 9l6.5-.5z" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linejoin="round"/>
            </template>
            <template v-else-if="item.icon === 'cart'">
              <circle cx="9" cy="21" r="1" fill="currentColor"/><circle cx="20" cy="21" r="1" fill="currentColor"/><path d="M1 1h4l2.68 13.39a2 2 0 002 1.61h9.72a2 2 0 002-1.61L23 6H6" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linecap="round"/>
            </template>
            <template v-else-if="item.icon === 'order'">
              <rect x="3" y="4" width="18" height="18" rx="2" stroke="currentColor" stroke-width="1.5" fill="none"/><path d="M3 10h18M8 2v4M16 2v4" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linecap="round"/>
            </template>
            <template v-else-if="item.icon === 'bell'">
              <path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9M13.73 21a2 2 0 01-3.46 0" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linecap="round"/>
            </template>
            <template v-else-if="item.icon === 'location'">
              <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7z" stroke="currentColor" stroke-width="1.5" fill="none"/><circle cx="12" cy="9" r="2.5" stroke="currentColor" stroke-width="1.5" fill="none"/>
            </template>
            <template v-else-if="item.icon === 'settings'">
              <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.5" fill="none"/><path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 01-2.83 2.83l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-4 0v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83-2.83l.06-.06A1.65 1.65 0 004.68 15a1.65 1.65 0 00-1.51-1H3a2 2 0 010-4h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 012.83-2.83l.06.06A1.65 1.65 0 009 4.68a1.65 1.65 0 001-1.51V3a2 2 0 014 0v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 2.83l-.06.06A1.65 1.65 0 0019.4 9a1.65 1.65 0 001.51 1H21a2 2 0 010 4h-.09a1.65 1.65 0 00-1.51 1z" stroke="currentColor" stroke-width="1.5" fill="none"/>
            </template>
            <template v-else>
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="1.5" fill="none"/><path d="M9.09 9a3 3 0 015.83 1c0 2-3 3-3 3" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linecap="round"/><circle cx="12" cy="17" r="1" fill="currentColor"/>
            </template>
          </svg>
          <span class="menu-label">{{ item.label }}</span>
        </div>
        <svg class="menu-arrow" viewBox="0 0 24 24" width="18" height="18">
          <path d="M9 18l6-6-6-6" stroke="#c4b8aa" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
    </div>

    <!-- Edit profile modal -->
    <transition name="modal">
      <div v-if="showEdit" class="modal-overlay" @click.self="showEdit = false">
        <div class="modal-card">
          <div class="modal-header">
            <h3 class="modal-title">编辑资料</h3>
            <button class="modal-close" @click="showEdit = false">
              <svg viewBox="0 0 24 24" width="20" height="20">
                <path d="M18 6L6 18M6 6l12 12" stroke="#9b9085" stroke-width="2" stroke-linecap="round"/>
              </svg>
            </button>
          </div>

          <div class="modal-input-group">
            <label class="modal-label">用户名</label>
            <input
              v-model="editForm.username"
              type="text"
              placeholder="请输入用户名"
              class="modal-input"
            />
          </div>

          <div class="modal-input-group">
            <label class="modal-label">昵称</label>
            <input
              v-model="editForm.nickName"
              type="text"
              placeholder="请输入昵称"
              class="modal-input"
            />
          </div>

          <div class="modal-input-group">
            <label class="modal-label">手机号</label>
            <input
              v-model="editForm.phone"
              type="tel"
              placeholder="请输入手机号"
              class="modal-input"
            />
          </div>

          <div class="modal-input-group">
            <label class="modal-label">邮箱</label>
            <input
              v-model="editForm.email"
              type="email"
              placeholder="请输入邮箱"
              class="modal-input"
            />
          </div>

          <button class="save-btn" @click="submitEdit" :disabled="editLoading">
            {{ editLoading ? '保存中...' : '保存修改' }}
          </button>
        </div>
      </div>
    </transition>

    <transition name="fade">
      <div v-if="toast.show" class="toast" :class="toast.type">{{ toast.msg }}</div>
    </transition>

    <AppTabbar />
  </div>
</template>

<script setup>
import { computed, reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { userStore } from '../store/user'
import { getUserDetail, getWorkCount, getLikeCount, getFollowingCount, getFollowerCount } from '../api/user'
import { checkMember } from '../api/member'
import { alterUser } from '../api/auth'
import AppTabbar from '../components/AppTabbar.vue'

const router = useRouter()
const isMember = ref(false)

const userInfo = reactive({ nickName: '', avatar: '' })

const initial = computed(() => {
  const name = userInfo.nickName || userStore.user?.username || ''
  return name.charAt(0).toUpperCase()
})

const stats = reactive({ works: 0, following: 0, followers: 0, likes: 0 })

const menuItems = [
  { label: '会员权益', route: '/member', icon: 'vip' },
  { label: '我的购物车', route: '/cart', icon: 'cart' },
  { label: '收货地址', route: '/address', icon: 'location' },
  { label: '我的订单', route: '/orders', icon: 'order' },
  { label: '消息通知', route: '/notifications', icon: 'bell' },
  { label: '账号设置', route: '/settings', icon: 'settings' },
  { label: '联系客服', route: '/ai-chat?mode=customer_service', icon: 'help' },
]

const showEdit = ref(false)
const editLoading = ref(false)
const toast = reactive({ show: false, msg: '', type: 'success' })

const editForm = reactive({
  username: '',
  nickName: '',
  phone: '',
  email: '',
})

onMounted(async () => {
  const user = userStore.user
  if (!user || !user.userId) {
    router.push('/')
    return
  }
  try {
    const [detailRes, worksRes, likesRes, followingRes, followerRes, memberFlag] = await Promise.all([
      getUserDetail(user.userId),
      getWorkCount(user.userId),
      getLikeCount(user.userId),
      getFollowingCount(user.userId),
      getFollowerCount(user.userId),
      checkMember(user.userId),
    ])
    isMember.value = memberFlag
    if (detailRes.data) {
      userInfo.nickName = detailRes.data.nickName || ''
      userInfo.avatar = detailRes.data.avatar || ''
    }
    stats.works = worksRes.data || 0
    stats.likes = likesRes.data || 0
    stats.following = followingRes.data || 0
    stats.followers = followerRes.data || 0
  } catch { /* ignore */ }
})

function showToast(msg, type = 'success') {
  toast.msg = msg
  toast.type = type
  toast.show = true
  setTimeout(() => { toast.show = false }, 2000)
}

function goWorks() {
  const user = userStore.user
  if (user) router.push('/my-recipes')
}

function goFollowing() {
  const user = userStore.user
  if (user) router.push('/following/' + user.userId)
}

function goFollowers() {
  const user = userStore.user
  if (user) router.push('/followers/' + user.userId)
}

function openEdit() {
  const user = userStore.user
  if (!user) return
  editForm.username = user.username || ''
  editForm.nickName = user.nickName || ''
  editForm.phone = user.phone || ''
  editForm.email = user.email || ''
  showEdit.value = true
}

async function submitEdit() {
  const user = userStore.user
  if (!user) return

  editLoading.value = true
  try {
    await alterUser({
      userId: user.userId,
      username: editForm.username.trim(),
      nickName: editForm.nickName.trim(),
      password: '',
      phone: editForm.phone.trim(),
      email: editForm.email.trim(),
    })
    // 更新本地store
    userStore.setUser({
      ...user,
      username: editForm.username.trim(),
      nickName: editForm.nickName.trim(),
      phone: editForm.phone.trim(),
      email: editForm.email.trim(),
    })
    userInfo.nickName = editForm.nickName.trim()
    showEdit.value = false
    showToast('修改成功')
  } catch {
    showToast('修改失败，请稍后重试', 'error')
  } finally {
    editLoading.value = false
  }
}

function handleMenuClick(item) {
  if (item.route) router.push(item.route)
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: var(--gradient-page);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: calc(44px + env(safe-area-inset-top, 0px)) var(--container-padding) 80px;
}

.header-section {
  display: flex;
  align-items: center;
  width: 100%;
  margin-top: 0;
}

.avatar-wrapper {
  width: 74px;
  height: 74px;
  border-radius: 50%;
  position: relative;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-ring {
  position: absolute;
  inset: -4px;
  border-radius: 50%;
  background: var(--gradient-primary);
  z-index: 0;
}

.avatar-img, .avatar-placeholder {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  position: relative;
  z-index: 1;
  border: 3px solid #fff;
}

.avatar-placeholder {
  background: linear-gradient(135deg, #F5F0E8, #EDE4D5);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  font-weight: 800;
  color: var(--primary);
  font-family: var(--font-heading);
}

.header-right {
  margin-left: 16px;
  flex: 1;
  min-width: 0;
}

.nickname-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nickname {
  font-family: var(--font-heading);
  font-size: 21px;
  font-weight: 800;
  color: var(--text-primary);
}

.vip-tag {
  background: var(--gradient-gold);
  color: #fff;
  font-family: var(--font-heading);
  font-size: 11px;
  font-weight: 800;
  padding: 3px 10px;
  border-radius: var(--radius-full);
  letter-spacing: 1.5px;
  box-shadow: var(--shadow-gold);
}

.bio {
  font-family: var(--font-body);
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: 6px;
}

.edit-btn {
  padding: 8px 16px;
  border-radius: var(--radius-full);
  border: 2px solid var(--primary);
  color: var(--primary);
  font-family: var(--font-heading);
  font-size: 13px;
  font-weight: 700;
  flex-shrink: 0;
  background: #fff;
  transition: all 0.25s;
}

.edit-btn:active {
  background: var(--primary);
  color: #fff;
  box-shadow: var(--shadow-primary);
}

.stats-card {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 18px 0;
  background: #fff;
  border-radius: var(--radius-xl);
  margin-top: 24px;
  box-shadow: var(--shadow-card);
  border: 1px solid rgba(255, 122, 51, 0.04);
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-num {
  font-family: var(--font-heading);
  font-size: 22px;
  font-weight: 900;
  color: var(--primary);
}

.stat-label {
  font-family: var(--font-body);
  font-size: 12px;
  color: var(--text-muted);
  font-weight: 500;
}

.menu-card {
  width: 100%;
  background: #fff;
  border-radius: var(--radius-xl);
  margin-top: 16px;
  box-shadow: var(--shadow-card);
  border: 1px solid rgba(255, 122, 51, 0.04);
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 54px;
  padding: 0 18px;
  cursor: pointer;
  transition: all 0.2s;
  border-bottom: 1px solid var(--divider);
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:active {
  background: var(--primary-bg-light);
}

.menu-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.menu-icon-svg {
  width: 20px;
  height: 20px;
  color: var(--primary);
  flex-shrink: 0;
}

.menu-label {
  font-family: var(--font-heading);
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
}

.menu-arrow {
  flex-shrink: 0;
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(18, 30, 31, 0.45);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 200;
  padding: 24px;
}

.modal-card {
  width: 100%;
  max-width: 340px;
  background: #fff;
  border-radius: var(--radius-2xl);
  padding: 28px 24px 24px;
  box-shadow: 0 20px 60px rgba(255, 122, 51, 0.15);
  animation: scaleIn 0.35s var(--ease-smooth);
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.modal-title {
  font-family: var(--font-heading);
  font-size: 20px;
  font-weight: 800;
  color: var(--text-primary);
  letter-spacing: 1px;
}

.modal-close {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}

.modal-close:active { background: var(--primary-bg); }

.modal-input-group {
  margin-bottom: 14px;
}

.modal-label {
  display: block;
  font-family: var(--font-heading);
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 6px;
  padding-left: 4px;
}

.modal-label .optional {
  font-weight: 400;
  color: var(--text-muted);
  font-size: 11px;
}

.modal-input {
  width: 100%;
  height: 46px;
  border: 1.5px solid var(--border);
  border-radius: var(--radius-md);
  padding: 0 14px;
  font-size: 14px;
  font-family: var(--font-body);
  background: #FFFBF7;
  transition: all 0.25s;
}

.modal-input:focus {
  border-color: var(--primary-lighter);
  background: #fff;
  box-shadow: 0 0 0 4px rgba(255, 176, 136, 0.12);
}

.modal-input::placeholder {
  color: var(--text-placeholder);
}

.save-btn {
  display: block;
  width: 100%;
  height: 48px;
  border-radius: var(--radius-full);
  background: var(--gradient-primary);
  color: #fff;
  font-family: var(--font-heading);
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 2px;
  margin-top: 8px;
  transition: all 0.3s var(--ease-smooth);
  box-shadow: var(--shadow-primary);
}

.save-btn:active {
  transform: scale(0.96);
  box-shadow: 0 2px 8px rgba(255, 122, 51, 0.2);
}

.save-btn:disabled {
  opacity: 0.55;
  transform: none;
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
  z-index: 300;
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

.modal-enter-active { transition: opacity 0.3s; }
.modal-enter-active .modal-card { animation: scaleIn 0.35s var(--ease-smooth); }
.modal-leave-active { transition: opacity 0.25s; }
.modal-leave-to { opacity: 0; }
</style>
