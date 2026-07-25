<template>
  <div class="login-page">
    <h1 class="brand-title">佳尝菜</h1>

    <div class="login-card">
      <template v-if="mode === 'password'">
        <h2 class="welcome-text">欢迎回来</h2>
        <p class="sub-text">开启您的舌尖之旅，探索家常美味</p>

        <div class="input-wrapper">
          <svg class="input-icon" viewBox="0 0 1024 1024" width="22" height="22">
            <path d="M736.65 929.96H287.35a83.59 83.59 0 0 1-83.6-83.6V177.63a83.59 83.59 0 0 1 83.6-83.59h449.3a83.59 83.59 0 0 1 83.6 83.6v668.73a83.59 83.59 0 0 1-83.6 83.6zM287.35 135.84a41.8 41.8 0 0 0-41.8 41.8v668.73a41.8 41.8 0 0 0 41.8 41.8h449.3a41.8 41.8 0 0 0 41.8-41.8V177.63a41.8 41.8 0 0 0-41.8-41.8H287.35z" fill="#2c2c2c"/>
            <path d="M616.49 815.02H407.51a20.9 20.9 0 1 1 0-41.8h208.98a20.9 20.9 0 1 1 0 41.8z" fill="#2c2c2c"/>
          </svg>
          <input
            v-model="loginForm.username"
            type="text"
            placeholder="请输入账号/手机号"
            autocomplete="username"
          />
        </div>

        <div class="input-wrapper">
          <svg class="input-icon" viewBox="0 0 1024 1024" width="22" height="22">
            <path d="M798.29 956.45H228.02c-36.26 0-60.72-4.26-79.33-13.81-18.3-9.39-40.1-29.3-40.1-70.76V404.81h809.14v467.07c0 41.47-21.81 61.37-40.1 70.76-18.61 9.55-43.08 13.81-79.34 13.81zM149.52 445.74v426.14c0 28.3 12.51 43.64 78.5 43.64h570.27c66 0 78.51-15.34 78.51-43.64V445.74H149.52z" fill="#2c2c2c"/>
            <path d="M810.69 445.74H215.62v-20.46c0-4.27.15-57.95.54-65.9 4.46-90.34 36.48-166.61 92.6-220.56 26.62-25.58 57.9-45.37 92.96-58.81 34.68-13.29 72.17-20.03 111.44-20.03 39.04 0 76.32 6.63 110.8 19.72 34.91 13.24 66.08 32.76 92.64 58.01 56 53.24 88.3 128.76 93.4 218.4.52 9.24.7 64.57.7 69.17v20.46zm-554.08-40.93h513.07c-.1-18.32-.3-41.78-.56-46.39-5.12-156.44-105.6-257.52-252.19-257.52-145.5 0-248.42 104.68-256.11 260.5-.2 3.9-.35 25.88-.43 43.4z" fill="#2c2c2c"/>
            <path d="M492.69 586.79h40.93v205.15h-40.93z" fill="#2c2c2c"/>
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
                <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" fill="#F08B4F"/>
              </svg>
            </span>
            <span class="remember-text">记住密码</span>
          </label>
          <span class="toggle-mode" @click="mode = 'sms'">验证码登录</span>
          <span class="forgot-link">忘记密码？</span>
        </div>

        <button class="login-btn" @click="handleLogin" :disabled="loading">
          {{ loading ? '登录中...' : '一键登录' }}
        </button>

        <div class="divider">
          <span>其他方式登录</span>
        </div>

        <div class="social-icons">
          <svg viewBox="0 0 1024 1024" width="24" height="24">
            <path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z" fill="#2c2c2c"/>
            <path d="M680 370c-19.8 0-37.8 7.4-52 19.6V320H372v384h256V524c0-41.2 21.6-74 52-74 13.2 0 24 10.8 24 24v230h64V474c0-51.6-39.4-104-88-104z" fill="#2c2c2c"/>
          </svg>
          <svg viewBox="0 0 1024 1024" width="24" height="24">
            <circle cx="512" cy="512" r="448" fill="none" stroke="#2c2c2c" stroke-width="40"/>
            <path d="M420 420c0-33.14 26.86-60 60-60s60 26.86 60 60v60H420v-60zm-60 60h-60v200h424V480H360zm300 0v200c0 22.09-17.91 40-40 40H400c-22.09 0-40-17.91-40-40V480c-22.09 0-40-17.91-40-40v-20c0-66.27 53.73-120 120-120h140c66.27 0 120 53.73 120 120v20c0 22.09-17.91 40-40 40z" fill="#2c2c2c"/>
          </svg>
          <svg viewBox="0 0 1024 1024" width="24" height="24">
            <path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm206.8 401.2c-6.6 44.4-44.4 78-88.8 84.6-56.4 8.4-112.8 12.6-169.2 12.6-12 0-24 0-36-.6-43.2-1.8-85.8-8.4-127.2-19.2-27-7.2-52.8-16.2-77.4-27.6-4.2-1.8-8.4 1.2-7.2 6 3 14.4 7.8 28.2 13.8 41.4 16.8 36.6 42.6 66.6 75 89.4 42.6 30 93 46.8 146.4 46.8 55.8 0 109.2-12.6 157.8-37.2 37.8-19.2 70.2-46.8 94.8-81 18.6-25.8 32.4-55.8 37.8-90 0-4.2-3-6-6.6-4.2-12 6-25.2 9.6-38.4 12-15 2.4-30 3-45.6 3-7.8 0-15 0-22.8-.6-4.8-.6-3-6.6 0-7.8 15.6-5.4 30-12.6 43.2-21.6 9-6 18-13.2 25.2-21.6 2.4-3-.6-7.2-4.2-6-21.6 7.2-43.8 12-66.6 15-18 1.8-36.6 1.8-54.6 0-44.4-5.4-82.2-31.2-96-73.8-5.4-15.6-7.8-31.8-7.8-48.6 0-33 13.2-63 36.6-84.6 33.6-31.2 86.4-33.6 125.4-10.8z" fill="#2c2c2c"/>
          </svg>
        </div>
      </template>

      <template v-else>
        <svg class="back-arrow" viewBox="0 0 1024 1024" width="22" height="22" @click="mode = 'password'">
          <path d="M669.6 849.6c8.8 8 22.4 7.2 30.4-1.6 7.2-8 6.4-20.8-1.6-28.8L338.4 512l360-307.2c8-7.2 8.8-20 1.6-28.8-8-8.8-21.6-9.6-30.4-1.6L288 512l381.6 337.6z" fill="#2c2c2c"/>
        </svg>

        <h2 class="welcome-text">欢迎回来</h2>
        <p class="sub-text">开启您的舌尖之旅，探索家常美味</p>

        <div class="input-wrapper">
          <svg class="input-icon" viewBox="0 0 1024 1024" width="22" height="22">
            <path d="M736.65 929.96H287.35a83.59 83.59 0 0 1-83.6-83.6V177.63a83.59 83.59 0 0 1 83.6-83.59h449.3a83.59 83.59 0 0 1 83.6 83.6v668.73a83.59 83.59 0 0 1-83.6 83.6zM287.35 135.84a41.8 41.8 0 0 0-41.8 41.8v668.73a41.8 41.8 0 0 0 41.8 41.8h449.3a41.8 41.8 0 0 0 41.8-41.8V177.63a41.8 41.8 0 0 0-41.8-41.8H287.35z" fill="#2c2c2c"/>
            <path d="M616.49 815.02H407.51a20.9 20.9 0 1 1 0-41.8h208.98a20.9 20.9 0 1 1 0 41.8z" fill="#2c2c2c"/>
          </svg>
          <input
            v-model="smsForm.phone"
            type="tel"
            placeholder="请输入手机号"
            maxlength="11"
          />
        </div>

        <div class="input-wrapper">
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
    </div>

    <transition name="fade">
      <div v-if="toast.show" class="toast" :class="toast.type">{{ toast.msg }}</div>
    </transition>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../api/auth'
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
  phone: '',
  code: '',
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

