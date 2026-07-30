<template>
  <div class="ai-page">
    <!-- 顶部导航 -->
    <div class="nav-bar">
      <div class="back-btn" @click="$router.back()">
        <svg viewBox="0 0 24 24" width="24" height="24">
          <path d="M15 18l-6-6 6-6" stroke="#555" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <span class="nav-title">{{ modeText }}</span>
      <div class="nav-placeholder">
        <span v-if="isMember" class="member-tag">VIP</span>
      </div>
    </div>

    <!-- 消息列表 -->
    <div class="msg-list" ref="msgList">
      <div v-if="messages.length === 0 && !streaming" class="welcome-area">
        <div class="welcome-avatar">
          <svg viewBox="0 0 48 48" width="48" height="48" fill="none">
            <circle cx="24" cy="24" r="23" fill="url(#w-grad)" stroke="#fff" stroke-width="2"/>
            <path d="M24 10l2.5 8.5L35 21l-8.5 2.5L24 32l-2.5-8.5L13 21l8.5-2.5z" fill="#fff"/>
            <defs>
              <radialGradient id="w-grad" cx="40%" cy="35%">
                <stop offset="0%" stop-color="#FF8C5A"/>
                <stop offset="100%" stop-color="#E85D26"/>
              </radialGradient>
            </defs>
          </svg>
        </div>
        <div class="welcome-msg">你好，我是你的"饮食管家"~</div>
        <div class="welcome-sub">有什么我可以帮助您的呢？</div>
      </div>

      <div
        v-for="(msg, i) in messages"
        :key="i"
        class="msg-row"
        :class="msg.role === 'user' ? 'msg-user' : 'msg-ai'"
      >
        <div v-if="msg.role === 'ai'" class="avatar-ai">
          <svg viewBox="0 0 20 20" width="11" height="11" fill="#fff">
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
          <svg viewBox="0 0 20 20" width="11" height="11" fill="#fff">
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

    <!-- 底部按钮 + 输入区 -->
    <div class="bottom-area">
      <div class="feature-btns">
        <button
          class="feature-btn"
          :class="{ active: mode === 'recipe' }"
          @click="switchMode('recipe')"
        >定制食谱</button>
        <button
          class="feature-btn"
          :class="{ active: mode === 'menu' }"
          @click="switchMode('menu')"
        >一键菜单</button>
        <button
          class="feature-btn"
          @click="ingredientManage"
        >食材管理</button>
      </div>

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
          <svg viewBox="0 0 24 24" width="18" height="18">
            <path d="M3 20V4l19 8-19 8zm2-2.5L17 12 5 6.5v4L11 12l-6 1.5v4z" :fill="input.trim() && !streaming ? '#fff' : '#B99E8E'"/>
          </svg>
        </button>
      </div>

      <div v-if="trialExhausted" class="trial-link" @click="$router.push('/member')">
        开通会员解锁无限次数 →
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { streamChat } from '../api/ai'
import { checkMember } from '../api/member'
import { userStore } from '../store/user'

const router = useRouter()
const input = ref('')
const messages = ref([])
const streaming = ref(false)
const streamingText = ref('')
const msgList = ref(null)
const mode = ref('chat')
const isMember = ref(false)

const trialExhausted = computed(() => {
  return messages.value.some(m => m.role === 'ai' && m.content.includes('额度已用完'))
})

let cancelStream = null

const modeText = computed(() => {
  return { chat: '饮食管家', recipe: '定制食谱', menu: '一键菜单' }[mode.value]
})

const inputPlaceholder = computed(() => {
  return {
    chat: '输入你的饮食需求...',
    recipe: '描述你想要什么类型的食谱...',
    menu: '告诉我你想怎么搭配菜单...'
  }[mode.value]
})

