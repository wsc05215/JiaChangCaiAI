<template>
  <div class="ai-page">
    <div class="nav-bar">
      <div class="back-btn" @click="$router.back()">
        <svg viewBox="0 0 24 24" width="24" height="24">
          <path d="M15 18l-6-6 6-6" stroke="#6B5E52" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <span class="nav-title">{{ modeText }}</span>
      <div class="nav-placeholder">
        <span v-if="isMember && memberExpire" class="member-expire">{{ memberExpire }} 到期</span>
      </div>
    </div>

    <div class="msg-list" ref="msgList">
      <div v-if="messages.length === 0 && !streaming" class="welcome-area">
        <div class="welcome-avatar">
          <div class="welcome-avatar-circle">
            <svg viewBox="0 0 48 48" width="48" height="48" fill="none">
              <circle cx="24" cy="24" r="23" fill="url(#w-grad)" stroke="#fff" stroke-width="2.5"/>
              <path d="M24 10l2.5 8.5L35 21l-8.5 2.5L24 32l-2.5-8.5L13 21l8.5-2.5z" fill="#fff"/>
              <defs>
                <radialGradient id="w-grad" cx="40%" cy="35%">
                  <stop offset="0%" stop-color="#FF7E55"/>
                  <stop offset="100%" stop-color="#E84518"/>
                </radialGradient>
              </defs>
            </svg>
          </div>
          <div class="welcome-glow"></div>
        </div>
        <template v-if="mode === 'customer_service'">
          <div class="welcome-msg">你好，我是平台智能客服</div>
          <div class="welcome-sub">有什么可以帮助您的？</div>
          <div class="quick-prompts">
            <div class="prompt-chip" @click="quickAsk('怎么修改密码？')">修改密码</div>
            <div class="prompt-chip" @click="quickAsk('忘记密码怎么办？')">忘记密码</div>
            <div class="prompt-chip" @click="quickAsk('如何查看我的订单？')">查看订单</div>
            <div class="prompt-chip" @click="quickAsk('怎么申请退款？')">申请退款</div>
          </div>
        </template>
        <template v-else>
          <div class="welcome-msg">你好，我是你的饮食管家</div>
          <div class="welcome-sub">有什么我可以帮助您的呢？</div>
          <div class="quick-prompts">
            <div class="prompt-chip" @click="quickAsk('今天吃什么好呢？')">今天吃什么？</div>
            <div class="prompt-chip" @click="quickAsk('推荐几道低热量的家常菜')">低热量推荐</div>
            <div class="prompt-chip" @click="quickAsk('帮我搭配一周的菜单')">一周菜单</div>
          </div>
        </template>
      </div>

      <div
        v-for="(msg, i) in messages"
        :key="i"
        class="msg-row"
        :class="msg.role === 'user' ? 'msg-user' : 'msg-ai'"
      >
        <div v-if="msg.role === 'ai'" class="avatar-ai">
          <svg viewBox="0 0 20 20" width="12" height="12" fill="#fff">
            <circle cx="10" cy="10" r="9" fill="none" stroke="#fff" stroke-width="1.5" opacity="0.5"/>
            <path d="M10 4l1 4 4 1-4 1-1 4-1-4-4-1 4-1z"/>
          </svg>
        </div>
        <div class="msg-bubble" :class="msg.role === 'user' ? 'bubble-user' : 'bubble-ai'">
          <div class="msg-text">{{ msg.content }}</div>
        </div>
        <div v-if="msg.role === 'user'" class="avatar-user">{{ (userStore.user?.username || '我')[0] }}</div>
      </div>

      <div v-if="streaming" class="msg-row msg-ai">
        <div class="avatar-ai streaming">
          <svg viewBox="0 0 20 20" width="12" height="12" fill="#fff">
            <circle cx="10" cy="10" r="9" fill="none" stroke="#fff" stroke-width="1.5" opacity="0.5"/>
            <path d="M10 4l1 4 4 1-4 1-1 4-1-4-4-1 4-1z"/>
          </svg>
        </div>
        <div class="msg-bubble bubble-ai">
          <div class="msg-text">{{ streamingText || '思考中...' }}</div>
          <span v-if="streamingText" class="typing-dot"></span>
        </div>
      </div>
    </div>

    <div class="bottom-area">
      <div v-if="mode !== 'customer_service'" class="feature-btns">
        <button class="feature-btn" :class="{ active: mode === 'recipe' }" @click="switchMode('recipe')">
          <span class="f-icon">&#x1F4D6;</span> 定制食谱
        </button>
        <button class="feature-btn" :class="{ active: mode === 'menu' }" @click="switchMode('menu')">
          <span class="f-icon">&#x1F4CB;</span> 一键菜单
        </button>
        <button class="feature-btn" @click="ingredientManage">
          <span class="f-icon">&#x1F96C;</span> 食材管理
        </button>
      </div>

      <div class="input-row">
        <div class="input-card">
          <input
            v-model="input"
            class="text-input"
            :placeholder="inputPlaceholder"
            @keyup.enter="send"
            :disabled="streaming"
          />
          <button
            class="send-btn"
            @click="send"
            :disabled="!input.trim() || streaming"
            :class="{ ready: input.trim() && !streaming }"
          >
            <svg viewBox="0 0 24 24" width="20" height="20">
              <path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z" :stroke="input.trim() && !streaming ? '#fff' : '#C4B5AA'" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>
      </div>

      <div v-if="trialExhausted" class="trial-link" @click="showPayModal = true">
        开通会员解锁无限次数 &#x2192;
      </div>
    </div>

    <MemberPayModal
      v-if="showPayModal"
      @close="showPayModal = false"
      @success="onPaySuccess"
    />
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { streamChat } from '../api/ai'
import { checkMember, getExpireTime } from '../api/member'
import { userStore } from '../store/user'
import MemberPayModal from '../components/MemberPayModal.vue'

