<template>
  <div class="ingredient-page">
    <div class="nav-bar">
      <div class="back-btn" @click="$goBack()">
        <svg viewBox="0 0 24 24" width="24" height="24">
          <path d="M15 18l-6-6 6-6" stroke="#6B5E52" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <span class="nav-title">记食材</span>
      <div class="nav-placeholder"></div>
    </div>

    <div class="page-body">
      <div class="section-title">记录新买的食材</div>

      <div class="ingredient-rows">
        <div v-for="(item, i) in rows" :key="i" class="ingredient-row">
          <div class="row-num">{{ i + 1 }}</div>
          <div class="row-fields">
            <input
              v-model="item.name"
              class="field-input name-input"
              placeholder="食材名称，如：鸡蛋"
            />
            <select v-model="item.category" class="field-input category-select">
              <option v-for="c in categories" :key="c" :value="c">{{ c }}</option>
            </select>
            <input
              v-model="item.date"
              type="date"
              class="field-input date-input"
              :max="today"
            />
          </div>
          <div v-if="rows.length > 1" class="row-del" @click="removeRow(i)">
            <svg viewBox="0 0 20 20" width="16" height="16">
              <path d="M6 6l8 8M14 6l-8 8" stroke="#C4B5AA" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </div>
        </div>
      </div>

      <button class="add-row-btn" @click="addRow">
        <svg viewBox="0 0 20 20" width="16" height="16">
          <path d="M10 4v12M4 10h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>
        添加一行
      </button>

      <button
        class="submit-btn"
        @click="submit"
        :disabled="!canSubmit || submitting"
      >
        <span v-if="!submitting">保存全部</span>
        <span v-else>保存中...</span>
      </button>
    </div>

    <div v-if="showNotMember" class="not-member-overlay" @click="goMember">
      <div class="not-member-card" @click.stop="goMember">
        <div class="nm-icon">&#x1F512;</div>
        <div class="nm-title">会员专享功能</div>
        <div class="nm-desc">记食材功能仅限会员使用，点击开通会员</div>
        <button class="nm-btn">开通会员</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { addIngredient } from '../api/ingredient'
import { checkMember } from '../api/member'
import { userStore } from '../store/user'

const router = useRouter()
const categories = ['蔬菜', '生禽', '蛋类', '水产', '豆制品', '其他']
const rows = ref([{ name: '', category: '蔬菜', date: new Date().toISOString().slice(0, 10) }])
const submitting = ref(false)
const showNotMember = ref(false)

const today = computed(() => new Date().toISOString().slice(0, 10))

const canSubmit = computed(() => rows.value.some(r => r.name.trim()))

onMounted(async () => {
  const uid = userStore.user?.userId
  if (!uid) return

  const isMember = await checkMember(uid)
  if (!isMember) {
    showNotMember.value = true
    return
  }
})

function addRow() {
  rows.value.push({ name: '', category: '蔬菜', date: new Date().toISOString().slice(0, 10) })
}

function removeRow(i) {
  rows.value.splice(i, 1)
}

async function submit() {
  if (submitting.value) return

  const uid = userStore.user?.userId
  if (!uid) return

  // 过滤掉空名称的行
  const validRows = rows.value.filter(r => r.name.trim())
  if (validRows.length === 0) return

  submitting.value = true
  try {
    for (const r of validRows) {
      const createTime = r.date + 'T00:00:00'
      await addIngredient(uid, r.name.trim(), r.category, createTime)
    }
    // 重置为一行空表单
    rows.value = [{ name: '', category: '蔬菜', date: new Date().toISOString().slice(0, 10) }]
  } finally {
    submitting.value = false
  }
}

function goMember() {
  router.push('/custom')
}
</script>

<style scoped>
.ingredient-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  height: 100dvh;
  background: linear-gradient(180deg, #F5F0E8 0%, #F9F7F2 25%, #F8F4ED 100%);
  padding-top: max(env(safe-area-inset-top), 0px);
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

.page-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px 14px;
  -webkit-overflow-scrolling: touch;
}

.section-title {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 14px;
}

.ingredient-rows {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.ingredient-row {
  display: flex;
  align-items: center;
  gap: 10px;
  background: #fff;
  border-radius: 14px;
  padding: 12px 14px;
  box-shadow: 0 1px 8px rgba(30, 21, 15, 0.04);
  border: 1.5px solid rgba(0,0,0,0.04);
}

.row-num {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: var(--primary-bg);
  color: var(--primary);
  font-size: 12px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.row-fields {
  flex: 1;
  display: flex;
  gap: 8px;
}

.field-input {
  height: 40px;
  border: 1.5px solid var(--border);
  border-radius: 10px;
  padding: 0 10px;
  font-size: 14px;
  color: var(--text-primary);
  background: #FAFAF8;
  outline: none;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.field-input:focus {
  border-color: var(--primary);
  background: #fff;
}

.name-input { flex: 1.4; }

.category-select {
  flex: 1;
  color: var(--text-primary);
  cursor: pointer;
  appearance: none;
  -webkit-appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 12 7' width='12' height='7' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M1 1l5 5 5-5' stroke='%23C4B5AA' stroke-width='1.6' stroke-linecap='round'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 8px center;
  padding-right: 26px;
}

.date-input { flex: 1; }

.row-del {
  width: 28px; height: 28px;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer;
  border-radius: 50%;
  flex-shrink: 0;
  transition: background 0.2s;
}

.row-del:active {
  background: #F5F0E8;
}

.add-row-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-top: 12px;
  padding: 14px;
  border-radius: 14px;
  border: 2px dashed var(--border);
  color: var(--text-muted);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  background: transparent;
  transition: all 0.2s;
}

.add-row-btn:active {
  border-color: var(--primary);
  color: var(--primary);
  background: var(--primary-bg);
}

.submit-btn {
  width: 100%;
  height: 48px;
  border-radius: 14px;
  background: var(--gradient-primary);
  color: #fff;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 4px 16px rgba(255, 122, 51, 0.2);
  margin-top: 20px;
}

.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.submit-btn:not(:disabled):active {
  transform: scale(0.97);
}

/* not member overlay */
.not-member-overlay {
  position: fixed;
  inset: 0;
  background: rgba(20, 14, 8, 0.55);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.not-member-card {
  background: #fff;
  border-radius: 24px;
  padding: 36px 28px 28px;
  text-align: center;
  width: 280px;
  box-shadow: 0 8px 40px rgba(0,0,0,0.15);
}

.nm-icon {
  font-size: 48px;
  margin-bottom: 14px;
}

.nm-title {
  font-size: 20px;
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.nm-desc {
  font-size: 13px;
  color: var(--text-muted);
  line-height: 1.5;
  margin-bottom: 20px;
}

.nm-btn {
  width: 100%;
  height: 46px;
  border-radius: 14px;
  background: var(--gradient-primary);
  color: #fff;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(255, 122, 51, 0.25);
}

.nm-btn:active {
  transform: scale(0.96);
}
</style>
