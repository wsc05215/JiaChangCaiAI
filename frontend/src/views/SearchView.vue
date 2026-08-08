<template>
  <div class="search-page">
    <div class="search-bar">
      <svg class="back-icon" viewBox="0 0 24 24" width="24" height="24" @click="$goBack()">
        <path d="M15 18l-6-6 6-6" stroke="#6B5E52" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <input
        ref="searchInputRef"
        class="search-input"
        type="search"
        placeholder="搜索菜谱、商品..."
        v-model="keyword"
        @keyup.enter="doSearch"
        autofocus
      />
      <button class="search-btn" @click="doSearch">搜索</button>
    </div>

    <!-- Tab 导航 -->
    <div class="tabs">
      <span class="tab-item" :class="{ active: activeTab === 'recipe' }" @click="activeTab = 'recipe'">菜谱</span>
      <span class="tab-item" :class="{ active: activeTab === 'product' }" @click="activeTab = 'product'">商品</span>
    </div>

    <!-- 菜谱 tab -->
    <div v-if="activeTab === 'recipe'" class="tab-content">
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

    <!-- 商品 tab -->
    <div v-if="activeTab === 'product'" class="tab-content">
      <div v-if="keyword.trim() === ''" class="hint-area">
        <svg viewBox="0 0 80 80" width="60" height="60" fill="none">
          <circle cx="40" cy="40" r="38" stroke="#E8DDD2" stroke-width="2" stroke-dasharray="6 4"/>
          <rect x="20" y="20" width="22" height="22" rx="4" stroke="#E0D5C8" stroke-width="2"/>
          <rect x="46" y="20" width="14" height="22" rx="4" stroke="#E0D5C8" stroke-width="2"/>
          <rect x="20" y="46" width="14" height="14" rx="4" stroke="#E0D5C8" stroke-width="2"/>
          <rect x="38" y="46" width="22" height="14" rx="4" stroke="#E0D5C8" stroke-width="2"/>
        </svg>
        <p class="hint-text">输入关键词搜索商品</p>
      </div>
      <div v-else-if="loading" class="hint-area"><p class="hint-text">搜索中...</p></div>
      <div v-else-if="productResults.length === 0" class="hint-area">
        <svg viewBox="0 0 80 80" width="60" height="60" fill="none">
          <circle cx="40" cy="40" r="38" stroke="#E8DDD2" stroke-width="2" stroke-dasharray="6 4"/>
          <path d="M40 20v40M20 40h40" stroke="#E0D5C8" stroke-width="2" stroke-linecap="round"/>
        </svg>
        <p class="hint-text">未找到相关商品</p>
      </div>
      <div v-else class="shop-list">
        <div v-for="item in productResults" :key="item.id" class="shop-card" @click="goProduct(item.id)">
          <img class="shop-img" :src="item.coverImage" @error="onImgError" />
          <div class="shop-info">
            <div class="shop-name">{{ item.name }}</div>
            <div class="shop-subtitle">{{ item.subtitle }}</div>
            <div class="shop-bottom">
              <span class="shop-price">¥{{ item.price }}</span>
              <span class="shop-unit">/{{ item.unit }}</span>
            </div>
          </div>
          <svg class="result-arrow" viewBox="0 0 24 24" width="18" height="18">
            <path d="M9 18l6-6-6-6" stroke="#C4B5AA" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { searchRecipes, searchProducts } from '../api/search'

const router = useRouter()
const keyword = ref('')
const results = ref([])
const productResults = ref([])
const loading = ref(false)
const activeTab = ref('recipe')

async function doSearch() {
  if (!keyword.value.trim()) return
  loading.value = true
  try {
    const [recipeRes, productRes] = await Promise.all([
      searchRecipes(keyword.value.trim()),
      searchProducts(keyword.value.trim())
    ])
    results.value = recipeRes.data || []
    productResults.value = productRes.data || []
  } catch (e) {
    console.error('搜索失败:', e)
    results.value = []
    productResults.value = []
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

function goProduct(id) { router.push('/product/' + id) }
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

/* ============ tabs ============ */
.tabs {
  display: flex;
  gap: 30px;
  margin-top: 20px;
  margin-bottom: 6px;
}

.tab-item {
  font-family: var(--font-heading);
  font-size: 18px;
  font-weight: 500;
  color: var(--text-muted);
  cursor: pointer;
  padding-bottom: 10px;
  border-bottom: 3px solid transparent;
  transition: all 0.3s var(--ease-smooth);
}

.tab-item.active {
  color: var(--primary);
  border-bottom-color: var(--primary-lighter);
  font-weight: 800;
}

.tab-content {
  margin-top: 10px;
}

/* ============ hints ============ */
.hint-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 80px;
  gap: 14px;
}

.hint-text { color: var(--text-muted); font-size: 14px; }

/* ============ recipe results ============ */
.result-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
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

/* ============ product results ============ */
.shop-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.shop-card {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #fff;
  border-radius: var(--radius-lg);
  padding: 12px;
  cursor: pointer;
  box-shadow: var(--shadow-xs);
  border: 1px solid rgba(0,0,0,0.03);
  transition: all 0.2s;
}

.shop-card:active { transform: scale(0.98); }

.shop-img {
  width: 64px;
  height: 64px;
  border-radius: var(--radius-md);
  object-fit: cover;
  background: linear-gradient(135deg, #F5F0E8, #EDE4D5);
  flex-shrink: 0;
}

.shop-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 3px;
  min-width: 0;
}

.shop-name {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.shop-subtitle {
  font-size: 12px;
  color: var(--text-muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.shop-bottom {
  display: flex;
  align-items: baseline;
  gap: 2px;
  margin-top: 2px;
}

.shop-price {
  font-size: 16px;
  font-weight: 800;
  color: #e67e22;
}

.shop-unit {
  font-size: 11px;
  color: var(--text-placeholder);
}
</style>
