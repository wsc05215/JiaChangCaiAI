<template>
  <div class="search-page">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <svg class="back-icon" viewBox="0 0 24 24" width="22" height="22" @click="$router.back()">
        <path d="M15 18l-6-6 6-6" stroke="#333" stroke-width="2" fill="none" stroke-linecap="round"/>
      </svg>
      <input
        class="search-input"
        type="search"
        placeholder="搜索菜谱"
        v-model="keyword"
        @keyup.enter="doSearch"
        autofocus
      />
      <span class="search-btn" @click="doSearch">搜索</span>
    </div>

    <!-- 搜索结果 -->
    <div v-if="keyword.trim() === ''" class="hint-text">输入关键词搜索菜谱</div>
    <div v-else-if="loading" class="hint-text">搜索中...</div>
    <div v-else-if="results.length === 0" class="hint-text">未找到相关菜谱</div>
    <div v-else class="result-list">
      <div v-for="item in results" :key="item.recipeId" class="result-card" @click="goDetail(item.recipeId)">
        <img class="result-img" :src="getCover(item.coverImages)" @error="onImgError" />
        <div class="result-info">
          <div class="result-title">{{ item.title }}</div>
          <div class="result-desc">{{ item.description }}</div>
          <div class="result-meta">
            <span>{{ item.authorName || '用户' + item.authorId }}</span>
            <span>收藏 {{ item.favoriteCount || 0 }}</span>
          </div>
        </div>
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
  try {
    const arr = JSON.parse(images)
    return arr[0] || ''
  } catch {
    return images
  }
}

function onImgError(e) {
  e.target.style.display = 'none'
}

function goDetail(id) {
  router.push('/recipe/' + id)
}
</script>

<style scoped>
.search-page {
  background: var(--bg);
  min-height: 100vh;
  padding: 10px 15px;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 10px;
}

.back-icon {
  cursor: pointer;
  flex-shrink: 0;
}

.search-input {
  flex: 1;
  height: 36px;
  border-radius: 18px;
  background: #f0e4d6;
  border: none;
  padding: 0 14px;
  font-size: 14px;
  color: #333;
  outline: none;
}

.search-input::placeholder {
  color: #B99E8E;
}

.search-btn {
  font-size: 14px;
  color: var(--orange);
  cursor: pointer;
  flex-shrink: 0;
}

.hint-text {
  text-align: center;
  color: #B99E8E;
  padding-top: 120px;
  font-size: 14px;
}

.result-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 16px;
}

.result-card {
  display: flex;
  gap: 12px;
  background: var(--white);
  border-radius: 12px;
  padding: 12px;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.result-img {
  width: 90px;
  height: 90px;
  border-radius: 8px;
  object-fit: cover;
  background: #f0e4d6;
  flex-shrink: 0;
}

.result-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-width: 0;
}

.result-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.result-desc {
  font-size: 12px;
  color: #999;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}

.result-meta {
  display: flex;
  gap: 12px;
  font-size: 11px;
  color: var(--gray);
}
</style>
