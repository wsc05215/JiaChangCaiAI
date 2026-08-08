<template>
  <div class="login-page">
    <div class="bg-decor-top"></div>
    <div class="bg-decor-bottom"></div>

    <h1 class="brand-title">佳尝菜</h1>

    <div class="login-card">
      <template v-if="mode === 'password'">
        <h2 class="welcome-text">欢迎回来</h2>
        <p class="sub-text">开启您的舌尖之旅，探索家常美味</p>

        <div class="input-wrapper">
          <svg class="input-icon" viewBox="0 0 24 24" width="20" height="20">
            <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" stroke="#c4b8aa" stroke-width="1.8" fill="none" stroke-linecap="round"/>
            <circle cx="12" cy="7" r="4" stroke="#c4b8aa" stroke-width="1.8" fill="none"/>
          </svg>
          <input
            v-model="loginForm.username"
            type="text"
            placeholder="请输入账号/手机号"
            autocomplete="username"
          />
        </div>

        <div class="input-wrapper">
          <svg class="input-icon" viewBox="0 0 24 24" width="20" height="20">
            <rect x="3" y="11" width="18" height="11" rx="2" stroke="#c4b8aa" stroke-width="1.8" fill="none"/>
            <path d="M7 11V7a5 5 0 0110 0v4" stroke="#c4b8aa" stroke-width="1.8" fill="none" stroke-linecap="round"/>
          </svg>
          <input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            autocomplete="current-password"
          />
        </div>

        <div class="options-row">
          <label class="remember-row" @click="rememberMe = !rememberMe">
            <span class="checkbox" :class="{ checked: rememberMe }">
              <svg v-if="rememberMe" viewBox="0 0 24 24" width="12" height="12">
                <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" fill="var(--primary)"/>
              </svg>
            </span>
            <span class="remember-text">记住密码</span>
          </label>
          <span class="forgot-link" @click="$router.push('/find-password')">忘记密码？</span>
        </div>

        <button class="login-btn" @click="handleLogin" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>

        <div class="divider">
          <span>其他方式登录</span>
        </div>

        <div class="other-login" @click="mode = 'sms'">
          <svg viewBox="0 0 24 24" width="24" height="24">
            <rect x="2" y="4" width="20" height="16" rx="2" stroke="#8B7B6B" stroke-width="1.6" fill="none"/>
            <path d="M2 8l10 6 10-6" stroke="#8B7B6B" stroke-width="1.3" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>

        <div class="switch-mode">
          <span>还没有账号？</span>
          <span class="switch-link" @click="mode = 'register'">注册新账号</span>
        </div>
      </template>

      <template v-else-if="mode === 'sms'">
        <svg class="back-arrow" viewBox="0 0 24 24" width="22" height="22" @click="mode = 'password'">
          <path d="M15 18l-6-6 6-6" stroke="#5a524c" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>

        <h2 class="welcome-text">验证码登录</h2>
        <p class="sub-text">请输入邮箱获取验证码</p>

        <div class="input-wrapper">
          <svg class="input-icon" viewBox="0 0 24 24" width="20" height="20">
            <rect x="2" y="4" width="20" height="16" rx="2" stroke="#c4b8aa" stroke-width="1.8" fill="none"/>
            <path d="M2 8l10 6 10-6" stroke="#c4b8aa" stroke-width="1.5" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <input
            v-model="smsForm.email"
            type="email"
            placeholder="请输入邮箱"
          />
        </div>

        <div class="input-wrapper">
          <svg class="input-icon" viewBox="0 0 24 24" width="20" height="20">
            <rect x="3" y="5" width="18" height="14" rx="2" stroke="#c4b8aa" stroke-width="1.8" fill="none"/>
            <path d="M8 5v14" stroke="#c4b8aa" stroke-width="1.8"/>
            <path d="M13 12h4M13 8h3M13 16h3" stroke="#c4b8aa" stroke-width="1.5" stroke-linecap="round"/>
          </svg>
          <input
            v-model="smsForm.code"
            type="text"
            placeholder="请输入验证码"
            maxlength="6"
          />
          <button class="code-btn" @click="sendCode" :disabled="codeCountdown > 0">
            {{ codeCountdown > 0 ? codeCountdown + 's' : '获取验证码' }}
          </button>
        </div>

        <button class="login-btn" @click="handleSmsLogin" :disabled="loading">
          {{ loading ? '登录中...' : '一键登录' }}
        </button>
      </template>

      <template v-else-if="mode === 'register'">
        <svg class="back-arrow" viewBox="0 0 24 24" width="22" height="22" @click="mode = 'password'">
          <path d="M15 18l-6-6 6-6" stroke="#5a524c" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>

        <h2 class="welcome-text">创建账号</h2>
        <p class="sub-text">注册佳尝菜，开启美食之旅</p>

        <div class="input-wrapper">
          <svg class="input-icon" viewBox="0 0 24 24" width="20" height="20">
            <rect x="2" y="4" width="20" height="16" rx="2" stroke="#c4b8aa" stroke-width="1.8" fill="none"/>
            <path d="M2 8l10 6 10-6" stroke="#c4b8aa" stroke-width="1.5" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <input
            v-model="registerForm.email"
            type="email"
            placeholder="请输入邮箱"
          />
        </div>

        <div class="input-wrapper">
          <svg class="input-icon" viewBox="0 0 24 24" width="20" height="20">
            <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" stroke="#c4b8aa" stroke-width="1.8" fill="none" stroke-linecap="round"/>
            <circle cx="12" cy="7" r="4" stroke="#c4b8aa" stroke-width="1.8" fill="none"/>
          </svg>
          <input
            v-model="registerForm.username"
            type="text"
            placeholder="请输入用户名"
            autocomplete="off"
          />
        </div>

        <div class="input-wrapper">
          <svg class="input-icon" viewBox="0 0 24 24" width="20" height="20">
            <rect x="3" y="11" width="18" height="11" rx="2" stroke="#c4b8aa" stroke-width="1.8" fill="none"/>
            <path d="M7 11V7a5 5 0 0110 0v4" stroke="#c4b8aa" stroke-width="1.8" fill="none" stroke-linecap="round"/>
          </svg>
          <input
            v-model="registerForm.password"
            type="password"
            placeholder="请设置密码"
            autocomplete="new-password"
          />
        </div>

        <div class="input-wrapper">
          <svg class="input-icon" viewBox="0 0 24 24" width="20" height="20">
            <rect x="3" y="11" width="18" height="11" rx="2" stroke="#c4b8aa" stroke-width="1.8" fill="none"/>
            <path d="M7 11V7a5 5 0 0110 0v4" stroke="#c4b8aa" stroke-width="1.8" fill="none" stroke-linecap="round"/>
          </svg>
          <input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            autocomplete="new-password"
          />
        </div>

        <button class="login-btn" @click="handleRegister" :disabled="loading">
          {{ loading ? '注册中...' : '注册' }}
        </button>

        <div class="switch-mode">
          <span>已有账号？</span>
          <span class="switch-link" @click="mode = 'password'">去登录</span>
        </div>
      </template>
    </div>

    <!-- 首次登录弹窗 -->
    <transition name="modal">
      <div v-if="showFirstLogin" class="modal-overlay">
        <div class="modal-card">
          <h3 class="modal-title">完善个人信息</h3>
          <p class="modal-sub">首次登录，请设置您的账号信息</p>

          <div class="modal-input-group">
            <label class="modal-label">用户名</label>
            <input
              v-model="firstLoginForm.username"
              type="text"
              placeholder="请输入用户名"
              class="modal-input"
            />
          </div>

          <div class="modal-input-group">
            <label class="modal-label">昵称</label>
            <input
              v-model="firstLoginForm.nickName"
              type="text"
              placeholder="请输入昵称"
              class="modal-input"
            />
          </div>

          <div class="modal-input-group">
            <label class="modal-label">密码</label>
            <input
              v-model="firstLoginForm.password"
              type="password"
              placeholder="请设置密码"
              class="modal-input"
            />
          </div>

          <div class="modal-input-group">
            <label class="modal-label">手机号 <span class="optional">(选填)</span></label>
            <input
              v-model="firstLoginForm.phone"
              type="tel"
              placeholder="请输入手机号"
              class="modal-input"
            />
          </div>

          <button class="login-btn modal-btn" @click="submitFirstLogin" :disabled="firstLoginLoading">
            {{ firstLoginLoading ? '保存中...' : '完成设置' }}
          </button>
        </div>
      </div>
    </transition>

    <transition name="fade">
      <div v-if="toast.show" class="toast" :class="toast.type">{{ toast.msg }}</div>
    </transition>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { login, sendEmailCode, emailLogin, isFirstLogin, alterUser, register } from '../api/auth'