function handleSmsLogin() {
  if (!smsForm.phone.trim()) return showToast('请输入手机号', 'error')
  if (!smsForm.code.trim()) return showToast('请输入验证码', 'error')
  showToast('验证码登录暂未对接后端', 'error')
}

function sendCode() {
  if (!smsForm.phone.trim()) return showToast('请输入手机号', 'error')
  if (smsForm.phone.length !== 11 || smsForm.phone[0] !== '1') return showToast('请输入正确的手机号', 'error')

  codeCountdown.value = 60
  const timer = setInterval(() => {
    codeCountdown.value--
    if (codeCountdown.value <= 0) clearInterval(timer)
  }, 1000)
  showToast('验证码已发送')
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
  background: var(--bg);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 16px;
  position: relative;
  overflow: hidden;
}

.brand-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--orange);
  margin-top: 15vh;
  margin-bottom: 24px;
  letter-spacing: 2px;
}

.login-card {
  width: 100%;
  max-width: 345px;
  background: var(--card-bg);
  border-radius: var(--radius-card);
  padding: 38px 22px 32px;
  position: relative;
  backdrop-filter: blur(4px);
  box-shadow: 0 4px 20px rgba(185, 158, 142, 0.1);
}

.back-arrow {
  position: absolute;
  left: 18px;
  top: 18px;
  cursor: pointer;
}