const router = useRouter()
const route = useRoute()
const input = ref('')
const messages = ref([])
const streaming = ref(false)
const streamingText = ref('')
const msgList = ref(null)
const mode = ref(route.query.mode || 'chat')
const isMember = ref(false)
const memberExpire = ref('')
const showPayModal = ref(false)

const trialExhausted = computed(() => {
  return messages.value.some(m => m.role === 'ai' && m.content.includes('额度已用完'))
})

let cancelStream = null

const modeText = computed(() => {
  return { chat: '饮食管家', recipe: '定制食谱', menu: '一键菜单', customer_service: '服务与帮助' }[mode.value]
})

const inputPlaceholder = computed(() => {
  return {
    chat: '输入你的饮食需求...',
    recipe: '描述你想要什么类型的食谱...',
    menu: '告诉我你想怎么搭配菜单...',
    customer_service: '输入您遇到的问题，例如：怎么修改密码？'
  }[mode.value]
})

onMounted(async () => {
  const uid = userStore.user?.userId
  if (uid) {
    isMember.value = await checkMember(uid)
    if (isMember.value) {
      memberExpire.value = formatExpire(await getExpireTime(uid))
    }
  }
})

function scrollBottom() {
  nextTick(() => {
    if (msgList.value) {
      msgList.value.scrollTop = msgList.value.scrollHeight
    }
  })
}

function addMessage(role, content) {
  messages.value.push({ role, content })
  scrollBottom()
}

function switchMode(newMode) {
  if (!isMember.value) {
    showPayModal.value = true
    return
  }
  if (mode.value === newMode) {
    mode.value = 'chat'
    return
  }
  mode.value = newMode
}

function quickAsk(text) {
  streamSend(mode.value, text)
}

function streamSend(m, msg) {
  addMessage('user', msg)
  streaming.value = true
  streamingText.value = ''

  const uid = userStore.user?.userId
  cancelStream = streamChat(msg, m, uid,
    (token) => {
      streamingText.value += token
      scrollBottom()
    },
    () => {
      if (streamingText.value) {
        addMessage('ai', streamingText.value)
      }
      streamingText.value = ''
      streaming.value = false
      cancelStream = null
    },
    () => {
      if (!streamingText.value) {
        addMessage('ai', '抱歉，出了点问题，请稍后重试。')
      } else {
        addMessage('ai', streamingText.value)
      }
      streamingText.value = ''
      streaming.value = false
      cancelStream = null
    }
  )
}

function send() {
  const text = input.value.trim()
  if (!text || streaming.value) return
  input.value = ''
  streamSend(mode.value, text)
}

function ingredientManage() {
  if (!isMember.value) {
    showPayModal.value = true
    return
  }
  streamSend('chat', '帮我看看家里的食材，哪些需要尽快用掉？有什么推荐的处理方法吗？')
}

async function onPaySuccess() {
  showPayModal.value = false
  const uid = userStore.user?.userId
  if (uid) {
    isMember.value = await checkMember(uid)
    if (isMember.value) {
      memberExpire.value = formatExpire(await getExpireTime(uid))
    }
  }
}

function formatExpire(time) {
  if (!time) return ''
  let d
  if (Array.isArray(time)) {
    d = new Date(time[0], time[1] - 1, time[2], time[3] || 0, time[4] || 0, time[5] || 0)
  } else {
    d = new Date(time)
  }
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}
</script>

<style scoped>
.ai-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  height: 100dvh;
  background: linear-gradient(180deg, #F5F0E8 0%, #F9F7F2 25%, #F8F4ED 100%);
}

.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 8px;
  flex-shrink: 0;
  background: rgba(249,247,242,0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(0,0,0,0.04);
}

.back-btn {
  width: 36px; height: 36px;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; border-radius: 50%;
}

.nav-title {
  font-size: 17px;
  font-weight: 800;
  color: var(--text-primary);
  letter-spacing: 0.5px;
}

.nav-placeholder { width: 36px; }

.member-expire {
  font-size: 10px; font-weight: 600;
  color: var(--gold);
  white-space: nowrap;
}

