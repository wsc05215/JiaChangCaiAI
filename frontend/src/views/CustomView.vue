<template>
  <div class="custom-page">
    <!-- 会员状态 -->
    <template v-if="isMember">
      <!-- AI 智能卡片 -->
      <div class="ai-hero-card" @click="openAiChat">
        <div class="ai-glow-bg"></div>
        <div class="ai-hero-content">
          <div class="ai-icon-ring">
            <div class="ai-icon-inner">
              <svg viewBox="0 0 28 28" width="17" height="17" fill="none">
                <circle cx="14" cy="14" r="12" stroke="#fff" stroke-width="1.8"/>
                <path d="M14 5l1.5 5.5L21 12l-5.5 1.5L14 19l-1.5-5.5L7 12l5.5-1.5z" fill="#fff"/>
              </svg>
            </div>
          </div>
          <div class="ai-hero-text">
            <div class="ai-hero-greeting">你好，我是你的"饮食管家"~</div>
            <div class="ai-hero-sub">有什么我可以帮助您的呢</div>
          </div>
          <div class="ai-sparkle-row">
            <span class="ai-sparkle"></span>
            <span class="ai-sparkle delay-1"></span>
            <span class="ai-sparkle delay-2"></span>
          </div>
        </div>
      </div>


      <!-- 三个Tab -->
      <div class="tabs-row">
        <span class="tab" :class="{ active: activeTab === 'recipe' }" @click="activeTab = 'recipe'">我的食谱</span>
        <span class="tab" :class="{ active: activeTab === 'menu' }" @click="activeTab = 'menu'">一键菜谱</span>
        <span class="tab" :class="{ active: activeTab === 'fridge' }" @click="activeTab = 'fridge'">食材管理</span>
      </div>

      <!-- Tab: 我的食谱 -->
      <div v-if="activeTab === 'recipe'" class="tab-content">
        <div v-if="recipeRecords.length === 0" class="empty-state">
          <div class="empty-icon">&#x1F4D6;</div>
          <div class="empty-text">还没有定制食谱记录</div>
          <div class="empty-sub">点击上方"定制食谱"，说出你的饮食需求</div>
        </div>
        <div v-else class="record-list">
          <div v-for="rec in recipeRecords" :key="rec.id" class="record-card" @click="openDetail(rec)">
            <div class="record-title">{{ rec.title }}</div>
            <div class="record-preview">{{ getPreview(rec.content) }}</div>
            <div class="record-time">{{ formatTime(rec.createTime) }}</div>
          </div>
        </div>
      </div>

      <!-- Tab: 一键菜谱 -->
      <div v-if="activeTab === 'menu'" class="tab-content">
        <div v-if="menuRecords.length === 0" class="empty-state">
          <div class="empty-icon">&#x1F4CB;</div>
          <div class="empty-text">还没有一键菜谱记录</div>
          <div class="empty-sub">点击上方"一键菜谱"，AI根据冰箱食材生成菜谱</div>
        </div>
        <div v-else class="record-list">
          <div v-for="rec in menuRecords" :key="rec.id" class="record-card" @click="openDetail(rec)">
            <div class="record-title">{{ rec.title }}</div>
            <div class="record-preview">{{ getPreview(rec.content) }}</div>
            <div class="record-time">{{ formatTime(rec.createTime) }}</div>
          </div>
        </div>
      </div>

      <!-- Tab: 食材管理 -->
      <div v-if="activeTab === 'fridge'" class="tab-content">
        <div class="stats-row">
          <div class="stat-card">
            <div class="stat-num">{{ stats.total }}</div>
            <div class="stat-label">现有食材</div>
          </div>
          <div class="stat-card stat-warn">
            <div class="stat-num">{{ stats.nearExpiry }}</div>
            <div class="stat-label">临近过期</div>
          </div>
          <div class="stat-card stat-ok">
            <div class="stat-num">{{ canCookCount }}</div>
            <div class="stat-label">可做菜品</div>
          </div>
        </div>

        <div class="category-filters">
          <span v-for="cat in ['全部', ...categories]" :key="cat"
                class="filter-chip" :class="{ active: activeCat === cat }"
                @click="activeCat = cat">{{ cat }}</span>
        </div>

        <div v-if="filteredIngredients.length === 0" class="empty-state small">
          <div class="empty-text">暂无食材记录</div>
          <div class="empty-sub">点击上方"食材管理"，告诉AI你有什么食材</div>
        </div>
        <div v-else class="ingredient-list">
          <div v-for="ing in filteredIngredients" :key="ing.ingredientId" class="ingredient-row"
               :class="{ expired: ing.expired, near: ing.nearExpiry && !ing.expired }">
            <div class="ing-icon">{{ getIngIcon(ing.category) }}</div>
            <div class="ing-info">
              <div class="ing-name">{{ ing.name }}</div>
              <div class="ing-meta">{{ ing.category }} · {{ formatExpiry(ing) }}</div>
            </div>
            <div class="ing-del" @click.stop="handleDeleteIngredient(ing.ingredientId)">
              <svg viewBox="0 0 20 20" width="16" height="16">
                <path d="M6 6l8 8M14 6l-8 8" stroke="#C4B5AA" stroke-width="2" stroke-linecap="round"/>
              </svg>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- 非会员状态 -->
    <template v-else>
      <div class="non-member-area">
        <div class="nm-hero">
          <div class="nm-crown">&#x1F451;</div>
          <div class="nm-title">厨艺会员</div>
          <div class="nm-sub">解锁全部专属内容·畅享定制化饮食</div>
        </div>

        <div class="section-title">会员专属权益</div>
        <div class="benefits-row">
          <div class="benefit-card">
            <div class="benefit-icon">&#x1F4D6;</div>
            <div class="benefit-name">专属食谱</div>
            <div class="benefit-desc">根据你的需求为你定制专属食谱</div>
          </div>
          <div class="benefit-card">
            <div class="benefit-icon">&#x1F4CB;</div>
            <div class="benefit-name">一键配餐</div>
            <div class="benefit-desc">用普通的食材做出不普通的美食</div>
          </div>
          <div class="benefit-card">
            <div class="benefit-icon">&#x1F96C;</div>
            <div class="benefit-name">食材管理</div>
            <div class="benefit-desc">食材记录管理过期自动提醒</div>
          </div>
        </div>

        <div class="section-title">选择套餐</div>
        <div class="plans">
          <div class="plan-card">
            <div class="plan-badge">次卡</div>
            <div class="plan-info">
              <div class="plan-name">食谱定制会员</div>
              <div class="plan-desc">根据您的需求和口味偏好定制一周食谱</div>
            </div>
            <div class="plan-price"><span class="price-num">¥5.9</span>/次</div>
            <button class="plan-btn" @click="handleBuy">开通</button>
          </div>
          <div class="plan-card">
            <div class="plan-badge">月卡</div>
            <div class="plan-info">
              <div class="plan-name">食材管理会员</div>
              <div class="plan-desc">帮您管理家中食材 根据食材推荐食谱</div>
            </div>
            <div class="plan-price"><span class="price-num">¥18</span>/月</div>
            <button class="plan-btn" @click="handleBuy">开通</button>
          </div>
          <div class="plan-card plan-premium">
            <div class="plan-badge plan-badge-gold">推荐</div>
            <div class="plan-info">
              <div class="plan-name">尊享会员</div>
              <div class="plan-sub">享以上两种会员待遇 性价比之选</div>
            </div>
            <div class="plan-price"><span class="price-num price-num-gold">¥128</span>/年</div>
            <button class="plan-btn plan-btn-gold" @click="handleBuy">立即开通</button>
          </div>
        </div>
      </div>
    </template>

    <!-- 支付弹窗 -->
    <MemberPayModal
      v-if="showPayModal"
      @close="showPayModal = false"
      @success="onPaySuccess"
    />

    <!-- 记录详情弹窗 -->
    <teleport to="body">
      <div v-if="detailRecord" class="detail-overlay" @click="detailRecord = null">
        <div class="detail-card" @click.stop>
          <div class="detail-header">
            <span class="detail-title">{{ detailRecord.title }}</span>
            <span class="detail-close" @click="detailRecord = null">&times;</span>
          </div>
          <div class="detail-body">{{ detailRecord.content }}</div>
          <div class="detail-footer">
            <span class="detail-time">{{ formatTime(detailRecord.createTime) }}</span>
            <button class="detail-del" @click="deleteRecord(detailRecord.id)">删除记录</button>
          </div>
        </div>
      </div>
    </teleport>

    <!-- AI聊天弹窗 -->
    <teleport to="body">
      <div v-if="showAiOverlay" class="ai-overlay">
        <div class="ai-panel">
          <div class="ai-panel-header">
            <span class="ai-back" @click="closeAiChat">
              <svg viewBox="0 0 24 24" width="22" height="22">
                <path d="M15 18l-6-6 6-6" stroke="#6B5E52" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </span>
            <span class="ai-panel-title">{{ aiModeText }}</span>
            <span class="ai-close" @click="closeAiChat">&times;</span>
          </div>
          <div class="ai-msg-list" ref="aiMsgList">
            <div v-if="aiMessages.length === 0 && !aiStreaming" class="ai-welcome">
              <template v-if="!currentAiMode">
                <div class="ai-welcome-icon">
                  <svg viewBox="0 0 48 48" width="42" height="42" fill="none">
                    <circle cx="24" cy="24" r="22" fill="url(#awg)" stroke="#fff" stroke-width="2"/>
                    <path d="M24 10l2 7.5L33.5 19.5 26 21.5 24 29l-2-7.5-7.5-2 7.5-2z" fill="#fff"/>
                    <defs><radialGradient id="awg" cx="38%" cy="32%"><stop offset="0%" stop-color="#FF7E55"/><stop offset="100%" stop-color="#E84518"/></radialGradient></defs>
                  </svg>
                </div>
                <div class="ai-welcome-title">你好，我是你的"饮食管家"~</div>
                <div class="ai-welcome-msg">请选择你想要的功能</div>
                <div class="mode-chips">
                  <div class="mode-chip chip-recipe" @click="selectMode('recipe')">
                    <span class="chip-icon">&#x1F4D6;</span>
                    <span class="chip-label">定制食谱</span>
                  </div>
                  <div class="mode-chip chip-menu" @click="selectMode('menu')">
                    <span class="chip-icon">&#x1F4CB;</span>
                    <span class="chip-label">一键菜谱</span>
                  </div>
                  <div class="mode-chip chip-fridge" @click="selectMode('fridge')">
                    <span class="chip-icon">&#x1F96C;</span>
                    <span class="chip-label">食材管理</span>
                  </div>
                </div>
              </template>
              <template v-else>
                <div class="ai-welcome-icon">
                  <svg viewBox="0 0 48 48" width="42" height="42" fill="none">
                    <circle cx="24" cy="24" r="22" fill="url(#awg)" stroke="#fff" stroke-width="2"/>
                    <path d="M24 10l2 7.5L33.5 19.5 26 21.5 24 29l-2-7.5-7.5-2 7.5-2z" fill="#fff"/>
                    <defs><radialGradient id="awg" cx="38%" cy="32%"><stop offset="0%" stop-color="#FF7E55"/><stop offset="100%" stop-color="#E84518"/></radialGradient></defs>
                  </svg>
                </div>
                <div class="ai-welcome-title">{{ aiModeText }}</div>
                <div class="ai-welcome-msg">{{ aiWelcomeMsg }}</div>
                <div class="mode-switch" @click="currentAiMode = null">切换功能</div>
              </template>
            </div>
            <div v-for="(msg, i) in aiMessages" :key="i" class="ai-msg" :class="msg.role">
              <div class="ai-msg-text">{{ msg.content }}</div>
            </div>
            <div v-if="aiStreaming" class="ai-msg ai">
              <div class="ai-msg-text">{{ aiStreamingText || '思考中...' }}</div>
            </div>
          </div>
          <div class="ai-input-row">
            <input v-model="aiInput" class="ai-input" :placeholder="aiPlaceholder"
                   @keyup.enter="aiSend" :disabled="aiStreaming || !currentAiMode" />
            <button class="ai-send-btn" @click="aiSend" :disabled="!aiInput.trim() || aiStreaming || !currentAiMode">
              <svg viewBox="0 0 24 24" width="18" height="18">
                <path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z" stroke="#fff" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { userStore } from '../store/user'
