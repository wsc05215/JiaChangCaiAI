<template>
  <div class="search-page">
    <div class="search-bar">
      <svg class="back-icon" viewBox="0 0 24 24" width="24" height="24" @click="$router.back()">
        <path d="M15 18l-6-6 6-6" stroke="#6B5E52" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <input
        ref="searchInputRef"
        class="search-input"
        type="search"
        placeholder="搜索菜谱..."
        v-model="keyword"
        @keyup.enter="doSearch"
        autofocus
      />
      <button class="search-btn" @click="doSearch">搜索</button>
    </div>

    <div v-if="keyword.trim() === ''" class="hint-area">
      <svg viewBox="0 0 80 80" width="60" height="60" fill="none">
        <circle cx="40" cy="40" r="38" stroke="#E8DDD2" stroke-width="2" stroke-dasharray="6 4"/>
        <circle cx="32" cy="32" r="10" stroke="#E0D5C8" stroke-width="2.5"/>
        <path d="M40 40l12 12" stroke="#E0D5C8" stroke-width="2.5" stroke-linecap="round"/>
      </svg>
      <p class="hint-text">输入关键词搜索菜谱</p>
    </div>
    <div v-else-if="loading" class="hint-area"><p class="hint-text">搜索中...</p></div>
    <div v-else-if="results.length === 0" class="hint-area">
      <svg viewBox="0 0 80 80" width="60" height="60" fill="none">
        <circle cx="40" cy="40" r="38" stroke="#E8DDD2" stroke-width="2" stroke-dasharray="6 4"/>
        <path d="M40 20v40M20 40h40" stroke="#E0D5C8" stroke-width="2" stroke-linecap="round"/>
      </svg>
      <p class="hint-text">未找到相关菜谱</p>
    </div>
    <div v-else class="result-list">
      <div v-for="item in results" :key="item.recipeId" class="result-card" @click="goDetail(item.recipeId)">
        <img class="result-img" :src="getCover(item.coverImages)" @error="onImgError" />
        <div class="result-info">
          <div class="result-title">{{ item.title }}</div>
          <div class="result-desc">{{ item.description }}</div>
          <div class="result-meta">
            <span class="result-author">{{ item.authorName || '用户' + item.authorId }}</span>
            <span class="result-fav">&#x2764; {{ item.favoriteCount || 0 }}</span>
          </div>
        </div>
        <svg class="result-arrow" viewBox="0 0 24 24" width="18" height="18">
          <path d="M9 18l6-6-6-6" stroke="#C4B5AA" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { searchRecipes } from '../api/search'

const router = useRouter()
const keyword = ref('')
const results = ref([])
const loading = ref(false)

async function doSearch() {
  if (!keyword.value.trim()) return
  loading.value = true
  try {
    const res = await searchRecipes(keyword.value.trim())
    results.value = res.data || []
  } catch (e) {
    console.error('搜索失败:', e)
    results.value = []
  } finally {
    loading.value = false
  }
}

function getCover(images) {
  if (!images) return ''
  try { const arr = JSON.parse(images); return arr[0] || '' } catch { return images }
}

function onImgError(e) { e.target.style.display = 'none' }

function goDetail(id) { router.push('/recipe/' + id) }
</script>

<style scoped>
.search-page {
  background: var(--gradient-page);
  min-height: 100vh;
  padding: 14px 15px;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 10px;
}

.back-icon { cursor: pointer; flex-shrink: 0; }

.search-input {
  flex: 1;
  height: 42px;
  border-radius: var(--radius-full);
  background: #fff;
  border: 1.5px solid var(--border);
  padding: 0 18px;
  font-size: 15px;
  color: var(--text-primary);
  outline: none;
  box-shadow: var(--shadow-xs);
  transition: all 0.2s;
}

.search-input:focus {
  border-color: var(--primary);
  box-shadow: 0 0 0 5px rgba(230,126,34,0.06);
}

.search-input::placeholder { color: var(--text-placeholder); }

.search-btn {
  font-size: 14px;
  font-weight: 700;
  color: var(--primary);
  cursor: pointer;
  flex-shrink: 0;
}

.hint-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 100px;
  gap: 14px;
}

.hint-text { color: var(--text-muted); font-size: 14px; }

.result-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-top: 18px;
}

.result-card {
  display: flex;
  align-items: center;
  gap: 14px;
  background: #fff;
  border-radius: var(--radius-lg);
  padding: 14px;
  cursor: pointer;
  box-shadow: var(--shadow-xs);
  border: 1px solid rgba(0,0,0,0.03);
  transition: all 0.2s;
}

.result-card:active { transform: scale(0.98); }

.result-img {
  width: 95px;
  height: 95px;
  border-radius: var(--radius-md);
  object-fit: cover;
  background: linear-gradient(135deg, #F5F0E8, #EDE4D5);
  flex-shrink: 0;
}

.result-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.result-title {
  font-size: 16px;
  font-weight: 800;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.result-desc {
  font-size: 13px;
  color: var(--text-muted);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
}

.result-meta {
  display: flex;
  gap: 14px;
  font-size: 12px;
  margin-top: 2px;
}

.result-author { color: var(--text-placeholder); }
.result-fav { color: var(--primary); font-weight: 600; }

.result-arrow {
  flex-shrink: 0;
  opacity: 0.3;
}
</style>