onMounted(async () => {
  const uid = userStore.user?.userId
  if (uid) {
    isMember.value = await checkMember(uid)
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
  if (mode.value === newMode) {
    mode.value = 'chat'
    return
  }
  mode.value = newMode
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
  streamSend('chat', '帮我看看家里的食材，哪些需要尽快用掉？有什么推荐的处理方法吗？')
}
</script>

<style scoped>
.ai-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  height: 100dvh;
  background: linear-gradient(180deg, #fef9f4 0%, var(--bg) 30%);
}

/* ===== 顶部导航 ===== */
.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 8px;
  flex-shrink: 0;
}

.back-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 50%;
  flex-shrink: 0;
}

.back-btn:active {
  background: rgba(0,0,0,0.04);
}

.nav-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.nav-placeholder {
  width: 36px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.member-tag {
  background: linear-gradient(135deg, #f5a623, #e8961a);
  color: #fff;
  font-size: 10px;
  font-weight: 700;
  padding: 2px 6px;
  border-radius: 6px;
  letter-spacing: 1px;
}

/* ===== 消息列表 ===== */
.msg-list {
  flex: 1;
  overflow-y: auto;
  padding: 6px 14px;
  -webkit-overflow-scrolling: touch;
}

.welcome-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 80px;
}

.welcome-avatar {
  margin-bottom: 20px;
}

.welcome-msg {
  font-size: 17px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
}

.welcome-sub {
  font-size: 14px;
  color: #B99E8E;
}

/* 消息行 */
.msg-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 18px;
  gap: 8px;
}

.msg-user {
  justify-content: flex-end;
}

.avatar-ai {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FF8C5A, #E85D26);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.avatar-ai.streaming {
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { box-shadow: 0 0 0 0 rgba(232, 93, 38, 0.3); }
  50% { box-shadow: 0 0 0 8px rgba(232, 93, 38, 0); }
}

.avatar-user {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: #f0e4d6;
  color: #B99E8E;
  font-size: 13px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.msg-bubble {
  max-width: 72%;
  padding: 10px 14px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
  white-space: pre-wrap;
}

.bubble-ai {
  background: #fff;
  color: #333;
  border-top-left-radius: 4px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}

.bubble-user {
  background: linear-gradient(135deg, #FF8C5A, #E85D26);
  color: #fff;
  border-top-right-radius: 4px;
}

.msg-text {
  display: inline;
}

.typing-dot {
  display: inline-block;
  width: 8px;
  height: 14px;
  vertical-align: middle;
  background: linear-gradient(135deg, #FF8C5A, #E85D26);
  border-radius: 2px;
  margin-left: 2px;
  animation: blink 1s step-end infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

/* ===== 底部区 ===== */
.bottom-area {
  flex-shrink: 0;
  padding: 8px 14px;
  padding-bottom: max(8px, env(safe-area-inset-bottom));
}

.feature-btns {
  display: flex;
  gap: 10px;
  margin-bottom: 8px;
}

.feature-btn {
  flex: 1;
  background: #fff;
  border: 1px solid #e8e0d8;
  border-radius: 8px;
  padding: 10px 0;
  font-size: 15px;
  color: #666;
  cursor: pointer;
  transition: all 0.15s;
}

.feature-btn:active {
  transform: scale(0.97);
}

.feature-btn.active {
  border-color: #E85D26;
  color: #E85D26;
  background: #fefaf7;
}

.input-card {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 24px;
  padding: 0 6px 0 18px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  border: 1px solid #f0e8de;
}

.text-input {
  flex: 1;
  height: 42px;
  border: none;
  outline: none;
  font-size: 14px;
  color: #333;
  background: transparent;
}

.text-input::placeholder {
  color: #B99E8E;
}

.send-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.2s;
  background: transparent;
}

.send-btn.ready {
  background: linear-gradient(135deg, #FF8C5A, #E85D26);
}

.send-btn:active {
  transform: scale(0.92);
}

.trial-link {
  text-align: center;
  font-size: 12px;
  color: #E85D26;
  padding: 6px 0 0;
  cursor: pointer;
  text-decoration: underline;
}

</style>
