<template>
  <div class="login-page">
    <div class="bg-decor-top"></div>
    <div class="bg-decor-bottom"></div>

    <h1 class="brand-title">佳尝菜</h1>

    <div class="login-card">
      <!-- 第一步：输入邮箱 -->
      <template v-if="step === 1">
        <h2 class="welcome-text">找回密码</h2>
        <p class="sub-text">请输入注册邮箱，获取验证码</p>

        <div class="input-wrapper">
          <svg class="input-icon" viewBox="0 0 24 24" width="20" height="20">
            <rect x="2" y="4" width="20" height="16" rx="2" stroke="#c4b8aa" stroke-width="1.8" fill="none"/>
            <path d="M2 8l10 6 10-6" stroke="#c4b8aa" stroke-width="1.5" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <input
            v-model="email"
            type="email"
            placeholder="请输入注册邮箱"
          />
        </div>

        <button class="login-btn" @click="handleFindEmail" :disabled="loading">
          {{ loading ? '发送中...' : '获取验证码' }}
        </button>

        <p class="back-link" @click="$router.push('/')">返回登录</p>
      </template>

      <!-- 第二步：输入验证码 + 新密码 -->
      <template v-else>
        <svg class="back-arrow" viewBox="0 0 24 24" width="22" height="22" @click="step = 1">
          <path d="M15 18l-6-6 6-6" stroke="#5a524c" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>

        <h2 class="welcome-text">重置密码</h2>
        <p class="sub-text">验证码已发送至 {{ email }}</p>

        <div class="input-wrapper">
          <svg class="input-icon" viewBox="0 0 24 24" width="20" height="20">
            <rect x="3" y="5" width="18" height="14" rx="2" stroke="#c4b8aa" stroke-width="1.8" fill="none"/>
            <path d="M8 5v14" stroke="#c4b8aa" stroke-width="1.8"/>
            <path d="M13 12h4M13 8h3M13 16h3" stroke="#c4b8aa" stroke-width="1.5" stroke-linecap="round"/>
          </svg>
          <input
            v-model="code"
            type="text"
            placeholder="请输入验证码"
            maxlength="6"
          />
        </div>

        <div class="input-wrapper">
          <svg class="input-icon" viewBox="0 0 24 24" width="20" height="20">
            <rect x="3" y="11" width="18" height="11" rx="2" stroke="#c4b8aa" stroke-width="1.8" fill="none"/>
            <path d="M7 11V7a5 5 0 0110 0v4" stroke="#c4b8aa" stroke-width="1.8" fill="none" stroke-linecap="round"/>
          </svg>
          <input
            v-model="newPassword"
            type="password"
            placeholder="请输入新密码"
          />
        </div>

        <button class="login-btn" @click="handleResetPassword" :disabled="loading">
          {{ loading ? '重置中...' : '重置密码' }}
        </button>
      </template>
    </div>

    <transition name="fade">
      <div v-if="toast.show" class="toast" :class="toast.type">{{ toast.msg }}</div>
    </transition>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { findEmail, reastPassword } from '../api/auth'

const router = useRouter()

const step = ref(1)
const email = ref('')
const code = ref('')
const newPassword = ref('')
const loading = ref(false)

const toast = reactive({ show: false, msg: '', type: 'success' })

function showToast(msg, type = 'success') {
  toast.msg = msg
  toast.type = type
  toast.show = true
  setTimeout(() => { toast.show = false }, 2000)
}

async function handleFindEmail() {
  if (!email.value.trim()) return showToast('请输入邮箱', 'error')
  if (!email.value.includes('@')) return showToast('请输入正确的邮箱', 'error')

  loading.value = true
  try {
    const res = await findEmail(email.value.trim())
    if (res.data === 'ok') {
      showToast('验证码已发送')
      step.value = 2
    } else {
      showToast(res.data || '该用户不存在', 'error')
    }
  } catch {
    showToast('网络错误，请稍后重试', 'error')
  } finally {
    loading.value = false
  }
}

