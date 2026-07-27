<template>
  <div class="ai-page">
    <!-- 顶部导航 -->
    <div class="nav-bar">
      <div class="back-btn" @click="$router.back()">
        <svg viewBox="0 0 24 24" width="24" height="24">
          <path d="M15 18l-6-6 6-6" stroke="#555" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <div class="nav-center">
        <div class="nav-avatar">
          <svg viewBox="0 0 20 20" width="11" height="11" fill="#fff">
            <circle cx="10" cy="10" r="9" fill="none" stroke="#fff" stroke-width="1.5" opacity="0.5"/>
            <path d="M10 4l1 4 4 1-4 1-1 4-1-4-4-1 4-1z"/>
          </svg>
        </div>
        <span class="nav-title">AI 饮食管家</span>
      </div>
      <div class="nav-placeholder"></div>
    </div>

    <!-- 消息列表 -->
    <div class="msg-list" ref="msgList">
      <!-- 欢迎消息 -->
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

      <!-- 流式输出 -->
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

    <!-- 快捷提问 -->
    <div class="quick-bar">
      <span class="quick-chip" @click="sendQuick('根据我冰箱里的食材给我定制一份今日食谱')">
        <svg viewBox="0 0 16 16" width="12" height="12" fill="none">
          <path d="M3 2h10l2 2v8l-2 2H3l-2-2V4l2-2z" stroke="#E85D26" stroke-width="1.2" stroke-linejoin="round"/>
          <path d="M6 7h4M6 10h2" stroke="#E85D26" stroke-width="1.2" stroke-linecap="round"/>
        </svg>
        定制食谱
      </span>
      <span class="quick-chip" @click="sendQuick('帮我生成今天的一日三餐菜单')">
        <svg viewBox="0 0 16 16" width="12" height="12" fill="none">
          <circle cx="8" cy="8" r="6" stroke="#E85D26" stroke-width="1.2"/>
          <path d="M8 5v4M6 7h4" stroke="#E85D26" stroke-width="1.2" stroke-linecap="round"/>
        </svg>
        一键菜单
      </span>
      <span class="quick-chip" @click="sendQuick('帮我看看冰箱里的食材哪些需要尽快用掉')">
        <svg viewBox="0 0 16 16" width="12" height="12" fill="none">
          <rect x="2" y="3" width="12" height="11" rx="1.5" stroke="#E85D26" stroke-width="1.2"/>
          <path d="M5 1v3M11 1v3" stroke="#E85D26" stroke-width="1.2" stroke-linecap="round"/>
        </svg>
        食材管理
      </span>
    </div>

    <!-- 底部输入区 -->
    <div class="input-area">
      <div class="input-card">
        <input
          v-model="input"
          class="text-input"
          placeholder="输入你的饮食需求..."
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
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { streamChat } from '../api/ai'
import { userStore } from '../store/user'

const input = ref('')
const messages = ref([])
const streaming = ref(false)
const streamingText = ref('')
const msgList = ref(null)
let cancelStream = null

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

async function send() {
  const text = input.value.trim()
  if (!text || streaming.value) return

  addMessage('user', text)
  input.value = ''
  streaming.value = true
  streamingText.value = ''

  cancelStream = streamChat(text,
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
    (err) => {
      console.error('Chat error:', err)
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

function sendQuick(msg) {
  input.value = ''
  addMessage('user', msg)
  streaming.value = true
  streamingText.value = ''

  cancelStream = streamChat(msg,
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
</script>

<style scoped>
.ai-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  height: 100dvh;
  background: linear-gradient(180deg, #fef9f4 0%, var(--bg) 30%);
}

/* ===== 顶部 ===== */
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

.nav-center {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-avatar {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FF8C5A, #E85D26);
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.nav-placeholder {
  width: 36px;
  flex-shrink: 0;
}

/* ===== 消息列表 ===== */
.msg-list {
  flex: 1;
  overflow-y: auto;
  padding: 6px 14px;
  -webkit-overflow-scrolling: touch;
}

/* 欢迎区 */
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

/* ===== 快捷提问 ===== */
.quick-bar {
  display: flex;
  gap: 8px;
  padding: 6px 14px;
  flex-shrink: 0;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}

.quick-bar::-webkit-scrollbar {
  display: none;
}

.quick-chip {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 5px;
  background: #fff;
  color: #E85D26;
  font-size: 12px;
  font-weight: 500;
  padding: 7px 14px;
  border-radius: 18px;
  cursor: pointer;
  border: 1px solid #fde0d2;
  transition: all 0.2s;
  white-space: nowrap;
}

.quick-chip:active {
  background: #E85D26;
  color: #fff;
  border-color: #E85D26;
}

.quick-chip:active svg path,
.quick-chip:active svg circle,
.quick-chip:active svg rect {
  stroke: #fff;
}

/* ===== 底部输入 ===== */
.input-area {
  padding: 8px 14px;
  padding-bottom: max(8px, env(safe-area-inset-bottom));
  flex-shrink: 0;
  background: transparent;
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
</style>
