<template>
  <div class="page-wrapper menu-plan-page">
    <!-- 顶栏 -->
    <div class="top-bar">
      <div class="top-back" @click="$goBack()">
        <svg viewBox="0 0 24 24" width="24" height="24">
          <path d="M15 18l-6-6 6-6" stroke="#fff" stroke-width="2.5" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <div class="top-title">定制菜单</div>
      <div class="top-spacer"></div>
    </div>

    <!-- 日历 -->
    <div class="calendar-card">
      <div class="cal-header">
        <button class="cal-arrow" @click="prevMonth">
          <svg viewBox="0 0 24 24" width="18" height="18"><path d="M15 18l-6-6 6-6" stroke="#5a524c" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/></svg>
        </button>
        <div class="cal-title">{{ year }}年{{ month }}月</div>
        <button class="cal-arrow" @click="nextMonth">
          <svg viewBox="0 0 24 24" width="18" height="18"><path d="M9 18l6-6-6-6" stroke="#5a524c" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/></svg>
        </button>
      </div>
      <div class="cal-weekdays">
        <span v-for="d in weekDays" :key="d">{{ d }}</span>
      </div>
      <div class="cal-dates">
        <div
          v-for="(d, i) in visibleCalendarDays"
          :key="i"
          class="cal-day"
          :class="{
            empty: !d,
            today: d && isToday(d.dateStr),
            selected: d && isSelected(d.dateStr),
            'has-plan': d && plannedDates.has(d.dateStr)
          }"
          @click="d && selectDate(d.dateStr)"
        >
          <span class="cal-day-num">{{ d ? d.dayNum : '' }}</span>
          <span v-if="d && plannedDates.has(d.dateStr)" class="cal-dot"></span>
        </div>
      </div>
      <div class="cal-toggle" @click="showFullCalendar = !showFullCalendar">
        <span>{{ showFullCalendar ? '收起日历' : '展开日历' }}</span>
        <svg viewBox="0 0 24 24" width="14" height="14" :style="{ transform: showFullCalendar ? 'rotate(180deg)' : '' }">
          <path d="M6 9l6 6 6-6" stroke="#9b9085" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
    </div>

    <!-- 三餐区域 -->
    <div class="meals-area">
      <!-- 早餐 -->
      <div class="meal-section">
        <div class="meal-header">
          <div class="meal-label">
            <span class="meal-icon">&#x2600;</span>
            <span>早餐</span>
          </div>
          <button class="meal-add-btn" @click="openPicker('BREAKFAST')">
            <svg viewBox="0 0 24 24" width="16" height="16"><path d="M12 5v14M5 12h14" stroke="#fff" stroke-width="2.5" stroke-linecap="round"/></svg>
          </button>
        </div>
        <div class="meal-recipes" v-if="breakfastList.length > 0">
          <div v-for="item in breakfastList" :key="item.id" class="recipe-chip" @click="goDetail(item.recipeId)">
            <img v-if="getFirstImage(item.recipeCoverImages)" :src="getFirstImage(item.recipeCoverImages)" class="chip-img" />
            <span class="chip-title">{{ item.recipeTitle }}</span>
            <button class="chip-remove" @click.stop="removePlan(item.id)">
              <svg viewBox="0 0 24 24" width="10" height="10"><path d="M18 6L6 18M6 6l12 12" stroke="#9b9085" stroke-width="2" stroke-linecap="round"/></svg>
            </button>
          </div>
        </div>
        <div v-else class="meal-empty">点击 + 从收藏中添加菜谱</div>
      </div>

      <!-- 午餐 -->
      <div class="meal-section">
        <div class="meal-header">
          <div class="meal-label">
            <span class="meal-icon">&#x26C5;</span>
            <span>午餐</span>
          </div>
          <button class="meal-add-btn" @click="openPicker('LUNCH')">
            <svg viewBox="0 0 24 24" width="16" height="16"><path d="M12 5v14M5 12h14" stroke="#fff" stroke-width="2.5" stroke-linecap="round"/></svg>
          </button>
        </div>
        <div class="meal-recipes" v-if="lunchList.length > 0">
          <div v-for="item in lunchList" :key="item.id" class="recipe-chip" @click="goDetail(item.recipeId)">
            <img v-if="getFirstImage(item.recipeCoverImages)" :src="getFirstImage(item.recipeCoverImages)" class="chip-img" />
            <span class="chip-title">{{ item.recipeTitle }}</span>
            <button class="chip-remove" @click.stop="removePlan(item.id)">
              <svg viewBox="0 0 24 24" width="10" height="10"><path d="M18 6L6 18M6 6l12 12" stroke="#9b9085" stroke-width="2" stroke-linecap="round"/></svg>
            </button>
          </div>
        </div>
        <div v-else class="meal-empty">点击 + 从收藏中添加菜谱</div>
      </div>

      <!-- 晚餐 -->
      <div class="meal-section">
        <div class="meal-header">
          <div class="meal-label">
            <span class="meal-icon">&#x1F319;</span>
            <span>晚餐</span>
          </div>
          <button class="meal-add-btn" @click="openPicker('DINNER')">
            <svg viewBox="0 0 24 24" width="16" height="16"><path d="M12 5v14M5 12h14" stroke="#fff" stroke-width="2.5" stroke-linecap="round"/></svg>
          </button>
        </div>
        <div class="meal-recipes" v-if="dinnerList.length > 0">
          <div v-for="item in dinnerList" :key="item.id" class="recipe-chip" @click="goDetail(item.recipeId)">
            <img v-if="getFirstImage(item.recipeCoverImages)" :src="getFirstImage(item.recipeCoverImages)" class="chip-img" />
            <span class="chip-title">{{ item.recipeTitle }}</span>
            <button class="chip-remove" @click.stop="removePlan(item.id)">
              <svg viewBox="0 0 24 24" width="10" height="10"><path d="M18 6L6 18M6 6l12 12" stroke="#9b9085" stroke-width="2" stroke-linecap="round"/></svg>
            </button>
          </div>
        </div>
        <div v-else class="meal-empty">点击 + 从收藏中添加菜谱</div>
      </div>
    </div>

    <!-- 选菜谱弹窗 -->
    <div class="picker-overlay" v-if="showPicker" @click.self="showPicker = false">
      <div class="picker-sheet">
        <div class="picker-top">
          <div class="picker-title">
            为{{ mealTypeText }}添加菜谱
          </div>
          <button class="picker-close" @click="showPicker = false">
            <svg viewBox="0 0 24 24" width="20" height="20"><path d="M18 6L6 18M6 6l12 12" stroke="#5a524c" stroke-width="2" stroke-linecap="round"/></svg>
          </button>
        </div>
        <div class="picker-search">
          <svg viewBox="0 0 24 24" width="16" height="16" fill="none">
            <circle cx="11" cy="11" r="7" stroke="#C4B5AA" stroke-width="2"/>
            <path d="M16.5 16.5l5 5" stroke="#C4B5AA" stroke-width="2" stroke-linecap="round"/>
          </svg>
          <input class="picker-search-input" type="search" placeholder="搜索收藏的菜谱..." v-model="pickerSearch" />
        </div>
        <div class="picker-list" v-if="filteredFavorites.length > 0">
          <div
            v-for="r in filteredFavorites"
            :key="r.recipeId"
            class="picker-item"
            :class="{ added: addedIds.has(r.recipeId) }"
            @click="addToMeal(r)"
          >
            <div class="picker-img-wrap">
              <img v-if="getFirstImage(r.coverImages)" :src="getFirstImage(r.coverImages)" class="picker-img" />
              <div v-else class="picker-img-placeholder"></div>
            </div>
            <div class="picker-info">
              <div class="picker-name">{{ r.title }}</div>
              <div class="picker-meta">
                <span v-if="r.rating">&#x2B50; {{ r.rating }}</span>
                <span v-if="r.favoriteCount">{{ r.favoriteCount }}人收藏</span>
              </div>
            </div>
            <div class="picker-check" v-if="addedIds.has(r.recipeId)">已添加</div>
          </div>
        </div>
        <div v-else-if="loadingFavorites" class="picker-empty">加载中...</div>
        <div v-else class="picker-empty">暂无收藏的菜谱，先去收藏一些吧</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { userStore } from '../store/user'