/* msg list */
.msg-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px 14px;
  -webkit-overflow-scrolling: touch;
}

/* welcome */
.welcome-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 60px;
}

.welcome-avatar {
  position: relative;
  margin-bottom: 28px;
}

.welcome-avatar-circle {
  position: relative;
  z-index: 1;
  filter: drop-shadow(0 6px 20px rgba(255, 122, 51,0.25));
}

.welcome-glow {
  position: absolute;
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  width: 80px; height: 80px;
  background: radial-gradient(circle, rgba(230,126,34,0.12) 0%, transparent 70%);
  border-radius: 50%;
  animation: welcomePulse 2.5s ease-in-out infinite;
}

@keyframes welcomePulse {
  0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 0.6; }
  50% { transform: translate(-50%, -50%) scale(1.3); opacity: 1; }
}

.welcome-msg {
  font-size: 20px;
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 8px;
  letter-spacing: 1px;
}

.welcome-sub {
  font-size: 14px;
  color: var(--text-muted);
  margin-bottom: 24px;
}

.quick-prompts {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: center;
}

.prompt-chip {
  padding: 10px 18px;
  background: #fff;
  border: 1.5px solid var(--border);
  border-radius: var(--radius-full);
  font-size: 13px;
  color: var(--text-secondary);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: var(--shadow-xs);
}

.prompt-chip:active {
  background: var(--primary-bg);
  border-color: var(--primary);
  color: var(--primary);
  transform: scale(0.96);
}

/* messages */
.msg-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 22px;
  gap: 8px;
  animation: fadeInUp 0.3s ease-out;
}

.msg-user { justify-content: flex-end; }

.avatar-ai {
  width: 34px; height: 34px;
  border-radius: 50%;
  background: var(--gradient-primary);
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 3px 10px rgba(255, 122, 51,0.25);
}

.avatar-ai.streaming {
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { box-shadow: 0 0 0 0 rgba(255, 122, 51,0.35); }
  50% { box-shadow: 0 0 0 12px rgba(255,94,44,0); }
}

.avatar-user {
  width: 34px; height: 34px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFF8F0, #EDE4D5);
  color: var(--text-muted);
  font-size: 14px; font-weight: 700;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}

.msg-bubble {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 20px;
  font-size: 14px;
  line-height: 1.65;
  word-break: break-word;
  white-space: pre-wrap;
}

.bubble-ai {
  background: #fff;
  color: var(--text-primary);
  border-top-left-radius: 6px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.04);
}

.bubble-user {
  background: var(--gradient-primary);
  color: #fff;
  border-top-right-radius: 6px;
  box-shadow: 0 4px 14px rgba(255, 122, 51,0.25);
}

.msg-text { display: inline; }

.typing-dot {
  display: inline-block;
  width: 7px; height: 14px;
  vertical-align: middle;
  background: var(--primary);
  border-radius: 2px;
  margin-left: 2px;
  animation: blink 1s step-end infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

/* bottom area */
.bottom-area {
  flex-shrink: 0;
  padding: 8px 14px;
  padding-bottom: max(10px, env(safe-area-inset-bottom));
  background: rgba(249,247,242,0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-top: 1px solid rgba(0,0,0,0.04);
}

.feature-btns {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.feature-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  background: #fff;
  border: 1.5px solid var(--border);
  border-radius: 12px;
  padding: 11px 0;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.25s var(--ease-smooth);
  box-shadow: var(--shadow-xs);
}

.f-icon { font-size: 14px; }

.feature-btn:active { transform: scale(0.96); }

.feature-btn.active {
  border-color: var(--primary);
  color: var(--primary);
  background: var(--primary-bg);
  font-weight: 800;
  box-shadow: 0 2px 12px rgba(230,126,34,0.08);
}

.input-row {
  display: flex;
  gap: 8px;
}

.input-card {
  flex: 1;
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 26px;
  padding: 0 6px 0 20px;
  box-shadow: 0 4px 24px rgba(30,21,15,0.05);
  border: 1.5px solid var(--border);
  transition: all 0.25s;
}

.input-card:focus-within {
  border-color: var(--primary);
  box-shadow: 0 4px 24px rgba(230,126,34,0.1);
}

.text-input {
  flex: 1;
  height: 46px;
  border: none; outline: none;
  font-size: 14px;
  color: var(--text-primary);
  background: transparent;
}

.text-input::placeholder { color: var(--text-placeholder); }

.send-btn {
  width: 40px; height: 40px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
  transition: all 0.3s var(--ease-bounce);
  background: transparent;
}

.send-btn.ready {
  background: var(--gradient-primary);
  box-shadow: 0 3px 14px rgba(255, 122, 51,0.25);
}

.send-btn:active {
  transform: scale(0.85);
}

.trial-link {
  text-align: center;
  font-size: 12px;
  color: var(--primary);
  padding: 8px 0 0;
  cursor: pointer;
  font-weight: 700;
}
</style>
