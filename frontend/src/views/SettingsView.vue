<template>
  <div class="settings-page">
    <div class="nav-bar">
      <div class="back-btn" @click="$goBack()">
        <svg viewBox="0 0 24 24" width="24" height="24">
          <path d="M15 18l-6-6 6-6" stroke="#5a524c" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <span class="nav-title">账号设置</span>
      <div class="nav-placeholder"></div>
    </div>

    <!-- 修改密码 -->
    <div class="section-card">
      <div class="section-title">修改密码</div>
      <div class="input-group">
        <label class="input-label">新密码</label>
        <input
          v-model="passwordForm.newPassword"
          type="password"
          placeholder="请输入新密码"
          class="form-input"
        />
      </div>
      <div class="input-group">
        <label class="input-label">确认密码</label>
        <input
          v-model="passwordForm.confirmPassword"
          type="password"
          placeholder="请再次输入新密码"
          class="form-input"
        />
      </div>
      <button class="action-btn primary-btn" @click="handleChangePassword" :disabled="passwordLoading">
        {{ passwordLoading ? '修改中...' : '修改密码' }}
      </button>
    </div>

    <!-- 注销账号 -->
    <div class="section-card danger-card">
      <div class="section-title danger-title">注销账号</div>
      <p class="danger-desc">注销后所有数据将被永久删除，不可恢复。</p>
      <button class="action-btn danger-btn" @click="showDeleteConfirm = true">注销账号</button>
    </div>

    <!-- 退出登录 -->
    <div class="section-card">
      <button class="action-btn logout-btn" @click="handleLogout">退出登录</button>
    </div>

    <!-- 删除确认弹窗 -->
    <transition name="modal">
      <div v-if="showDeleteConfirm" class="modal-overlay" @click.self="showDeleteConfirm = false">
        <div class="modal-card">
          <div class="modal-header">
            <h3 class="modal-title">确认注销</h3>
            <button class="modal-close" @click="showDeleteConfirm = false">
              <svg viewBox="0 0 24 24" width="20" height="20">
                <path d="M18 6L6 18M6 6l12 12" stroke="#9b9085" stroke-width="2" stroke-linecap="round"/>
              </svg>
            </button>
          </div>
          <p class="modal-desc">注销后账号及所有数据将被<strong>永久删除</strong>，此操作不可撤销。确定要继续吗？</p>
          <div class="modal-actions">
            <button class="modal-btn cancel-btn" @click="showDeleteConfirm = false">取消</button>
            <button class="modal-btn confirm-delete-btn" @click="handleDeleteAccount" :disabled="deleteLoading">
              {{ deleteLoading ? '注销中...' : '确认注销' }}
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
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { userStore } from '../store/user'
import { alterUser, deleteUser } from '../api/auth'

const router = useRouter()

const passwordForm = reactive({
  newPassword: '',
  confirmPassword: '',
})
const passwordLoading = ref(false)

const showDeleteConfirm = ref(false)
const deleteLoading = ref(false)

const toast = reactive({ show: false, msg: '', type: 'success' })

function showToast(msg, type = 'success') {
  toast.msg = msg
  toast.type = type
  toast.show = true
  setTimeout(() => { toast.show = false }, 2000)
}

async function handleChangePassword() {
  const { newPassword, confirmPassword } = passwordForm
  if (!newPassword.trim()) return showToast('请输入新密码', 'error')
  if (newPassword.length < 6) return showToast('密码至少6位', 'error')
  if (newPassword !== confirmPassword) return showToast('两次密码不一致', 'error')

  const user = userStore.user
  if (!user) return

  passwordLoading.value = true
  try {
    await alterUser({
      userId: user.userId,
      username: user.username || '',
      nickName: user.nickName || '',
      password: newPassword.trim(),
      phone: user.phone || '',
      email: user.email || '',
    })
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    showToast('密码修改成功')
  } catch {
    showToast('修改失败，请稍后重试', 'error')
  } finally {
    passwordLoading.value = false
  }
}

async function handleDeleteAccount() {
  const user = userStore.user
  if (!user) return

  deleteLoading.value = true
  try {
    await deleteUser(user.userId)
    userStore.logout()
    showToast('账号已注销')
    setTimeout(() => {
      router.push('/')
    }, 800)
  } catch {
    showToast('注销失败，请稍后重试', 'error')
  } finally {
    deleteLoading.value = false
  }
}