import { getAllRecipes } from '../api/recipe'
import { getFavoriteRecipeIds } from '../api/favorite'
import { addMealPlan, removeMealPlan, getMealPlanList, getMealPlanDates } from '../api/menuPlan'

const router = useRouter()
const currentUser = computed(() => userStore.user)

// 日历状态
const now = new Date()
const year = ref(now.getFullYear())
const month = ref(now.getMonth() + 1)
const selectedDate = ref(formatDate(now))
const plannedDates = ref(new Set())
const showFullCalendar = ref(false)
const weekDays = ['日', '一', '二', '三', '四', '五', '六']

// 当天三餐数据
const breakfastList = ref([])
const lunchList = ref([])
const dinnerList = ref([])

// 弹窗状态
const showPicker = ref(false)
const pickerMealType = ref('')
const pickerSearch = ref('')
const favoriteRecipes = ref([])
const loadingFavorites = ref(false)

const mealTypeText = computed(() => {
  return { BREAKFAST: '早餐', LUNCH: '午餐', DINNER: '晚餐' }[pickerMealType.value] || ''
})

// 当前餐已添加的 recipeId 集合
const addedIds = computed(() => {
  const list = pickerMealType.value === 'BREAKFAST' ? breakfastList.value
    : pickerMealType.value === 'LUNCH' ? lunchList.value
    : pickerMealType.value === 'DINNER' ? dinnerList.value
    : []
  return new Set(list.map(i => i.recipeId))
})