import { userStore } from '../store/user'

const router = useRouter()

const mode = ref('password')
const rememberMe = ref(false)
const loading = ref(false)
const codeCountdown = ref(0)

const toast = reactive({ show: false, msg: '', type: 'success' })

const loginForm = reactive({
  username: '',
  password: '',
})

const smsForm = reactive({
  email: '',
  code: '',
})

// 首次登录弹窗
const showFirstLogin = ref(false)
const firstLoginLoading = ref(false)
const pendingUser = ref(null)

// 注册表单
const registerForm = reactive({
  email: '',
  username: '',
  password: '',
  confirmPassword: '',
})

const firstLoginForm = reactive({
  username: '',
  nickName: '',
  password: '',
  phone: '',
})

function showToast(msg, type = 'success') {
  toast.msg = msg
  toast.type = type
  toast.show = true
  setTimeout(() => { toast.show = false }, 2000)
}

async function handleLogin() {
  const { username, password } = loginForm
  if (!username.trim()) return showToast('请输入账号或手机号', 'error')
  if (!password.trim()) return showToast('请输入密码', 'error')

  loading.value = true
  try {
    const res = await login(username, password)
    if (res.data) {
      userStore.setUser(res.data)
      showToast('登录成功')
      if (rememberMe.value) {
        localStorage.setItem('savedUsername', username)
        localStorage.setItem('savedPassword', password)
      } else {
        localStorage.removeItem('savedUsername')
        localStorage.removeItem('savedPassword')
      }
      setTimeout(() => {
        router.push('/home')
      }, 500)
    } else {
      showToast('账号或密码错误', 'error')
    }
  } catch {
    showToast('网络错误，请稍后重试', 'error')
  } finally {
    loading.value = false
  }
}