async function handleResetPassword() {
  if (!code.value.trim()) return showToast('请输入验证码', 'error')
  if (!newPassword.value.trim()) return showToast('请输入新密码', 'error')
  if (newPassword.value.length < 6) return showToast('密码至少6位', 'error')

  loading.value = true
  try {
    const res = await reastPassword(email.value.trim(), code.value.trim(), newPassword.value)
    if (res.data === '更改成功') {
      showToast('密码重置成功')
      setTimeout(() => {
        router.push('/')
      }, 800)
    } else {
      showToast(res.data || '重置失败', 'error')
    }
  } catch {
    showToast('网络错误，请稍后重试', 'error')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: linear-gradient(160deg, #F6F1EA 0%, #FAF7F2 40%, #F9F6F0 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 16px;
  position: relative;
  overflow: hidden;
}

.bg-decor-top {
  position: absolute;
  top: -100px;
  right: -60px;
  width: 260px;
  height: 260px;
  background: radial-gradient(circle, rgba(226,184,138,0.10) 0%, rgba(226,184,138,0.03) 40%, transparent 70%);
  border-radius: 50%;
  pointer-events: none;
}

.bg-decor-bottom {
  position: absolute;
  bottom: -40px;
  left: -40px;
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(226,184,138,0.08) 0%, transparent 70%);
  border-radius: 50%;
  pointer-events: none;
}

.brand-title {
  margin-top: 16vh;
  margin-bottom: 32px;
  position: relative;
  z-index: 1;
  font-family: var(--font-heading);
  font-size: 36px;
  font-weight: 900;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 8px;
}

.login-card {
  width: 100%;
  max-width: 345px;
  background: rgba(255, 255, 255, 0.82);
  border-radius: var(--radius-2xl);
  padding: 36px 24px 30px;
  position: relative;
  z-index: 1;
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  box-shadow: 0 8px 48px rgba(255, 122, 51, 0.05), 0 1px 4px rgba(0,0,0,0.02);
  border: 1px solid rgba(255,255,255,0.65);
}

.back-arrow {
  position: absolute;
  left: 16px;
  top: 16px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.back-arrow:active { opacity: 0.5; }

.welcome-text {
  font-family: var(--font-heading);
  font-size: 20px;
  font-weight: 800;
  text-align: center;
  color: var(--text-primary);
  letter-spacing: 1px;
}

.sub-text {
  font-family: var(--font-body);
  font-size: 13px;
  color: var(--text-secondary);
  text-align: center;
  margin-top: 6px;
  margin-bottom: 30px;
  word-break: break-all;
}

.input-wrapper {
  display: flex;
  align-items: center;
  height: 50px;
  border: 1.5px solid var(--border);
  border-radius: var(--radius-full);
  padding: 0 18px 0 14px;
  background: #FFFBF7;
  transition: all 0.3s var(--ease-smooth);
  margin-bottom: 14px;
}

.input-wrapper:focus-within {
  border-color: var(--primary-lighter);
  background: #fff;
  box-shadow: 0 0 0 5px rgba(255, 176, 136, 0.10);
}

.input-wrapper .input-icon {
  flex-shrink: 0;
  margin-right: 10px;
}

.input-wrapper input {
  flex: 1;
  height: 100%;
  font-size: 15px;
  font-family: var(--font-body);
}

.login-btn {
  display: block;
  width: 100%;
  max-width: 300px;
  height: 50px;
  border-radius: var(--radius-full);
  background: var(--gradient-primary);
  color: #fff;
  font-family: var(--font-heading);
  font-size: 18px;
  font-weight: 800;
  letter-spacing: 4px;
  margin: 22px auto 0;
  transition: all 0.3s var(--ease-smooth);
  box-shadow: var(--shadow-primary);
}

.login-btn:active {
  transform: scale(0.96);
  box-shadow: 0 2px 8px rgba(255, 122, 51, 0.2);
}

.login-btn:disabled {
  opacity: 0.55;
  transform: none;
}

.back-link {
  text-align: center;
  margin-top: 20px;
  font-size: 13px;
  color: var(--text-muted);
  cursor: pointer;
  font-weight: 500;
}

.back-link:active { color: var(--text-secondary); }

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