const filteredFavorites = computed(() => {
  if (!pickerSearch.value.trim()) return favoriteRecipes.value
  const kw = pickerSearch.value.trim().toLowerCase()
  return favoriteRecipes.value.filter(r => r.title && r.title.toLowerCase().includes(kw))
})

// 工具函数
function formatDate(d) {
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

function getFirstImage(images) {
  if (!images) return ''
  try { const arr = JSON.parse(images); return arr[0] || '' } catch { return images }
}

function goDetail(id) { router.push('/recipe/' + id) }

// 日历逻辑
const calendarDays = computed(() => {
  const firstDay = new Date(year.value, month.value - 1, 1)
  const lastDay = new Date(year.value, month.value, 0)
  const startDow = firstDay.getDay()
  const totalDays = lastDay.getDate()

  const days = []
  for (let i = 0; i < startDow; i++) days.push(null)
  for (let d = 1; d <= totalDays; d++) {
    const dateStr = `${year.value}-${String(month.value).padStart(2, '0')}-${String(d).padStart(2, '0')}`
    days.push({ dateStr, dayNum: d })
  }
  return days
})

const visibleCalendarDays = computed(() => {
  const days = calendarDays.value
  if (showFullCalendar.value) return days

  const today = new Date()
  const todayInView = year.value === today.getFullYear() && month.value === today.getMonth() + 1

  if (todayInView) {
    const firstDay = new Date(year.value, month.value - 1, 1)
    const startDow = firstDay.getDay()
    const todayIdx = startDow + today.getDate() - 1
    const row = Math.floor(todayIdx / 7)
    const rowStart = row * 7
    return days.slice(rowStart, Math.min(rowStart + 7, days.length))
  }

  return days.slice(0, Math.min(7, days.length))
})

function isToday(dateStr) {
  return dateStr === formatDate(new Date())
}

function isSelected(dateStr) {
  return dateStr === selectedDate.value
}

function prevMonth() {
  if (month.value === 1) { month.value = 12; year.value-- }
  else month.value--
  loadPlannedDates()
}

function nextMonth() {
  if (month.value === 12) { month.value = 1; year.value++ }
  else month.value++
  loadPlannedDates()
}

function selectDate(dateStr) {
  selectedDate.value = dateStr
  loadDayPlan()
}

// 数据加载
async function loadPlannedDates() {
  if (!currentUser.value) return
  try {
    const res = await getMealPlanDates(currentUser.value.userId, year.value, month.value)
    plannedDates.value = new Set(res.data || [])
  } catch { /* ignore */ }
}

async function loadDayPlan() {
  if (!currentUser.value) return
  try {
    const res = await getMealPlanList(currentUser.value.userId, selectedDate.value)
    const list = res.data || []
    breakfastList.value = list.filter(i => i.mealType === 'BREAKFAST')
    lunchList.value = list.filter(i => i.mealType === 'LUNCH')
    dinnerList.value = list.filter(i => i.mealType === 'DINNER')
  } catch {
    breakfastList.value = []
    lunchList.value = []
    dinnerList.value = []
  }
}

async function loadFavorites() {
  if (!currentUser.value) return
  loadingFavorites.value = true
  try {
    const [idsRes, allRes] = await Promise.all([
      getFavoriteRecipeIds(currentUser.value.userId),
      getAllRecipes()
    ])
    const ids = idsRes.data || []
    const allRecipes = allRes.data || []
    const idSet = new Set(ids)
    favoriteRecipes.value = allRecipes.filter(r => idSet.has(r.recipeId))
  } catch {
    favoriteRecipes.value = []
  } finally {
    loadingFavorites.value = false
  }
}

// 操作
function openPicker(mealType) {
  pickerMealType.value = mealType
  pickerSearch.value = ''
  showPicker.value = true
  loadFavorites()
}

async function addToMeal(recipe) {
  if (!currentUser.value) return
  try {
    await addMealPlan({
      userId: currentUser.value.userId,
      planDate: selectedDate.value,
      mealType: pickerMealType.value,
      recipeId: recipe.recipeId
    })
    // 更新列表
    await loadDayPlan()
    await loadPlannedDates()
  } catch { /* ignore */ }
}

async function removePlan(id) {
  try {
    await removeMealPlan(id)
    await loadDayPlan()
    await loadPlannedDates()
  } catch { /* ignore */ }
}

onMounted(async () => {
  if (!currentUser.value) return
  await loadPlannedDates()
  await loadDayPlan()
})
</script>

<style scoped>
.menu-plan-page {
  padding: max(env(safe-area-inset-top), 0) 0 80px;
  min-height: 100vh;
  min-height: 100dvh;
  background: var(--gradient-page);
}

/* 顶栏 */
.top-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  padding: 0 12px;
  background: var(--gradient-primary);
  position: sticky;
  top: 0;
  z-index: 10;
}