.welcome-text {
  font-size: 16px;
  font-weight: 700;
  text-align: center;
  color: #333;
}

.sub-text {
  font-size: 12px;
  color: var(--gray);
  text-align: center;
  margin-top: 6px;
  margin-bottom: 24px;
}

.input-wrapper {
  display: flex;
  align-items: center;
  height: 50px;
  border: 1px solid var(--brown);
  border-radius: var(--radius-input);
  padding: 0 14px 0 10px;
  background: var(--white);
}

.input-wrapper + .input-wrapper {
  margin-top: 16px;
}

.input-wrapper .input-icon {
  flex-shrink: 0;
  margin-right: 10px;
}

.input-wrapper input {
  flex: 1;
  height: 100%;
  font-size: 14px;
}

.code-btn {
  flex-shrink: 0;
  font-size: 12px;
  color: var(--orange-btn);
  white-space: nowrap;
  padding-left: 10px;
  border-left: 1px solid var(--brown);
  margin-left: 10px;
}

.code-btn:disabled {
  color: var(--gray);
}

.options-row {
  display: flex;
  align-items: center;
  position: relative;
  margin-top: 14px;
  font-size: 12px;
  color: var(--brown);
}

.remember-row {
  display: flex;
  align-items: center;
  cursor: pointer;
  gap: 6px;
}

.checkbox {
  width: 15px;
  height: 15px;
  border: 1px solid var(--brown);
  border-radius: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.checkbox.checked {
  background: rgba(240, 139, 79, 0.1);
}

.toggle-mode {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  cursor: pointer;
  white-space: nowrap;
}

.forgot-link {
  margin-left: auto;
  cursor: pointer;
  white-space: nowrap;
}

.login-btn {
  display: block;
  width: 100%;
  max-width: 300px;
  height: 48px;
  border-radius: var(--radius-btn);
  background: var(--orange-btn);
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 4px;
  margin: 20px auto 0;
  transition: opacity 0.2s;
}

.login-btn:active {
  opacity: 0.85;
}

.login-btn:disabled {
  opacity: 0.7;
}

.divider {
  text-align: center;
  margin-top: 28px;
  font-size: 12px;
  color: var(--gray);
  position: relative;
}

.divider::before,
.divider::after {
  content: '';
  position: absolute;
  top: 50%;
  width: 60px;
  height: 1px;
  background: #ddd;
}

.divider::before { left: 16px; }
.divider::after { right: 16px; }

.social-icons {
  display: flex;
  justify-content: center;
  gap: 32px;
  margin-top: 14px;
}

.social-icons svg {
  cursor: pointer;
  opacity: 0.6;
  transition: opacity 0.2s;
}

.social-icons svg:active {
  opacity: 1;
}

.toast {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  padding: 12px 28px;
  border-radius: 8px;
  font-size: 14px;
  color: #fff;
  z-index: 999;
  pointer-events: none;
}

.toast.success { background: rgba(0, 0, 0, 0.75); }
.toast.error { background: rgba(220, 60, 40, 0.85); }

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