import { checkMember, getExpireTime } from '../api/member'
import { listCustomRecords, deleteCustomRecord } from '../api/custom'
import { listIngredients, deleteIngredient, getIngredientStats } from '../api/ingredient'
import { streamChat } from '../api/ai'
import MemberPayModal from '../components/MemberPayModal.vue'

const router = useRouter()

const isMember = ref(false)
const expireTime = ref('')
const showPayModal = ref(false)

const activeTab = ref('recipe')
const categories = ['蔬菜', '生禽', '蛋类', '水产', '豆制品', '其他']

const recipeRecords = ref([])
const menuRecords = ref([])

const ingredients = ref([])
const stats = ref({ total: 0, nearExpiry: 0 })
const activeCat = ref('全部')

const detailRecord = ref(null)

const showAiOverlay = ref(false)
const aiMessages = ref([])
const aiStreaming = ref(false)
const aiStreamingText = ref('')
const aiInput = ref('')
const aiMsgList = ref(null)
const currentAiMode = ref(null)
let cancelStream = null

const filteredIngredients = computed(() => {
  if (activeCat.value === '全部') return ingredients.value
  return ingredients.value.filter(i => i.category === activeCat.value)
})

const canCookCount = computed(() => {
  const total = stats.value.total
  if (total >= 6) return '10+'
  if (total >= 3) return '5-8'
  if (total >= 1) return '1-3'
  return '0'
})