function handleLogout() {
  userStore.logout()
  router.push('/')
}
</script>

<style scoped>
.settings-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: var(--gradient-page);
  padding-top: env(safe-area-inset-top, 0px);
  padding-bottom: 40px;
}

.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 8px;
  background: rgba(249,247,242,0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  position: sticky;
  top: 0;
  z-index: 10;
}

.back-btn {
  width: 36px; height: 36px;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; border-radius: 50%;
}

.nav-title {
  font-size: 17px; font-weight: 800;
  color: var(--text-primary);
  letter-spacing: 0.5px;
}

.nav-placeholder { width: 36px; }

/* section */
.section-card {
  margin: 16px 16px 0;
  background: #fff;
  border-radius: var(--radius-xl);
  padding: 20px 18px;
  box-shadow: var(--shadow-xs);
  border: 1px solid rgba(0,0,0,0.03);
}

.section-title {
  font-size: 16px;
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 16px;
}

.input-group { margin-bottom: 14px; }

.input-label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
  margin-bottom: 6px;
  padding-left: 4px;
}

.form-input {
  width: 100%;
  height: 46px;
  border: 1.5px solid var(--border);
  border-radius: var(--radius-md);
  padding: 0 14px;
  font-size: 14px;
  background: #FFFBF7;
  transition: all 0.25s;
}

.form-input:focus {
  border-color: var(--primary-lighter);
  background: #fff;
  box-shadow: 0 0 0 4px rgba(255, 176, 136, 0.12);
  outline: none;
}

.form-input::placeholder { color: var(--text-placeholder); }

.action-btn {
  width: 100%;
  height: 46px;
  border-radius: var(--radius-full);
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 1px;
  cursor: pointer;
  transition: all 0.25s;
  border: none;
}

.action-btn:active { transform: scale(0.97); }

.primary-btn {
  background: var(--gradient-primary);
  color: #fff;
  box-shadow: var(--shadow-primary);
  margin-top: 4px;
}

.primary-btn:disabled { opacity: 0.55; transform: none; }

/* danger */
.danger-card { border: 1.5px solid rgba(220, 80, 60, 0.15); }

.danger-title { color: #d44; }

.danger-desc {
  font-size: 13px;
  color: var(--text-muted);
  margin-bottom: 16px;
  line-height: 1.6;
}

.danger-btn {
  background: #fff;
  color: #d44;
  border: 2px solid #d44;
}

.danger-btn:active { background: #d44; color: #fff; }

.logout-btn {
  background: #fff;
  color: var(--text-secondary);
  border: 2px solid var(--border);
}

.logout-btn:active { background: var(--bg); }

/* modal */
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
  max-width: 320px;
  background: #fff;
  border-radius: var(--radius-2xl);
  padding: 28px 24px 24px;
  box-shadow: 0 20px 60px rgba(255, 122, 51, 0.15);
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.modal-title {
  font-size: 18px;
  font-weight: 800;
  color: var(--text-primary);
}

.modal-close {
  width: 32px; height: 32px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer;
  transition: background 0.2s;
}

.modal-close:active { background: var(--primary-bg); }

.modal-desc {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.7;
  margin-bottom: 24px;
}

.modal-desc strong { color: #d44; }

.modal-actions {
  display: flex;
  gap: 12px;
}

.modal-btn {
  flex: 1;
  height: 44px;
  border-radius: var(--radius-full);
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.25s;
}

.cancel-btn {
  background: var(--bg);
  color: var(--text-secondary);
  border: 1.5px solid var(--border);
}

.cancel-btn:active { background: var(--divider); }

.confirm-delete-btn {
  background: #d44;
  color: #fff;
  border: none;
}

.confirm-delete-btn:active { opacity: 0.85; transform: scale(0.97); }
.confirm-delete-btn:disabled { opacity: 0.55; transform: none; }

/* toast */
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
}

.toast.success { background: rgba(18, 30, 31, 0.88); }
.toast.error { background: rgba(180, 60, 20, 0.9); }

.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

.modal-enter-active { transition: opacity 0.3s; }
.modal-leave-active { transition: opacity 0.25s; }
.modal-leave-to { opacity: 0; }
</style>
