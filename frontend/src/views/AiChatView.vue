<template>
  <div class="ai-page">
    <div class="nav-bar">
      <div class="back-btn" @click="$goBack()">
        <svg viewBox="0 0 24 24" width="24" height="24">
          <path d="M15 18l-6-6 6-6" stroke="#6B5E52" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <span class="nav-title">{{ isCustomerService ? 'AI智能客服' : 'AI烹饪助手' }}</span>
      <div class="nav-placeholder"></div>
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
        <div class="welcome-msg">{{ isCustomerService ? '你好，我是AI智能客服' : '你好，我是AI烹饪助手' }}</div>
        <div class="welcome-sub">{{ isCustomerService ? '有什么使用问题可以问我' : '告诉我你想做什么菜？' }}</div>
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
      <div v-if="!speech.supported.value" class="mic-hint">当前浏览器不支持语音输入，请使用 Chrome 或 Edge</div>
      <div v-if="speech.error.value" class="mic-hint mic-error">{{ speech.error.value }}</div>
      <div class="input-row">
        <div class="input-card" :class="{ recording: speech.isListening.value || speech.isRecognizing.value }">
          <button
            class="mic-btn"
            :class="{ on: speech.isListening.value, recognizing: speech.isRecognizing.value }"
            @click="toggleMic"
            :disabled="streaming || speech.isRecognizing.value"
            :title="speech.isListening.value ? '点击停止' : speech.isRecognizing.value ? '识别中...' : '语音输入'"
          >
            <svg v-show="!speech.isListening.value && !speech.isRecognizing.value" viewBox="0 0 24 24" width="20" height="20" key="mic-idle">
              <path d="M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z" :fill="streaming ? '#D4CBC1' : '#8B7B6B'"/>
              <path d="M19 10v2a7 7 0 0 1-6 6.92V21h3v2H8v-2h3v-2.08A7 7 0 0 1 5 12v-2h2v2a5 5 0 0 0 10 0v-2z" :fill="streaming ? '#D4CBC1' : '#8B7B6B'"/>
            </svg>
            <svg v-show="speech.isListening.value" viewBox="0 0 24 24" width="20" height="20" class="pulse-icon" key="mic-recording">
              <circle cx="12" cy="12" r="11" fill="none" stroke="#E84518" stroke-width="1.5" opacity="0.3"/>
              <circle cx="12" cy="12" r="7" fill="#E84518" opacity="0.15"/>
              <path d="M12 3a3 3 0 0 0-3 3v6a3 3 0 0 0 6 0V6a3 3 0 0 0-3-3z" fill="#E84518"/>
              <path d="M17 10v2a5 5 0 0 1-10 0v-2" stroke="#E84518" stroke-width="2" fill="none" stroke-linecap="round"/>
            </svg>
            <svg v-show="speech.isRecognizing.value" viewBox="0 0 24 24" width="20" height="20" class="spin-icon" key="mic-spinner">
              <circle cx="12" cy="12" r="10" fill="none" stroke="#8B7B6B" stroke-width="2" stroke-dasharray="32 32" stroke-linecap="round"/>
            </svg>
          </button>
          <input
            v-model="input"
            class="text-input"
            :placeholder="speech.isListening.value ? '正在聆听...' : speech.isRecognizing.value ? '识别中...' : isCustomerService ? '输入您的问题...' : '输入菜名，如：番茄炒蛋怎么做？'"
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
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { streamChat } from '../api/ai'
import { userStore } from '../store/user'
import { useSpeechRecognition } from '../composables/useSpeechRecognition'

const route = useRoute()
const isCustomerService = computed(() => route.query.mode === 'customer_service')

const input = ref('')
const messages = ref([])
const streaming = ref(false)
const streamingText = ref('')
const msgList = ref(null)

const speech = useSpeechRecognition()

let cancelStream = null

async function toggleMic() {
  if (speech.isListening.value) {
    const text = await speech.stop()
    if (text) {
      input.value = text
    }
  } else {
    speech.reset()
    speech.start()
  }
}

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

function streamSend(msg) {
  addMessage('user', msg)
  streaming.value = true
  streamingText.value = ''

  const uid = userStore.user?.userId
  cancelStream = streamChat(msg, isCustomerService.value ? 'customer_service' : 'chef', uid,
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
  streamSend(text)
}
</script>

<style scoped>
.ai-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  height: 100dvh;
  background: linear-gradient(180deg, #F5F0E8 0%, #F9F7F2 25%, #F8F4ED 100%);
  padding-top: env(safe-area-inset-top, 0px);
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
  padding: 0 6px 0 6px;
  box-shadow: 0 4px 24px rgba(30,21,15,0.05);
  border: 1.5px solid var(--border);
  transition: all 0.25s;
}

.input-card:focus-within {
  border-color: var(--primary);
  box-shadow: 0 4px 24px rgba(230,126,34,0.1);
}

.input-card.recording {
  border-color: #E84518;
  box-shadow: 0 4px 24px rgba(232,69,24,0.12);
  animation: recordingGlow 1.5s ease-in-out infinite;
}

@keyframes recordingGlow {
  0%, 100% { box-shadow: 0 4px 24px rgba(232,69,24,0.08); }
  50% { box-shadow: 0 4px 28px rgba(232,69,24,0.18); }
}

/* mic button */
.mic-btn {
  width: 40px; height: 40px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
  transition: all 0.3s var(--ease-bounce);
  background: transparent;
}

.mic-btn:active {
  transform: scale(0.85);
}

.mic-btn.on {
  background: rgba(232,69,24,0.08);
}

.pulse-icon {
  animation: micPulse 1.2s ease-in-out infinite;
}

@keyframes micPulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.7; transform: scale(1.08); }
}

.spin-icon {
  animation: micSpin 1s linear infinite;
}

@keyframes micSpin {
  to { transform: rotate(360deg); }
}

.mic-btn.recognizing {
  background: rgba(139, 123, 107, 0.06);
}

.mic-hint {
  text-align: center;
  font-size: 12px;
  color: var(--text-muted);
  padding-bottom: 4px;
}

.mic-error {
  color: #D1523F;
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
</style>