.top-back {
  width: 36px; height: 36px;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer;
  border-radius: 50%;
  transition: all 0.2s;
}

.top-back:active { background: rgba(255,255,255,0.2); }

.top-title {
  font-size: 18px; font-weight: 800;
  color: #fff;
  font-family: var(--font-heading);
}

.top-spacer { width: 36px; }

/* 日历 */
.calendar-card {
  margin: 14px 15px 0;
  background: #fff;
  border-radius: var(--radius-xl);
  padding: 16px 14px 10px;
  box-shadow: var(--shadow-xs);
  border: 1px solid rgba(0,0,0,0.03);
}

.cal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.cal-arrow {
  width: 34px; height: 34px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  background: transparent;
}

.cal-arrow:active { background: var(--primary-bg); }

.cal-title {
  font-size: 16px;
  font-weight: 800;
  color: var(--text-primary);
  font-family: var(--font-heading);
}

.cal-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  text-align: center;
  margin-bottom: 4px;
}

.cal-weekdays span {
  font-size: 12px;
  color: var(--text-muted);
  font-weight: 600;
  padding: 6px 0;
}

.cal-dates {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  text-align: center;
}

.cal-day {
  height: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: var(--radius-xs);
  transition: all 0.15s;
  position: relative;
  font-size: 14px;
  color: var(--text-primary);
  font-weight: 600;
}

.cal-day.empty { cursor: default; }

.cal-day.today .cal-day-num {
  color: var(--primary);
  font-weight: 800;
}

.cal-day.selected {
  background: var(--gradient-primary);
}

.cal-day.selected .cal-day-num {
  color: #fff;
  font-weight: 800;
}

.cal-day:not(.empty):not(.selected):active {
  background: var(--primary-bg);
}

.cal-dot {
  width: 5px; height: 5px;
  border-radius: 50%;
  background: var(--primary);
  position: absolute;
  bottom: 3px;
}

.cal-day.selected .cal-dot {
  background: #fff;
}