const aiModeText = computed(() => {
  if (currentAiMode.value === 'recipe') return '定制食谱'
  if (currentAiMode.value === 'menu') return '一键菜谱'
  if (currentAiMode.value === 'fridge') return 'AI食材管家'
  return ''
})

const aiWelcomeMsg = computed(() => {
  if (currentAiMode.value === 'recipe') return '告诉我你的饮食需求和偏好，我为你定制专属食谱'
  if (currentAiMode.value === 'menu') return '我会查看你的冰箱，为你推荐能做的菜'
  if (currentAiMode.value === 'fridge') return '告诉我你有什么食材，我帮你记录和管理'
  return ''
})

const aiPlaceholder = computed(() => {
  if (!currentAiMode.value) return '请先选择功能...'
  if (currentAiMode.value === 'recipe') return '如：帮我定制一周减脂食谱...'
  if (currentAiMode.value === 'menu') return '如：看看冰箱有什么，推荐几道菜...'
  return '如：我买了鸡蛋、西红柿...'
})

onMounted(async () => {
  const uid = userStore.user?.userId
  if (!uid) return
  isMember.value = await checkMember(uid)
  if (isMember.value) {
    expireTime.value = formatExpire(await getExpireTime(uid))
    loadRecords(uid)
    loadIngredients(uid)
  }
})

