<template>
  <div class="custom-page">
    <!-- AI 智能卡片 -->
    <div class="ai-hero-card" @click="openAiChat">
      <div class="ai-hero-content">
        <div class="ai-icon-ring">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="none">
            <path d="M5 12h14v4a2 2 0 01-2 2H7a2 2 0 01-2-2v-4z" fill="#fff" fill-opacity="0.92"/>
            <rect x="4" y="11" width="16" height="2" rx="1" fill="#fff" fill-opacity="0.95"/>
            <!-- 蒸汽 -->
            <path class="steam steam-1" d="M8.5 10c-0.4-1.8 1-2.9 0.6-4.8" stroke="#fff" stroke-width="1.6" stroke-linecap="round"/>
            <path class="steam steam-2" d="M12 10V5.4" stroke="#fff" stroke-width="1.6" stroke-linecap="round"/>
            <path class="steam steam-3" d="M15.5 10c0.4-1.8-1-2.9-0.6-4.8" stroke="#fff" stroke-width="1.6" stroke-linecap="round"/>
          </svg>
        </div>
        <div class="ai-hero-text">
          <div class="ai-hero-greeting">你好，我是你的"烹饪助手"~</div>
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
      <span class="tab" :class="{ active: activeTab === 'recipe' }" @click="activeTab = 'recipe'">定制食谱</span>
      <span class="tab" :class="{ active: activeTab === 'menu' }" @click="activeTab = 'menu'">一键菜谱</span>
      <span class="tab" :class="{ active: activeTab === 'fridge' }" @click="activeTab = 'fridge'">食材管理</span>
    </div>

    <!-- Tab: 我的食谱 -->
    <div v-if="activeTab === 'recipe'" class="tab-content">
      <div v-if="recipeRecords.length === 0" class="empty-state">
        <div class="empty-icon">&#x1F4D6;</div>
        <div class="empty-text">还没有定制食谱记录</div>
        <div class="empty-sub">点击上方烹饪助手定制食谱功能</div>
      </div>
      <div v-else class="record-list">
        <div v-for="rec in recipeRecords" :key="rec.id" class="record-card" :class="{ 'weekly-card': isWeekly(rec) }" @click="openDetail(rec)">
          <div class="card-top">
            <span class="card-icon">{{ isWeekly(rec) ? '📅' : '🍲' }}</span>
            <div class="card-info">
              <div class="record-title">{{ rec.title }}</div>
              <div class="card-meta" v-if="rec.cookTime || rec.difficulty || rec.ingredients">
                <span v-if="rec.cookTime" class="meta-tag">&#x23F2; {{ rec.cookTime }}</span>
                <span v-if="rec.difficulty" class="meta-tag">&#x1F3AF; {{ rec.difficulty }}</span>
                <span v-if="rec.ingredients" class="meta-tag">&#x1F96C; {{ ingredientCount(rec.ingredients) }}种食材</span>
              </div>
            </div>
          </div>
          <div class="record-time">{{ formatTime(rec.createTime) }}</div>
        </div>
      </div>
    </div>

    <!-- Tab: 一键菜谱 -->
    <div v-if="activeTab === 'menu'" class="tab-content">
      <div v-if="menuRecords.length === 0" class="empty-state">
        <div class="empty-icon">&#x1F4CB;</div>
        <div class="empty-text">还没有一键菜谱记录</div>
        <div class="empty-sub">点击上方烹饪助手一键菜谱功能</div>
      </div>
      <div v-else class="record-list">
        <div v-for="rec in menuRecords" :key="rec.id" class="record-card" @click="openDetail(rec)">
          <div class="card-top">
            <span class="card-icon">&#x1F372;</span>
            <div class="card-info">
              <div class="record-title">{{ rec.title }}</div>
              <div class="card-meta" v-if="rec.cookTime || rec.difficulty || rec.ingredients">
                <span v-if="rec.cookTime" class="meta-tag">&#x23F2; {{ rec.cookTime }}</span>
                <span v-if="rec.difficulty" class="meta-tag">&#x1F3AF; {{ rec.difficulty }}</span>
                <span v-if="rec.ingredients" class="meta-tag">&#x1F96C; {{ ingredientCount(rec.ingredients) }}种食材</span>
              </div>
            </div>
          </div>
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
        <div class="stat-card stat-danger">
          <div class="stat-num">{{ stats.expired }}</div>
          <div class="stat-label">已过期</div>
        </div>
      </div>

      <div class="category-scroll" ref="catScroll"
           @mousedown="onCatDragStart" @mousemove="onCatDragMove" @mouseup="onCatDragEnd" @mouseleave="onCatDragEnd"
           @touchstart="onCatDragStart" @touchmove="onCatDragMove" @touchend="onCatDragEnd">
        <span v-for="cat in ['全部', ...categories]" :key="cat"
              class="filter-chip" :class="{ active: activeCat === cat }"
              @click="selectCat(cat)">{{ cat }}</span>
      </div>

      <div v-if="filteredIngredients.length === 0" class="empty-state small">
        <div class="empty-text">暂无食材记录</div>
        <div class="empty-sub">点击上方烹饪助手食材管理功能</div>
      </div>
      <div v-else class="ingredient-list">
        <div v-for="ing in filteredIngredients" :key="ing.ingredientId" class="ingredient-row"
             :class="{ expired: ing.expired, near: ing.nearExpiry && !ing.expired }">
          <div class="ing-icon">{{ getIngIcon(ing.category) }}</div>
          <div class="ing-info">
            <div class="ing-name">
              {{ ing.name }}
              <span v-if="ing.expired" class="ing-badge badge-expired">已过期</span>
              <span v-else-if="ing.nearExpiry" class="ing-badge badge-near">临期</span>
            </div>
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

    <!-- 记录详情弹窗（菜谱样式） -->
    <teleport to="body">
      <div v-if="detailRecord" class="detail-overlay" @click="detailRecord = null">
        <div class="detail-card" @click.stop>
          <div class="detail-header">
            <span class="detail-title">{{ detailRecord.title }}</span>
            <span class="detail-close" @click="detailRecord = null">&times;</span>
          </div>
          <div class="detail-body">
            <!-- 7天完整方案 -->
            <template v-if="parsedRecipe.isWeekly">
              <div v-if="parsedRecipe.summary" class="weekly-summary">{{ parsedRecipe.summary }}</div>

              <div v-for="(day, di) in parsedRecipe.days" :key="di" class="day-folder">
                <div class="day-folder-header" @click="expandedDays.has(di) ? expandedDays.delete(di) : expandedDays.add(di); expandedDays = new Set(expandedDays)">
                  <span class="day-num">第{{ di + 1 }}天</span>
                  <span class="day-label">{{ day.label.replace(/^第[一二三四五六七]+天[，、\s]*/, '') }}</span>
                  <span class="day-arrow" :class="{ open: expandedDays.has(di) }">›</span>
                </div>
                <div v-if="expandedDays.has(di)" class="day-meals">
                  <div v-for="(meal, mi) in day.meals" :key="mi" class="meal-card">
                    <div class="meal-header">
                      <span class="meal-type">{{ meal.type }}</span>
                      <span class="meal-name">{{ meal.name }}</span>
                    </div>
                    <div class="meal-body">
                      <div v-if="meal.ingredients" class="meal-row">
                        <span class="meal-label">食材</span>
                        <span class="meal-value">{{ meal.ingredients }}</span>
                      </div>
                      <div v-if="meal.instructions" class="meal-row">
                        <span class="meal-label">做法</span>
                        <span class="meal-value">{{ meal.instructions }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div v-if="parsedRecipe.shoppingList" class="rp-section" style="margin-top:16px">
                <div class="rp-section-title">一周采购清单</div>
                <div class="rp-raw">{{ parsedRecipe.shoppingList }}</div>
              </div>
              <div v-if="parsedRecipe.tips" class="rp-section">
                <div class="rp-section-title">营养建议</div>
                <div class="rp-raw">{{ parsedRecipe.tips }}</div>
              </div>
            </template>

            <!-- 单道菜谱 -->
            <template v-else>
              <!-- 菜品简介 -->
              <div v-if="parsedRecipe.description" class="rp-desc">{{ parsedRecipe.description }}</div>

              <!-- 基本信息 -->
              <div v-if="parsedRecipe.infoItems.length" class="rp-section">
                <div class="rp-section-title">基本信息</div>
                <div class="rp-info-grid">
                  <div v-for="item in parsedRecipe.infoItems" :key="item.label" class="rp-info-item">
                    <span class="rp-info-label">{{ item.label }}</span>
                    <span class="rp-info-value">{{ item.value }}</span>
                  </div>
                </div>
              </div>

              <!-- 食材清单 -->
              <div v-if="parsedRecipe.ingredients.length" class="rp-section">
                <div class="rp-section-title">食材清单</div>
                <div class="rp-ingredients">
                  <div v-for="(ing, i) in parsedRecipe.ingredients" :key="i" class="rp-ing-item">
                    <span class="rp-ing-dot"></span>
                    <span class="rp-ing-name">{{ ing.name }}</span>
                    <span class="rp-ing-amount">{{ ing.amount }}</span>
                  </div>
                </div>
              </div>

              <!-- 烹饪步骤 -->
              <div v-if="parsedRecipe.steps.length" class="rp-section">
                <div class="rp-section-title">烹饪步骤</div>
                <div class="rp-steps">
                  <div v-for="(step, i) in parsedRecipe.steps" :key="i" class="rp-step-item">
                    <div class="rp-step-num">{{ i + 1 }}</div>
                    <div class="rp-step-text">{{ step }}</div>
                  </div>
                </div>
              </div>

              <!-- 小贴士 -->
              <div v-if="parsedRecipe.tips.length" class="rp-section">
                <div class="rp-section-title">小贴士</div>
                <div class="rp-tips">
                  <div v-for="(tip, i) in parsedRecipe.tips" :key="i" class="rp-tip-item">
                    <span class="rp-tip-bulb">&#x1F4A1;</span>
                    <span>{{ tip }}</span>
                  </div>
                </div>
              </div>

              <!-- 兜底：老数据没有结构化格式，显示原文 -->
              <div v-if="!parsedRecipe.hasStructure" class="rp-raw">{{ detailRecord.content }}</div>
            </template>
          </div>
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
                <div class="ai-welcome-title">你好，我是你的"烹饪助手"~</div>
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
          <div v-if="!speech.supported.value && currentAiMode" class="ai-mic-hint">当前浏览器不支持语音输入，请使用 Chrome 或 Edge</div>
          <div v-if="speech.error.value" class="ai-mic-hint ai-mic-error">{{ speech.error.value }}</div>
          <div class="ai-input-row" :class="{ recording: speech.isListening.value || speech.isRecognizing.value }">
            <button
              class="ai-mic-btn"
              :class="{ on: speech.isListening.value, recognizing: speech.isRecognizing.value }"
              @click="toggleMic"
              :disabled="aiStreaming || !currentAiMode || speech.isRecognizing.value"
              :title="speech.isListening.value ? '点击停止' : speech.isRecognizing.value ? '识别中...' : '语音输入'"
            >
              <svg v-show="!speech.isListening.value && !speech.isRecognizing.value" viewBox="0 0 24 24" width="18" height="18" key="mic-idle">
                <path d="M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z" :fill="(aiStreaming || !currentAiMode) ? '#D4CBC1' : '#8B7B6B'"/>
                <path d="M19 10v2a7 7 0 0 1-6 6.92V21h3v2H8v-2h3v-2.08A7 7 0 0 1 5 12v-2h2v2a5 5 0 0 0 10 0v-2z" :fill="(aiStreaming || !currentAiMode) ? '#D4CBC1' : '#8B7B6B'"/>
              </svg>
              <svg v-show="speech.isListening.value" viewBox="0 0 24 24" width="18" height="18" class="ai-pulse-icon" key="mic-recording">
                <circle cx="12" cy="12" r="11" fill="none" stroke="#E84518" stroke-width="1.5" opacity="0.3"/>
                <circle cx="12" cy="12" r="7" fill="#E84518" opacity="0.15"/>
                <path d="M12 3a3 3 0 0 0-3 3v6a3 3 0 0 0 6 0V6a3 3 0 0 0-3-3z" fill="#E84518"/>
                <path d="M17 10v2a5 5 0 0 1-10 0v-2" stroke="#E84518" stroke-width="2" fill="none" stroke-linecap="round"/>
              </svg>
              <svg v-show="speech.isRecognizing.value" viewBox="0 0 24 24" width="18" height="18" class="ai-spin-icon" key="mic-spinner">
                <circle cx="12" cy="12" r="10" fill="none" stroke="#8B7B6B" stroke-width="2" stroke-dasharray="32 32" stroke-linecap="round"/>
              </svg>
            </button>
            <input v-model="aiInput" class="ai-input" :placeholder="speech.isListening.value ? '正在聆听...' : speech.isRecognizing.value ? '识别中...' : aiPlaceholder"
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

    <!-- 底部导航 -->
    <AppTabbar />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { userStore } from '../store/user'
import { listCustomRecords, deleteCustomRecord } from '../api/custom'
import { listIngredients, deleteIngredient, getIngredientStats } from '../api/ingredient'
import { streamChat } from '../api/ai'
import { useSpeechRecognition } from '../composables/useSpeechRecognition'
import AppTabbar from '../components/AppTabbar.vue'

const router = useRouter()

const activeTab = ref('recipe')
const categories = ['蔬菜', '生禽', '蛋类', '水产', '豆制品', '其他']

const recipeRecords = ref([])
const menuRecords = ref([])

const ingredients = ref([])
const stats = ref({ total: 0, nearExpiry: 0 })
const activeCat = ref('全部')
const catScroll = ref(null)
let catDragging = false, catStartX = 0, catScrollLeft = 0, catMoved = false

function onCatDragStart(e) {
  catDragging = true
  catMoved = false
  catStartX = (e.touches ? e.touches[0].pageX : e.pageX) - catScroll.value.offsetLeft
  catScrollLeft = catScroll.value.scrollLeft
}

function onCatDragMove(e) {
  if (!catDragging) return
  const x = (e.touches ? e.touches[0].pageX : e.pageX) - catScroll.value.offsetLeft
  const dist = Math.abs(x - catStartX)
  if (dist > 5) catMoved = true
  if (catMoved) e.preventDefault()
  catScroll.value.scrollLeft = catScrollLeft - (x - catStartX) * 1.5
}

function onCatDragEnd() {
  catDragging = false
}

function selectCat(cat) {
  if (catMoved) return
  activeCat.value = cat
}

const detailRecord = ref(null)
const expandedDays = ref(new Set([0]))

const showAiOverlay = ref(false)
const aiMessages = ref([])
const aiStreaming = ref(false)
const aiStreamingText = ref('')
const aiInput = ref('')
const aiMsgList = ref(null)
const currentAiMode = ref(null)
let cancelStream = null

const speech = useSpeechRecognition()

// No need to watch speech text — stop() returns recognized text directly
// (interim results are not available with MediaRecorder approach)

async function toggleMic() {
  if (speech.isListening.value) {
    const text = await speech.stop()
    if (text) {
      aiInput.value = text
    }
  } else {
    speech.reset()
    speech.start()
  }
}

const filteredIngredients = computed(() => {
  if (activeCat.value === '全部') return ingredients.value
  return ingredients.value.filter(i => i.category === activeCat.value)
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

onMounted(() => {
  const uid = userStore.user?.userId
  if (!uid) return
  loadRecords(uid)
  loadIngredients(uid)
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
    ingredients.value = list.map(ing => {
      const days = calcDaysUntil(ing.expireDate)
      return {
        ...ing,
        expired: days != null && days < 0,
        nearExpiry: days != null && days >= 0 && days <= 1
      }
    })
    stats.value = statsRes.data || { total: 0, nearExpiry: 0, expired: 0 }
  } catch { /* ignore */ }
}

function getPreview(content) {
  if (!content) return ''
  // 提取菜品简介段落（# 标题之后、## 第一个章节之前的内容）
  const lines = content.split('\n')
  let inDesc = false
  let descParts = []
  for (const line of lines) {
    const trimmed = line.trim()
    if (!trimmed) continue
    if (trimmed.startsWith('# ') && !inDesc) {
      inDesc = true
      continue
    }
    if (trimmed.startsWith('## ')) break
    if (inDesc && !trimmed.startsWith('#')) {
      descParts.push(trimmed)
      if (descParts.join(' ').length > 80) break
    }
  }
  if (descParts.length) {
    const desc = descParts.join(' ').replace(/\*\*/g, '').trim()
    return desc.length > 100 ? desc.substring(0, 100) + '...' : desc
  }
  // 兜底：老数据没有结构化格式
  const text = content.replace(/#{1,6}\s/g, '').replace(/\*\*/g, '').trim()
  return text.length > 100 ? text.substring(0, 100) + '...' : text
}

function parseRecipeMarkdown(content) {
  if (!content) return { hasStructure: false }
  // 检测7天完整方案（有多天标题 + 早餐/午餐/晚餐结构）
  if (/##\s*第一天/.test(content) && /###\s*(早餐|午餐|晚餐)/.test(content)) {
    return parseWeeklyFull(content)
  }
  const lines = content.split('\n')
  let title = ''
  let description = ''
  const infoItems = []
  const ingredients = []
  const steps = []
  const tips = []
  let currentSection = null

  // 判断是否为标题行
  function isHeading(line) {
    return /^#{1,3}\s/.test(line)
  }

  for (const line of lines) {
    const trimmed = line.trim()
    if (!trimmed) continue

    // 主标题（菜名）——第一个 # 或 ## 开头的行
    if (isHeading(trimmed) && !title && currentSection === null) {
      title = trimmed.replace(/^#{1,3}\s/, '').trim()
      continue
    }

    // 识别章节标题（仅在 #/## 开头的行中匹配，避免正文中的关键词误触发）
    if (isHeading(trimmed)) {
      if (trimmed.includes('菜品简介') || trimmed.includes('简介')) {
        currentSection = 'description'
        continue
      }
      if (trimmed.includes('基本信息')) {
        currentSection = 'info'
        continue
      }
      if (trimmed.includes('食材清单') || trimmed.includes('食材')) {
        currentSection = 'ingredients'
        continue
      }
      if (trimmed.includes('烹饪步骤') || trimmed.includes('制作步骤')) {
        currentSection = 'steps'
        continue
      }
      if (trimmed.includes('小贴士') || trimmed.includes('贴士') || trimmed.includes('提示')) {
        currentSection = 'tips'
        continue
      }
      // 其他标题行（如 ## 备注 等），跳过
      continue
    }

    // 按当前章节解析内容
    switch (currentSection) {
      case 'description':
        description += (description ? ' ' : '') + trimmed
        break
      case 'info': {
        const m = trimmed.match(/^[-*•]\s*(.+?)[：:]\s*(.+)/)
        if (m) infoItems.push({ label: m[1].replace(/\*\*/g, '').trim(), value: m[2].replace(/\*\*/g, '').trim() })
        break
      }
      case 'ingredients': {
        const m = trimmed.match(/^[-*•]\s*(?:\*\*)?(.+?)(?:\*\*)?[：:]\s*(.+)/)
        if (m) ingredients.push({ name: m[1].trim(), amount: m[2].trim() })
        break
      }
      case 'steps': {
        const m = trimmed.match(/^\d+[.)]\s*(.+)/)
        if (m) steps.push(m[1].trim())
        break
      }
      case 'tips': {
        const m = trimmed.match(/^[-*•]\s*(.+)/)
        if (m) tips.push(m[1].trim())
        break
      }
    }
  }

  const hasStructure = !!(title || ingredients.length || steps.length)
  return { hasStructure, title, description, infoItems, ingredients, steps, tips }
}

/** 解析完整7天方案 */
function parseWeeklyFull(content) {
  const lines = content.split('\n')
  let title = ''
  let summary = ''
  const days = []
  let shoppingList = ''
  let tips = ''
  let currentDay = null
  let currentMeal = null
  let currentField = null
  let inShopping = false
  let inTips = false

  for (const line of lines) {
    const trimmed = line.trim()
    if (!trimmed) continue

    // 主标题
    if (trimmed.startsWith('# ') && !title) {
      title = trimmed.replace(/^#\s*/, '').trim()
      continue
    }

    // 用户需求总结
    if (trimmed.startsWith('# ') && trimmed.includes('总结')) {
      currentDay = null; currentMeal = null
      continue
    }

    // 一周食材采购清单
    if (trimmed.includes('采购清单') || trimmed.includes('购物清单')) {
      if (currentMeal && currentDay) currentDay.meals.push(currentMeal)
      if (currentDay) days.push(currentDay)
      inShopping = true; inTips = false
      currentDay = null; currentMeal = null
      continue
    }

    // 一周营养建议
    if (trimmed.includes('营养建议') || trimmed.includes('营养贴士') || trimmed.includes('小贴士')) {
      if (currentMeal && currentDay) currentDay.meals.push(currentMeal)
      if (currentDay) days.push(currentDay)
      inTips = true; inShopping = false
      currentDay = null; currentMeal = null
      continue
    }

    // 天标题：## 第一天（周一）
    const dayMatch = trimmed.match(/^##\s*(第[一二三四五六七天]+天[^#]*)/)
    if (dayMatch) {
      if (currentMeal && currentDay) currentDay.meals.push(currentMeal)
      if (currentDay) days.push(currentDay)
      currentDay = { label: dayMatch[1].trim(), meals: [] }
      currentMeal = null
      inShopping = false; inTips = false
      continue
    }

    // 餐标题：### 早餐：菜名
    const mealMatch = trimmed.match(/^###\s*(早餐|午餐|晚餐)[：:]\s*(.+)/)
    if (mealMatch && currentDay) {
      if (currentMeal) currentDay.meals.push(currentMeal)
      currentMeal = { type: mealMatch[1], name: mealMatch[2], ingredients: '', instructions: '' }
      currentField = null
      continue
    }

    // 采购清单 / 营养建议内容
    if (inShopping) {
      shoppingList += (shoppingList ? '\n' : '') + trimmed
      continue
    }
    if (inTips) {
      tips += (tips ? '\n' : '') + trimmed
      continue
    }

    // 总结内容（# 用户需求总结 之后的正文）
    if (!currentDay && !currentMeal && !inShopping && !inTips && !trimmed.startsWith('#')) {
      summary += (summary ? ' ' : '') + trimmed
      continue
    }

    if (!currentMeal) continue

    // 列表项：- 主要食材：... / - 做法简述：...
    const listMatch = trimmed.match(/^[-*•]\s*(.+)/)
    if (listMatch) {
      const item = listMatch[1].trim()
      if (item.startsWith('主要食材') || item.startsWith('食材')) {
        currentMeal.ingredients = item.replace(/^主要食材[：:]?\s*/, '').replace(/^食材[：:]?\s*/, '')
        currentField = 'ingredients'
      } else if (item.startsWith('做法简述') || item.startsWith('做法') || item.startsWith('简述')) {
        currentMeal.instructions = item.replace(/^做法简述[：:]?\s*/, '').replace(/^做法[：:]?\s*/, '').replace(/^简述[：:]?\s*/, '')
        currentField = 'instructions'
      }
      continue
    }

    // 补充文本追加到当前字段
    if (currentField === 'instructions') {
      currentMeal.instructions += ' ' + trimmed
    }
  }

  if (currentMeal && currentDay) currentDay.meals.push(currentMeal)
  if (currentDay) days.push(currentDay)

  return {
    hasStructure: true,
    isWeekly: true,
    title,
    summary: summary.length > 150 ? summary.substring(0, 150) + '...' : summary,
    days,
    shoppingList,
    tips
  }
}

function parseJsonField(field) {
  if (!field) return []
  if (typeof field === 'object') return field
  try { return JSON.parse(field) } catch { return [] }
}

function ingredientCount(field) {
  if (!field) return 0
  try {
    const arr = typeof field === 'string' ? JSON.parse(field) : field
    return Array.isArray(arr) ? arr.length : 0
  } catch { return 0 }
}

const parsedRecipe = computed(() => {
  if (!detailRecord.value) return { hasStructure: false }
  const rec = detailRecord.value
  // 7天方案：总是从 markdown 解析
  if (isWeekly(rec)) {
    return parseRecipeMarkdown(rec.content)
  }
  // 新记录：优先使用结构化字段
  if (rec.ingredients || rec.steps) {
    const infoItems = []
    if (rec.cookTime) infoItems.push({ label: '烹饪时长', value: rec.cookTime })
    if (rec.difficulty) infoItems.push({ label: '难度等级', value: rec.difficulty })
    return {
      hasStructure: true,
      title: rec.title,
      description: rec.description || '',
      infoItems,
      ingredients: parseJsonField(rec.ingredients),
      steps: parseJsonField(rec.steps),
      tips: []
    }
  }
  // 老记录：回退到 markdown 解析
  return parseRecipeMarkdown(rec.content)
})

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

function calcDaysUntil(expireDate) {
  if (!expireDate) return null
  let d
  if (Array.isArray(expireDate)) {
    d = new Date(expireDate[0], expireDate[1] - 1, expireDate[2], expireDate[3] || 0, expireDate[4] || 0)
  } else {
    d = new Date(expireDate)
  }
  if (isNaN(d.getTime())) return null
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  d.setHours(0, 0, 0, 0)
  return Math.round((d - today) / (1000 * 60 * 60 * 24))
}

function formatExpiry(ing) {
  const days = calcDaysUntil(ing.expireDate)
  if (days == null || days > 36500) return '未知'
  if (days < -365) return '数据异常'
  if (days < 0) return `已过期${Math.abs(days)}天`
  if (days === 0) return '今天到期'
  if (days <= 1) return `剩余${days}天`
  if (days > 365) return '数据异常'
  return `剩余${days}天`
}

function getIngIcon(cat) {
  const map = { '蔬菜': '🥬', '生禽': '🥩', '蛋类': '🥚', '水产': '🐟', '豆制品': '🫘', '其他': '📦' }
  return map[cat] || '📦'
}

function openDetail(rec) { detailRecord.value = rec; expandedDays.value = new Set([0]) }

function isWeekly(rec) {
  if (rec.title && rec.title.includes('7天')) return true
  // 兜底：通过内容结构判断（标题提取可能遗漏"7天"）
  if (rec.content && /##\s*第一天/.test(rec.content) && /###\s*(早餐|午餐|晚餐)/.test(rec.content)) return true
  return false
}

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
  padding-top: calc(14px + env(safe-area-inset-top, 0px));
  padding-bottom: 80px;
}

/* ===== AI 智能卡片 ===== */
.ai-hero-card {
  position: relative;
  margin: 14px 14px 0;
  background: linear-gradient(120deg, rgba(255,249,243,0.94), rgba(255,238,224,0.94), rgba(255,249,243,0.94), rgba(255,242,230,0.94));
  background-size: 300% 300%;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 24px 18px;
  cursor: pointer;
  border: 1px solid rgba(255,140,90,0.28);
  box-shadow: 0 4px 24px rgba(255,122,51,0.12), 0 1px 4px rgba(0,0,0,0.04);
  transition: all 0.3s;
  overflow: hidden;
  animation: heroCardBgFlow 8s ease-in-out infinite, heroCardBreath 3.5s ease-in-out infinite;
}

/* 卡片扫光 */
.ai-hero-card::after {
  content: '';
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  width: 30%;
  background: linear-gradient(105deg, transparent, rgba(255,255,255,0.22) 50%, transparent);
  transform: translateX(-160%) skewX(-18deg);
  animation: heroShine 4.5s ease-in-out infinite;
  pointer-events: none;
  border-radius: inherit;
}

.ai-hero-card:active { transform: scale(0.985); }

@keyframes heroCardBgFlow {
  0%, 100% { background-position: 0% 50%; }
  50%      { background-position: 100% 50%; }
}

@keyframes heroCardBreath {
  0%, 100% { box-shadow: 0 4px 24px rgba(255,122,51,0.12), 0 1px 4px rgba(0,0,0,0.04); }
  50%      { box-shadow: 0 8px 34px rgba(255,122,51,0.30), 0 2px 12px rgba(0,0,0,0.07); }
}

@keyframes heroShine {
  0%, 55% { transform: translateX(-160%) skewX(-18deg); }
  100%    { transform: translateX(600%) skewX(-18deg); }
}

.ai-hero-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

/* 图标环：整体呼吸缩放 + 旋转光环 + 外圈光晕 */
.ai-icon-ring {
  position: relative;
  width: 56px; height: 56px;
  border-radius: 50%;
  margin-bottom: 14px;
  background: linear-gradient(135deg, rgba(255,122,51,0.32), rgba(255,160,100,0.24));
  border: 1.5px solid rgba(255,122,51,0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  animation: iconRingBreath 3.5s ease-in-out infinite;
}

.ai-icon-ring::before {
  content: '';
  position: absolute;
  inset: -5px;
  border-radius: 50%;
  background: conic-gradient(from 0deg,
    transparent 0deg,
    rgba(255,150,90,0.95) 50deg,
    transparent 120deg,
    transparent 200deg,
    rgba(255,200,150,0.70) 260deg,
    transparent 340deg);
  -webkit-mask: radial-gradient(farthest-side, transparent calc(100% - 2.5px), #000 calc(100% - 2px));
  mask: radial-gradient(farthest-side, transparent calc(100% - 2.5px), #000 calc(100% - 2px));
  animation: iconRingSpin 3.2s linear infinite;
  pointer-events: none;
  z-index: 0;
}

/* 图标外圈呼吸光晕 */
.ai-icon-ring::after {
  content: '';
  position: absolute;
  inset: -6px;
  border-radius: 50%;
  background: radial-gradient(circle at center, rgba(255,150,80,0.42), transparent 70%);
  z-index: 0;
  animation: iconRingGlow 2.6s ease-in-out infinite;
  pointer-events: none;
}

.ai-icon-ring svg {
  position: relative;
  z-index: 1;
}

@keyframes iconRingBreath {
  0%, 100% { transform: scale(1); }
  50%      { transform: scale(1.06); }
}

@keyframes iconRingSpin {
  to { transform: rotate(360deg); }
}

@keyframes iconRingGlow {
  0%, 100% { opacity: 0.35; transform: scale(0.9); }
  50%      { opacity: 1; transform: scale(1.12); }
}

/* 锅上升腾的蒸汽 */
.steam {
  transform-box: fill-box;
  transform-origin: center bottom;
  filter: drop-shadow(0 0 2px rgba(255,255,255,0.9));
}

.steam-1 { animation: steamRise 2.2s ease-in-out infinite; }
.steam-2 { animation: steamRise 2.2s ease-in-out 0.4s infinite; }
.steam-3 { animation: steamRise 2.2s ease-in-out 0.8s infinite; }

@keyframes steamRise {
  0%   { transform: translateY(1px) scale(0.75); opacity: 0; }
  30%  { opacity: 0.95; }
  60%  { opacity: 0.7; }
  100% { transform: translateY(-6px) scale(1.15); opacity: 0; }
}

.ai-hero-text { margin-bottom: 12px; }

.ai-hero-greeting {
  font-size: 17px;
  font-weight: 700;
  letter-spacing: 0.5px;
  margin-bottom: 4px;
  background: linear-gradient(90deg, #5C3520, #C06535, #5C3520);
  background-size: 200% 100%;
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  color: transparent;
  animation: heroTitleShimmer 4.5s ease-in-out infinite;
}

@keyframes heroTitleShimmer {
  0%, 100% { background-position: 0% 50%; }
  50%      { background-position: 100% 50%; }
}

.ai-hero-sub {
  font-size: 13px;
  color: #B08868;
}

.ai-sparkle-row {
  display: flex;
  justify-content: center;
  gap: 6px;
}

.ai-sparkle {
  width: 4px; height: 4px;
  border-radius: 50%;
  background: #FF9A5C;
  animation: sparklePulse 1.8s ease-in-out infinite;
  box-shadow: 0 0 6px rgba(255,122,51,0.4);
}

.ai-sparkle.delay-1 { animation-delay: 0.3s; background: #FFB088; box-shadow: 0 0 6px rgba(255,160,100,0.4); }
.ai-sparkle.delay-2 { animation-delay: 0.6s; background: #FFC8A0; box-shadow: 0 0 6px rgba(255,180,130,0.4); }

@keyframes sparklePulse {
  0%, 100% { transform: scale(1); opacity: 0.3; }
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
.stat-warn .stat-num { color: #E8A820; }
.stat-danger .stat-num { color: #D1523F; }

/* 分类拖拽滚动 */
.category-scroll {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding: 4px 0 12px;
  margin-bottom: 8px;
  -webkit-overflow-scrolling: touch;
  cursor: grab;
  scroll-behavior: smooth;
  user-select: none;
  -webkit-user-select: none;
}

.category-scroll:active { cursor: grabbing; }

.category-scroll::-webkit-scrollbar { display: none; }

.filter-chip {
  padding: 8px 18px;
  border-radius: 22px;
  font-size: 13px;
  font-weight: 700;
  color: var(--text-secondary);
  background: #fff;
  cursor: pointer;
  border: 1.5px solid var(--border);
  transition: all 0.25s;
  white-space: nowrap;
  flex-shrink: 0;
  box-shadow: 0 1px 4px rgba(30,21,15,0.03);
}

.filter-chip:active { transform: scale(0.95); }

.filter-chip.active {
  background: var(--gradient-primary);
  color: #fff;
  border-color: transparent;
  box-shadow: 0 3px 12px rgba(255,122,51,0.25);
  transform: scale(1.05);
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
  transition: all 0.25s;
}

.ingredient-row.near {
  border-left-color: #E8A820;
  background: linear-gradient(135deg, #FFFDF5, #FFF8E8);
  box-shadow: 0 2px 8px rgba(232, 168, 32, 0.12);
  animation: nearPulse 2.5s ease-in-out infinite;
}

.ingredient-row.expired {
  border-left-color: #D1523F;
  background: #FFF7F5;
  opacity: 0.85;
}

@keyframes nearPulse {
  0%, 100% { box-shadow: 0 2px 8px rgba(232, 168, 32, 0.08); }
  50% { box-shadow: 0 2px 16px rgba(232, 168, 32, 0.20); }
}

.ing-icon { font-size: 24px; flex-shrink: 0; }
.ing-info { flex: 1; min-width: 0; }
.ing-name { font-size: 14px; font-weight: 700; color: var(--text-primary); display: flex; align-items: center; gap: 8px; }
.ing-meta { font-size: 11px; color: var(--text-muted); margin-top: 2px; }

.ing-badge {
  font-size: 10px;
  font-weight: 800;
  padding: 2px 8px;
  border-radius: 10px;
  letter-spacing: 0.5px;
  white-space: nowrap;
}

.badge-near {
  background: #FFF3D6;
  color: #B87A14;
  border: 1px solid #F5D78A;
}

.badge-expired {
  background: #FFE8E4;
  color: #C0392B;
  border: 1px solid #F5B8A8;
}

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

.card-top {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.card-icon {
  font-size: 28px;
  flex-shrink: 0;
  line-height: 1;
}

.card-info {
  flex: 1;
  min-width: 0;
}

.record-title {
  font-size: 15px; font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 6px;
}

.card-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.meta-tag {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  font-size: 11px;
  font-weight: 600;
  color: #8B7355;
  background: #FBF6EF;
  padding: 2px 8px;
  border-radius: 10px;
  border: 1px solid #F0E4D5;
}

.record-time { font-size: 11px; color: var(--text-placeholder); margin-top: 10px; }

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
  max-width: 500px;
  max-height: 85vh;
  background: #FDFAF5;
  border-radius: 20px 20px 0 0;
  padding: 0;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 20px 0;
  flex-shrink: 0;
}

.detail-title { font-size: 19px; font-weight: 800; color: var(--text-primary); }

.detail-close {
  font-size: 28px; color: var(--text-muted); cursor: pointer;
  width: 32px; height: 32px; display: flex; align-items: center; justify-content: center;
  border-radius: 50%;
}

.detail-body { padding: 16px 20px; flex: 1; }

.detail-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px;
  border-top: 1px solid #EDE8DF;
  flex-shrink: 0;
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

/* ===== 菜谱详情样式 ===== */
.rp-desc {
  font-size: 14px; color: var(--text-secondary);
  line-height: 1.7;
  margin-bottom: 20px;
  padding: 12px 14px;
  background: #FFFBF5;
  border-radius: 10px;
  border-left: 3px solid #F5C34B;
}

.rp-section { margin-bottom: 20px; }

.rp-section-title {
  font-size: 15px; font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 10px;
  padding-bottom: 6px;
  border-bottom: 2px solid #F0E8D8;
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 基本信息 */
.rp-info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.rp-info-item {
  background: #fff;
  border-radius: 10px;
  padding: 10px 12px;
  display: flex;
  flex-direction: column;
  gap: 2px;
  border: 1px solid #F0E8D8;
}

.rp-info-label { font-size: 11px; color: var(--text-muted); }

.rp-info-value { font-size: 13px; font-weight: 700; color: var(--text-primary); }

/* 食材清单 */
.rp-ingredients {
  background: #fff;
  border-radius: 12px;
  padding: 4px 0;
  border: 1px solid #F0E8D8;
  overflow: hidden;
}

.rp-ing-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border-bottom: 1px solid #F7F3EB;
}

.rp-ing-item:last-child { border-bottom: none; }

.rp-ing-dot {
  width: 6px; height: 6px;
  border-radius: 50%;
  background: var(--primary);
  flex-shrink: 0;
}

.rp-ing-name { font-size: 13px; font-weight: 600; color: var(--text-primary); }
.rp-ing-amount { font-size: 12px; color: var(--text-muted); margin-left: auto; }

/* 烹饪步骤 */
.rp-step-item {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.rp-step-num {
  width: 26px; height: 26px;
  border-radius: 50%;
  background: var(--gradient-primary);
  color: #fff;
  font-size: 13px; font-weight: 800;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
  margin-top: 1px;
}

.rp-step-text {
  font-size: 13px; color: var(--text-secondary);
  line-height: 1.65;
  flex: 1;
  padding-top: 3px;
}

/* 小贴士 */
.rp-tips { display: flex; flex-direction: column; gap: 8px; }

.rp-tip-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 13px; color: var(--text-secondary);
  line-height: 1.55;
  background: #FFFBF0;
  border-radius: 10px;
  padding: 10px 12px;
  border: 1px solid #F5E6C8;
}

.rp-tip-bulb { flex-shrink: 0; font-size: 14px; }

/* 兜底原文 */
.rp-raw {
  font-size: 14px; color: var(--text-secondary);
  line-height: 1.75;
  white-space: pre-wrap;
}

/* ===== 7天方案 周文件夹样式 ===== */

.weekly-card {
  border-left: 4px solid var(--primary);
  background: linear-gradient(135deg, #FFFAF5, #FFF6F0);
}

.weekly-badge {
  display: inline-block;
  font-family: var(--font-heading);
  font-size: 11px;
  font-weight: 700;
  color: var(--primary);
  background: var(--primary-bg);
  padding: 2px 10px;
  border-radius: var(--radius-xs);
  margin-top: 4px;
}

.weekly-summary {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin-bottom: 14px;
  padding: 10px 14px;
  background: #FFFBF7;
  border-radius: var(--radius-sm);
  border-left: 3px solid var(--primary-lighter);
}

/* 天文件夹 */
.day-folder {
  margin-bottom: 8px;
  border-radius: var(--radius-md);
  overflow: hidden;
  background: #fff;
  border: 1px solid var(--border-light);
}

.day-folder-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 16px;
  cursor: pointer;
  user-select: none;
  transition: background 0.2s;
}

.day-folder-header:active {
  background: #FFFAF5;
}

.day-num {
  font-family: var(--font-heading);
  font-size: 13px;
  font-weight: 800;
  color: #fff;
  background: var(--gradient-primary);
  padding: 3px 10px;
  border-radius: var(--radius-xs);
  white-space: nowrap;
}

.day-label {
  flex: 1;
  font-family: var(--font-heading);
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary);
}

.day-arrow {
  font-size: 20px;
  color: var(--text-muted);
  transition: transform 0.3s;
  font-weight: 700;
}

.day-arrow.open {
  transform: rotate(90deg);
}

.day-meals {
  padding: 0 12px 12px;
}

/* ===== 三餐卡片 ===== */

.meal-card {
  background: #fff;
  border-radius: var(--radius-md);
  padding: 14px 16px;
  margin-bottom: 12px;
  box-shadow: var(--shadow-card);
  border: 1px solid rgba(255, 122, 51, 0.06);
}

.meal-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.meal-type {
  font-family: var(--font-heading);
  font-size: 11px;
  font-weight: 700;
  color: #fff;
  background: var(--gradient-primary);
  padding: 3px 10px;
  border-radius: var(--radius-xs);
  letter-spacing: 1px;
}

.meal-name {
  font-family: var(--font-heading);
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
}

.meal-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.meal-row {
  display: flex;
  gap: 10px;
  font-size: 13px;
  line-height: 1.6;
}

.meal-label {
  font-family: var(--font-heading);
  font-weight: 700;
  color: var(--primary);
  white-space: nowrap;
  flex-shrink: 0;
  min-width: 32px;
}

.meal-value {
  color: var(--text-secondary);
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
  max-width: 500px;
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

/* AI mic button */
.ai-mic-btn {
  width: 42px; height: 42px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
  transition: all 0.3s ease;
  background: #fff;
  border: 1.5px solid var(--border);
}

.ai-mic-btn:active {
  transform: scale(0.85);
}

.ai-mic-btn.on {
  background: rgba(232,69,24,0.06);
  border-color: #E84518;
}

.ai-pulse-icon {
  animation: aiMicPulse 1.2s ease-in-out infinite;
}

@keyframes aiMicPulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.7; transform: scale(1.08); }
}

.ai-spin-icon {
  animation: aiMicSpin 1s linear infinite;
}

@keyframes aiMicSpin {
  to { transform: rotate(360deg); }
}

.ai-mic-btn.recognizing {
  background: rgba(139, 123, 107, 0.06);
}

.ai-input-row.recording {
  background: transparent;
}

.ai-mic-hint {
  text-align: center;
  font-size: 11px;
  color: var(--text-muted);
  padding: 0 14px 2px;
}

.ai-mic-error {
  color: #D1523F;
}
</style>