.cal-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px 0 2px;
  font-size: 12px;
  color: var(--text-muted);
  cursor: pointer;
  transition: color 0.2s;
  user-select: none;
}

.cal-toggle:active { color: var(--primary); }

.cal-toggle svg { transition: transform 0.2s; }

/* 三餐 */
.meals-area {
  padding: 14px 15px 0;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.meal-section {
  background: #fff;
  border-radius: var(--radius-xl);
  padding: 16px;
  box-shadow: var(--shadow-xs);
  border: 1px solid rgba(0,0,0,0.03);
}

.meal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.meal-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 17px;
  font-weight: 800;
  color: var(--text-primary);
  font-family: var(--font-heading);
}

.meal-icon { font-size: 20px; }

.meal-add-btn {
  width: 32px; height: 32px;
  border-radius: 50%;
  background: var(--gradient-primary);
  display: flex; align-items: center; justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: var(--shadow-primary);
}

.meal-add-btn:active { transform: scale(0.9); }

.meal-recipes {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.recipe-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  background: var(--bg);
  border-radius: var(--radius-full);
  padding: 6px 12px 6px 6px;
  cursor: pointer;
  transition: all 0.2s;
  max-width: 100%;
}

.recipe-chip:active { transform: scale(0.97); }

.chip-img {
  width: 36px; height: 36px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.chip-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100px;
}

.chip-remove {
  width: 18px; height: 18px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
  cursor: pointer;
  transition: all 0.2s;
  background: transparent;
}

.chip-remove:active { background: rgba(0,0,0,0.06); }

.meal-empty {
  font-size: 13px;
  color: var(--text-placeholder);
  padding: 12px 0;
  text-align: center;
}

/* 弹窗 */
.picker-overlay {
  position: fixed; inset: 0;
  background: rgba(30,21,15,0.4);
  backdrop-filter: blur(6px);
  z-index: 100;
  display: flex;
  align-items: flex-end;
}

.picker-sheet {
  width: 100%;
  max-width: 500px;
  margin: 0 auto;
  max-height: 75vh;
  background: #fff;
  border-radius: var(--radius-2xl) var(--radius-2xl) 0 0;
  display: flex;
  flex-direction: column;
  animation: slideUp 0.3s var(--ease-smooth);
}

.picker-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 20px 12px;
}

.picker-title {
  font-size: 17px;
  font-weight: 800;
  color: var(--text-primary);
  font-family: var(--font-heading);
}

.picker-close {
  width: 34px; height: 34px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer;
  background: var(--divider);
  transition: all 0.2s;
}

.picker-close:active { background: var(--border); }

.picker-search {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0 20px 10px;
  height: 40px;
  background: var(--bg);
  border-radius: var(--radius-full);
  padding: 0 16px;
}

.picker-search-input {
  flex: 1;
  font-size: 14px;
  border: none; outline: none;
  background: transparent;
  color: var(--text-primary);
}

.picker-search-input::placeholder { color: var(--text-placeholder); }

.picker-list {
  flex: 1;
  overflow-y: auto;
  padding: 0 20px 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.picker-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.15s;
  background: var(--bg);
}

.picker-item:active { transform: scale(0.98); }

.picker-item.added {
  opacity: 0.6;
  background: var(--primary-bg);
}

.picker-img-wrap {
  width: 52px; height: 52px;
  border-radius: var(--radius-sm);
  overflow: hidden;
  flex-shrink: 0;
  background: linear-gradient(135deg, #F5F0E8, #EDE4D5);
}

.picker-img {
  width: 100%; height: 100%;
  object-fit: cover;
}

.picker-img-placeholder { width: 100%; height: 100%; }

.picker-info { flex: 1; min-width: 0; }

.picker-name {
  font-size: 15px; font-weight: 700;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 4px;
}

.picker-meta {
  font-size: 12px;
  color: var(--text-muted);
  display: flex;
  gap: 10px;
}

.picker-check {
  font-size: 12px;
  font-weight: 700;
  color: var(--primary);
  flex-shrink: 0;
}

.picker-empty {
  text-align: center;
  padding: 40px 20px;
  font-size: 14px;
  color: var(--text-muted);
}
</style>