async function loadRecords(uid) {
  try {
    const [recipeRes, menuRes] = await Promise.all([
      listCustomRecords(uid, 'CustomizedRecipe'),
      listCustomRecords(uid, 'Oneclickmenu')
    ])
    recipeRecords.value = recipeRes.data || []
    menuRecords.value = menuRes.data || []
  } catch { /* ignore */ }
}

async function loadIngredients(uid) {
  try {
    const [listRes, statsRes] = await Promise.all([
      listIngredients(uid),
      getIngredientStats(uid)
    ])
    const list = listRes.data || []
    ingredients.value = list.map(ing => ({
      ...ing,
      expired: ing.expired !== undefined ? ing.expired : (ing.daysUntilExpiry != null && ing.daysUntilExpiry < 0),
      nearExpiry: ing.nearExpiry !== undefined ? ing.nearExpiry : (ing.daysUntilExpiry != null && ing.daysUntilExpiry >= 0 && ing.daysUntilExpiry <= 1)
    }))
    stats.value = statsRes.data || { total: 0, nearExpiry: 0 }
  } catch { /* ignore */ }
}

function getPreview(content) {
  if (!content) return ''
  const text = content.replace(/#{1,6}\s/g, '').replace(/\*\*/g, '').trim()
  return text.length > 100 ? text.substring(0, 100) + '...' : text
}

function formatTime(time) {
  if (!time) return ''
  let d
  if (Array.isArray(time)) {
    d = new Date(time[0], time[1] - 1, time[2], time[3] || 0, time[4] || 0, time[5] || 0)
  } else {
    d = new Date(time)
  }
  if (isNaN(d.getTime())) return ''
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

function formatExpire(time) { return formatTime(time) }

function formatExpiry(ing) {
  if (ing.daysUntilExpiry == null || ing.daysUntilExpiry > 36500) return '未知'
  if (ing.daysUntilExpiry < 0) return `已过期${Math.abs(ing.daysUntilExpiry)}天`
  if (ing.daysUntilExpiry === 0) return '今天到期'
  if (ing.daysUntilExpiry <= 1) return `剩余${ing.daysUntilExpiry}天`
  return `剩余${ing.daysUntilExpiry}天`
}

function getIngIcon(cat) {
  const map = { '蔬菜': '🥬', '生禽': '🥩', '蛋类': '🥚', '水产': '🐟', '豆制品': '🫘', '其他': '📦' }
  return map[cat] || '📦'
}

function openDetail(rec) { detailRecord.value = rec }

async function deleteRecord(id) {
  try {
    await deleteCustomRecord(id)
    detailRecord.value = null
    const uid = userStore.user?.userId
    if (uid) loadRecords(uid)
  } catch { /* ignore */ }
}

async function handleDeleteIngredient(id) {
  try {
    await deleteIngredient(id)
    const uid = userStore.user?.userId
    if (uid) loadIngredients(uid)
  } catch { /* ignore */ }
}

function handleBuy() { showPayModal.value = true }

async function onPaySuccess() {
  showPayModal.value = false
  const uid = userStore.user?.userId
  if (uid) {
    isMember.value = await checkMember(uid)
    if (isMember.value) {
      expireTime.value = formatExpire(await getExpireTime(uid))
      loadRecords(uid)
      loadIngredients(uid)
    }
  }
}

function openAiChat() {
  currentAiMode.value = null
  showAiOverlay.value = true
  aiMessages.value = []
  aiStreamingText.value = ''
}

function selectMode(mode) {
  currentAiMode.value = mode
}

function closeAiChat() {
  showAiOverlay.value = false
  currentAiMode.value = null
  aiMessages.value = []
  aiStreamingText.value = ''
  aiStreaming.value = false
  if (cancelStream) {
    cancelStream()
    cancelStream = null
  }
}

function aiScrollBottom() {
  nextTick(() => {
    if (aiMsgList.value) {
      aiMsgList.value.scrollTop = aiMsgList.value.scrollHeight
    }
  })
}

function aiSend() {
  const text = aiInput.value.trim()
  if (!text || aiStreaming.value || !currentAiMode.value) return
  aiInput.value = ''

  let mode
  if (currentAiMode.value === 'recipe') mode = 'customized_recipe'
  else if (currentAiMode.value === 'menu') mode = 'oneclick_menu'
  else mode = 'fridge'

  aiMessages.value.push({ role: 'user', content: text })
  aiStreaming.value = true
  aiStreamingText.value = ''

  const uid = userStore.user?.userId
  cancelStream = streamChat(text, mode, uid,
    (token) => {
      aiStreamingText.value += token
      aiScrollBottom()
    },
    () => {
      if (aiStreamingText.value) {
        aiMessages.value.push({ role: 'ai', content: aiStreamingText.value })
      }
      aiStreamingText.value = ''
      aiStreaming.value = false
      cancelStream = null
      if (uid) {
        if (currentAiMode.value === 'recipe' || currentAiMode.value === 'menu') loadRecords(uid)
        if (currentAiMode.value === 'fridge') loadIngredients(uid)
      }
    },
    () => {
      if (!aiStreamingText.value) {
        aiMessages.value.push({ role: 'ai', content: '抱歉，出了点问题，请稍后重试。' })
      } else {
        aiMessages.value.push({ role: 'ai', content: aiStreamingText.value })
      }
      aiStreamingText.value = ''
      aiStreaming.value = false
      cancelStream = null
    }
  )
}
</script>

<style scoped>
.custom-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: linear-gradient(180deg, #F5F0E8 0%, #F9F7F2 25%, #F8F4ED 100%);
  padding-bottom: 80px;
}

/* ===== AI 智能卡片 ===== */
.ai-hero-card {
  position: relative;
  margin: 14px 14px 0;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  border-radius: 20px;
  padding: 24px 18px;
  cursor: pointer;
  overflow: hidden;
  border: 1px solid rgba(255,255,255,0.08);
  transition: all 0.3s;
}

.ai-hero-card:active { transform: scale(0.985); }

.ai-glow-bg {
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse at 50% 30%, rgba(139,92,246,0.18) 0%, transparent 60%),
              radial-gradient(ellipse at 80% 80%, rgba(59,130,246,0.10) 0%, transparent 50%);
  animation: aiGlowShift 4s ease-in-out infinite;
  pointer-events: none;
}

@keyframes aiGlowShift {
  0%, 100% { opacity: 0.6; }
  50% { opacity: 1; }
}

.ai-hero-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.ai-icon-ring {
  width: 52px; height: 52px;
  border-radius: 50%;
  margin-bottom: 14px;
  background: conic-gradient(from 0deg, #8B5CF6, #3B82F6, #06B6D4, #8B5CF6);
  animation: aiRingSpin 3s linear infinite;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 3px;
}

.ai-icon-inner {
  width: 100%; height: 100%;
  border-radius: 50%;
  background: linear-gradient(135deg, #1e1b4b, #1e293b);
  display: flex;
  align-items: center;
  justify-content: center;
}

@keyframes aiRingSpin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.ai-hero-text { margin-bottom: 12px; }

.ai-hero-greeting {
  font-size: 17px;
  font-weight: 700;
  color: #e2e8f0;
  letter-spacing: 0.5px;
  margin-bottom: 4px;
}

.ai-hero-sub {
  font-size: 13px;
  color: #94a3b8;
}

.ai-sparkle-row {
  display: flex;
  justify-content: center;
  gap: 6px;
}

.ai-sparkle {
  width: 4px; height: 4px;
  border-radius: 50%;
  background: #8B5CF6;
  animation: sparklePulse 1.8s ease-in-out infinite;
  box-shadow: 0 0 6px rgba(139,92,246,0.6);
}

.ai-sparkle.delay-1 { animation-delay: 0.3s; background: #3B82F6; box-shadow: 0 0 6px rgba(59,130,246,0.6); }
.ai-sparkle.delay-2 { animation-delay: 0.6s; background: #06B6D4; box-shadow: 0 0 6px rgba(6,182,212,0.6); }

@keyframes sparklePulse {
  0%, 100% { transform: scale(1); opacity: 0.4; }
  50% { transform: scale(1.8); opacity: 1; }
}


/* ===== Tabs ===== */
.tabs-row {
  display: flex;
  gap: 6px;
  margin: 16px 14px;
  background: #fff;
  border-radius: 14px;
  padding: 4px;
  box-shadow: 0 2px 8px rgba(30,21,15,0.04);
}

.tab {
  flex: 1;
  text-align: center;
  padding: 10px 0;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-muted);
  border-radius: 11px;
  cursor: pointer;
  transition: all 0.25s;
}

.tab.active {
  background: var(--gradient-primary);
  color: #fff;
  font-weight: 700;
  box-shadow: 0 2px 10px rgba(255,122,51,0.2);
}

/* Tab Content */
.tab-content { padding: 0 14px; }

/* 统计 */
.stats-row { display: flex; gap: 10px; margin-bottom: 14px; }

.stat-card {
  flex: 1;
  background: #fff;
  border-radius: 14px;
  padding: 14px 10px;
  text-align: center;
  box-shadow: 0 1px 6px rgba(30,21,15,0.04);
}

.stat-num { font-size: 22px; font-weight: 800; color: var(--text-primary); }
.stat-label { font-size: 11px; color: var(--text-muted); margin-top: 2px; }
.stat-warn .stat-num { color: #E8783D; }
.stat-ok .stat-num { color: #56AB6F; }

/* 分类筛选 */
.category-filters {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 14px;
}

.filter-chip {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
  background: #fff;
  cursor: pointer;
  border: 1.5px solid var(--border);
  transition: all 0.2s;
}

.filter-chip.active {
  background: var(--gradient-primary);
  color: #fff;
  border-color: transparent;
  box-shadow: 0 2px 8px rgba(255,122,51,0.2);
}

/* 食材列表 */
.ingredient-list { display: flex; flex-direction: column; gap: 8px; }

.ingredient-row {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #fff;
  border-radius: 12px;
  padding: 12px 14px;
  box-shadow: 0 1px 4px rgba(30,21,15,0.03);
  border-left: 3px solid #56AB6F;
}

.ingredient-row.near { border-left-color: #E8A820; }
.ingredient-row.expired { border-left-color: #E8783D; opacity: 0.7; }

.ing-icon { font-size: 24px; flex-shrink: 0; }
.ing-info { flex: 1; min-width: 0; }
.ing-name { font-size: 14px; font-weight: 700; color: var(--text-primary); }
.ing-meta { font-size: 11px; color: var(--text-muted); margin-top: 2px; }

.ing-del {
  width: 28px; height: 28px;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; border-radius: 50%; flex-shrink: 0;
}
.ing-del:active { background: #F5F0E8; }

/* 记录列表 */
.record-list { display: flex; flex-direction: column; gap: 10px; }

.record-card {
  background: #fff;
  border-radius: 14px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(30,21,15,0.04);
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid rgba(0,0,0,0.03);
}

.record-card:active { transform: scale(0.98); }

.record-title {
  font-size: 15px; font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 6px;
}

.record-preview {
  font-size: 12px; color: var(--text-muted);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.record-time { font-size: 11px; color: var(--text-placeholder); margin-top: 8px; }

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 20px;
}

.empty-state.small { padding: 40px 20px; }
.empty-icon { font-size: 48px; margin-bottom: 14px; }
.empty-text { font-size: 15px; color: var(--text-secondary); font-weight: 600; }
.empty-sub { font-size: 12px; color: var(--text-muted); margin-top: 6px; text-align: center; }

/* ===== 非会员区 ===== */
.non-member-area { padding-bottom: 30px; }

.nm-hero {
  text-align: center;
  padding: 40px 20px 10px;
}

.nm-crown { font-size: 56px; margin-bottom: 12px; }
.nm-title { font-size: 24px; font-weight: 800; color: var(--text-primary); letter-spacing: 1px; }
.nm-sub { font-size: 14px; color: var(--text-secondary); margin-top: 8px; font-weight: 500; }

.section-title {
  font-size: 17px; font-weight: 800;
  color: var(--text-primary);
  padding: 22px 14px 12px;
  letter-spacing: 0.5px;
}

.benefits-row { display: flex; gap: 10px; padding: 0 14px; }

.benefit-card {
  flex: 1;
  background: #fff;
  border-radius: 14px;
  padding: 16px 10px;
  text-align: center;
  box-shadow: 0 1px 6px rgba(30,21,15,0.04);
  border: 1px solid rgba(0,0,0,0.03);
}

.benefit-icon { font-size: 28px; margin-bottom: 8px; }
.benefit-name { font-size: 13px; font-weight: 800; color: var(--text-primary); margin-bottom: 3px; }
.benefit-desc { font-size: 10px; color: var(--text-muted); line-height: 1.4; }

.plans { padding: 0 14px; display: flex; flex-direction: column; gap: 12px; }

.plan-card {
  background: #fff;
  border-radius: 14px;
  padding: 18px 14px;
  display: flex;
  align-items: center;
  gap: 10px;
  box-shadow: 0 1px 6px rgba(30,21,15,0.04);
  border: 2px solid var(--border);
  position: relative;
}

.plan-premium {
  border-color: #F5C34B;
  box-shadow: 0 4px 20px rgba(240,165,0,0.08);
  background: #FFFDF6;
}

.plan-badge {
  position: absolute;
  top: -10px; left: 14px;
  background: var(--text-secondary);
  color: #fff;
  font-size: 10px; font-weight: 800;
  padding: 2px 10px;
  border-radius: 10px;
  letter-spacing: 1px;
}

.plan-badge-gold {
  background: linear-gradient(135deg, #F5C34B, #E8A820);
  box-shadow: 0 2px 8px rgba(240,165,0,0.3);
}

.plan-info { flex: 1; min-width: 0; }
.plan-name { font-size: 14px; font-weight: 800; color: var(--text-primary); }
.plan-desc { font-size: 11px; color: var(--text-muted); margin-top: 2px; }
.plan-sub { font-size: 11px; color: var(--text-muted); margin-top: 2px; }

.plan-price { flex-shrink: 0; font-size: 11px; color: var(--text-muted); text-align: right; }
.price-num { font-size: 20px; font-weight: 900; color: var(--primary); }
.price-num-gold { color: #E8A820; }

.plan-btn {
  flex-shrink: 0;
  padding: 8px 16px;
  border-radius: 20px;
  border: 2px solid var(--primary);
  color: var(--primary);
  font-size: 12px; font-weight: 800;
  background: #fff;
  cursor: pointer;
  transition: all 0.2s;
}

.plan-btn:active { background: var(--primary); color: #fff; }

.plan-btn-gold {
  border-color: #E8A820;
  background: linear-gradient(135deg, #F5C34B, #E8A820);
  color: #fff;
  box-shadow: 0 3px 10px rgba(240,165,0,0.25);
}

/* ===== 详情弹窗 ===== */
.detail-overlay {
  position: fixed; inset: 0;
  background: rgba(20,14,8,0.55);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  display: flex; align-items: flex-end; justify-content: center;
  z-index: 9999;
}

.detail-card {
  width: 100%;
  max-width: 420px;
  max-height: 80vh;
  background: #fff;
  border-radius: 20px 20px 0 0;
  padding: 20px;
  overflow-y: auto;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.detail-title { font-size: 17px; font-weight: 800; color: var(--text-primary); }
.detail-close { font-size: 24px; color: var(--text-muted); cursor: pointer; }

.detail-body {
  font-size: 14px; color: var(--text-secondary);
  line-height: 1.75;
  white-space: pre-wrap;
}

.detail-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding-top: 14px;
  border-top: 1px solid var(--border);
}

.detail-time { font-size: 12px; color: var(--text-muted); }

.detail-del {
  padding: 6px 14px;
  border-radius: 14px;
  border: 1px solid #E8783D;
  color: #E8783D;
  font-size: 12px; font-weight: 600;
  cursor: pointer;
  background: #fff;
}

/* ===== AI弹窗 ===== */
.ai-overlay {
  position: fixed; inset: 0;
  background: rgba(20,14,8,0.55);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  display: flex; align-items: flex-end; justify-content: center;
  z-index: 9999;
}

.ai-panel {
  width: 100%;
  max-width: 420px;
  height: 85vh;
  background: linear-gradient(180deg, #F5F0E8, #F9F7F2);
  border-radius: 20px 20px 0 0;
  display: flex;
  flex-direction: column;
}

.ai-panel-header {
  display: flex;
  align-items: center;
  padding: 12px 14px;
  border-bottom: 1px solid rgba(0,0,0,0.06);
  flex-shrink: 0;
  gap: 8px;
}

.ai-back {
  width: 32px; height: 32px;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; border-radius: 50%;
}

.ai-panel-title { font-size: 16px; font-weight: 800; color: var(--text-primary); flex: 1; }
.ai-close { font-size: 24px; color: var(--text-muted); cursor: pointer; padding: 0 6px; }

.ai-msg-list { flex: 1; overflow-y: auto; padding: 14px; }

.ai-welcome {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 50px;
}

.ai-welcome-icon { margin-bottom: 20px; }
.ai-welcome-title { font-size: 18px; font-weight: 800; color: var(--text-primary); margin-bottom: 8px; }
.ai-welcome-msg { font-size: 13px; color: var(--text-muted); }

/* mode selection chips */
.mode-chips {
  display: flex;
  gap: 10px;
  margin-top: 22px;
}

.mode-chip {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 16px 18px;
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.2s;
  background: #fff;
  border: 1.5px solid var(--border);
  min-width: 85px;
}

.mode-chip:active { transform: scale(0.95); }

.chip-icon { font-size: 24px; }

.chip-label {
  font-size: 12px;
  font-weight: 700;
  color: var(--text-primary);
}

.chip-recipe { border-color: rgba(232, 120, 61, 0.25); background: #FFF8F2; }
.chip-recipe:active { background: #FFEDE0; }
.chip-menu { border-color: rgba(86, 171, 111, 0.25); background: #F4FAF6; }
.chip-menu:active { background: #E8F5EC; }
.chip-fridge { border-color: rgba(59, 130, 246, 0.25); background: #F2F6FF; }
.chip-fridge:active { background: #E3ECFE; }

.mode-switch {
  margin-top: 20px;
  font-size: 12px;
  color: var(--primary);
  cursor: pointer;
  font-weight: 600;
}

.ai-msg { margin-bottom: 14px; display: flex; }
.ai-msg.user { justify-content: flex-end; }
.ai-msg.ai { justify-content: flex-start; }

.ai-msg-text {
  max-width: 80%;
  padding: 10px 14px;
  border-radius: 16px;
  font-size: 13px;
  line-height: 1.55;
  white-space: pre-wrap;
  word-break: break-word;
}

.ai-msg.user .ai-msg-text {
  background: var(--gradient-primary);
  color: #fff;
  border-top-right-radius: 4px;
}

.ai-msg.ai .ai-msg-text {
  background: #fff;
  color: var(--text-primary);
  border-top-left-radius: 4px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
}

.ai-input-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  padding-bottom: max(14px, env(safe-area-inset-bottom));
  border-top: 1px solid rgba(0,0,0,0.06);
  flex-shrink: 0;
}

.ai-input {
  flex: 1;
  height: 42px;
  border-radius: 21px;
  border: 1.5px solid var(--border);
  padding: 0 16px;
  font-size: 13px;
  color: var(--text-primary);
  background: #fff;
  outline: none;
}

.ai-input:focus { border-color: var(--primary); }

.ai-send-btn {
  width: 42px; height: 42px;
  border-radius: 50%;
  background: var(--gradient-primary);
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 3px 12px rgba(255,122,51,0.25);
}

.ai-send-btn:disabled { opacity: 0.4; }
.ai-send-btn:not(:disabled):active { transform: scale(0.9); }
</style>