async function handleSmsLogin() {
  if (!smsForm.email.trim()) return showToast('请输入邮箱', 'error')
  if (!smsForm.code.trim()) return showToast('请输入验证码', 'error')

  loading.value = true
  try {
    const res = await emailLogin(smsForm.email.trim(), smsForm.code.trim())
    if (res.data) {
      const user = res.data
      userStore.setUser(user)

      try {
        const firstRes = await isFirstLogin(user.userId)
        if (firstRes.data === true) {
          pendingUser.value = user
          firstLoginForm.username = user.username || ''
          firstLoginForm.nickName = user.nickName || ''
          firstLoginForm.password = ''
          firstLoginForm.phone = user.phone || ''
          showFirstLogin.value = true
          loading.value = false
          return
        }
      } catch { /* 检查失败则跳过弹窗 */ }

      showToast('登录成功')
      setTimeout(() => {
        router.push('/home')
      }, 500)
    } else {
      showToast('验证码错误或已过期', 'error')
    }
  } catch (e) {
    console.error(e)
    showToast(e.response?.data?.message || e.response?.data || '网络错误，请稍后重试', 'error')
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  const { email, username, password, confirmPassword } = registerForm
  if (!email.trim()) return showToast('请输入邮箱', 'error')
  if (!email.includes('@')) return showToast('请输入正确的邮箱格式', 'error')
  if (!username.trim()) return showToast('请输入用户名', 'error')
  if (!password.trim()) return showToast('请设置密码', 'error')
  if (password.length < 6) return showToast('密码不能少于6位', 'error')
  if (password !== confirmPassword) return showToast('两次密码不一致', 'error')

  loading.value = true
  try {
    const res = await register(email.trim(), username.trim(), password)
    if (res.data === 'ok') {
      showToast('注册成功')
      // 清空表单
      registerForm.email = ''
      registerForm.username = ''
      registerForm.password = ''
      registerForm.confirmPassword = ''
      // 切换到密码登录
      mode.value = 'password'
    } else {
      showToast(res.data || '注册失败', 'error')
    }
  } catch {
    showToast('网络错误，请稍后重试', 'error')
  } finally {
    loading.value = false
  }
}

async function submitFirstLogin() {
  if (!firstLoginForm.username.trim()) return showToast('请输入用户名', 'error')
  if (!firstLoginForm.nickName.trim()) return showToast('请输入昵称', 'error')
  if (!firstLoginForm.password.trim()) return showToast('请设置密码', 'error')

  firstLoginLoading.value = true
  try {
    await alterUser({
      userId: pendingUser.value.userId,
      username: firstLoginForm.username.trim(),
      nickName: firstLoginForm.nickName.trim(),
      password: firstLoginForm.password,
      phone: firstLoginForm.phone.trim(),
      email: pendingUser.value.email || '',
    })
    const updatedUser = {
      ...pendingUser.value,
      username: firstLoginForm.username.trim(),
      nickName: firstLoginForm.nickName.trim(),
      phone: firstLoginForm.phone.trim(),
    }
    userStore.setUser(updatedUser)
    showFirstLogin.value = false
    showToast('设置完成')
    setTimeout(() => {
      router.push('/home')
    }, 500)
  } catch {
    showToast('保存失败，请稍后重试', 'error')
  } finally {
    firstLoginLoading.value = false
  }
}

async function sendCode() {
  if (!smsForm.email.trim()) return showToast('请输入邮箱', 'error')
  if (!smsForm.email.includes('@')) return showToast('请输入正确的邮箱', 'error')

  try {
    const res = await sendEmailCode(smsForm.email.trim())
    if (res.data === 'ok') {
      codeCountdown.value = 60
      const timer = setInterval(() => {
        codeCountdown.value--
        if (codeCountdown.value <= 0) clearInterval(timer)
      }, 1000)
      showToast('验证码已发送')
    } else {
      // 显示后端返回的具体错误信息
      const errMsg = typeof res.data === 'string' && res.data.startsWith('fail:') ? res.data.substring(5) : '发送失败，请稍后重试'
      showToast(errMsg, 'error')
    }
  } catch {
    showToast('发送失败，请稍后重试', 'error')
  }
}

const savedUser = localStorage.getItem('savedUsername')
const savedPwd = localStorage.getItem('savedPassword')
if (savedUser && savedPwd) {
  loginForm.username = savedUser
  loginForm.password = savedPwd
  rememberMe.value = true
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

.code-btn {
  flex-shrink: 0;
  font-family: var(--font-heading);
  font-size: 13px;
  font-weight: 700;
  color: var(--primary);
  white-space: nowrap;
  padding-left: 14px;
  border-left: 1.5px solid var(--border);
  margin-left: 14px;
  transition: color 0.2s;
}

.code-btn:disabled {
  color: var(--text-placeholder);
  font-weight: 500;
}

.options-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 4px;
  margin-bottom: 8px;
  font-size: 12px;
  color: var(--text-muted);
}

.remember-row {
  display: flex;
  align-items: center;
  cursor: pointer;
  gap: 6px;
}

.checkbox {
  width: 17px;
  height: 17px;
  border: 1.5px solid var(--border);
  border-radius: 5px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.2s;
}

.checkbox.checked {
  background: var(--primary-bg);
  border-color: var(--primary);
}

.forgot-link {
  cursor: pointer;
  font-weight: 500;
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

.divider {
  text-align: center;
  margin-top: 30px;
  font-size: 12px;
  color: var(--text-muted);
  position: relative;
}

.divider::before,
.divider::after {
  content: '';
  position: absolute;
  top: 50%;
  width: 40px;
  height: 1px;
  background: var(--divider);
}

.divider::before { left: 24px; }
.divider::after { right: 24px; }

.other-login {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 18px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-secondary);
  transition: all 0.2s;
}

.other-login:active {
  color: var(--primary);
  transform: scale(0.96);
}

.switch-mode {
  text-align: center;
  margin-top: 18px;
  font-size: 12px;
  color: var(--text-muted);
}

.switch-link {
  color: var(--primary);
  font-weight: 700;
  cursor: pointer;
  margin-left: 4px;
  transition: opacity 0.2s;
}

.switch-link:active { opacity: 0.6; }

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

/* Modal overlay */
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
  padding: 32px 24px 28px;
  box-shadow: 0 20px 60px rgba(255, 122, 51, 0.15);
  animation: scaleIn 0.35s var(--ease-smooth);
}

.modal-title {
  font-family: var(--font-heading);
  font-size: 20px;
  font-weight: 800;
  text-align: center;
  color: var(--text-primary);
  letter-spacing: 1px;
}

.modal-sub {
  font-family: var(--font-body);
  font-size: 13px;
  color: var(--text-secondary);
  text-align: center;
  margin-top: 6px;
  margin-bottom: 24px;
}

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

.modal-btn {
  margin-top: 6px;
  max-width: 100%;
}

/* Transitions */
.fade-enter-active,
.fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from,
.fade-leave-to { opacity: 0; }

.modal-enter-active { transition: opacity 0.3s; }
.modal-enter-active .modal-card { animation: scaleIn 0.35s var(--ease-smooth); }
.modal-leave-active { transition: opacity 0.25s; }
.modal-leave-to { opacity: 0; }
</style>
