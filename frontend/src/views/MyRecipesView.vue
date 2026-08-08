<template>
  <div class="my-recipes-page">
    <div class="nav-bar">
      <svg class="back-icon" viewBox="0 0 24 24" width="24" height="24" @click="$goBack()">
        <path d="M15 18l-6-6 6-6" stroke="#6B5E52" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <span class="nav-title">我发布的菜谱</span>
      <span class="nav-placeholder"></span>
    </div>

    <div class="count-row" v-if="recipes.length > 0">
      共 <span class="count-num">{{ recipes.length }}</span> 个作品
    </div>

    <div v-if="loading" class="empty-state"><p class="empty-text">加载中...</p></div>
    <div v-else-if="recipes.length === 0" class="empty-state">
      <svg viewBox="0 0 80 80" width="60" height="60" fill="none">
        <circle cx="40" cy="40" r="38" stroke="#E8DDD2" stroke-width="2" stroke-dasharray="6 4"/>
        <rect x="28" y="22" width="24" height="36" rx="3" stroke="#E0D5C8" stroke-width="2"/>
        <path d="M33 30h14M33 36h10M33 42h14M33 48h10" stroke="#E0D5C8" stroke-width="1.5" stroke-linecap="round"/>
      </svg>
      <p class="empty-text">还没有发布过菜谱</p>
    </div>
    <div v-else class="recipe-grid">
      <div v-for="item in recipes" :key="item.recipeId" class="recipe-card" @click="goDetail(item.recipeId)">
        <div class="recipe-img-wrap">
          <img class="recipe-img" :src="getCover(item.coverImages)" @error="onImgError" />
        </div>
        <div class="recipe-body">
          <div class="recipe-name">{{ item.title }}</div>
          <div class="recipe-meta">{{ item.cookTime || '30分钟' }} · {{ formatCount(item.favoriteCount) }}收藏</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { userStore } from '../store/user'
import { getOwnRecipes } from '../api/recipe'

const router = useRouter()
const recipes = ref([])
const loading = ref(true)

onMounted(async () => {
  const user = userStore.user
  if (!user || !user.userId) { router.push('/'); return }
  try {
    const res = await getOwnRecipes(user.userId)
    recipes.value = res.data || []
  } catch {
    recipes.value = []
  } finally {
    loading.value = false
  }
})

function getCover(images) {
  if (!images) return ''
  try { const arr = JSON.parse(images); return arr[0] || '' } catch { return images }
}

function formatCount(n) {
  if (!n) return '0'
  if (n >= 1000) return (n / 1000).toFixed(1) + 'k'
  return String(n)
}

function onImgError(e) { e.target.style.display = 'none' }

function goDetail(id) { router.push('/recipe/' + id) }
</script>

<style scoped>
.my-recipes-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: var(--gradient-page);
  padding: env(safe-area-inset-top, 0px) 15px 20px;
}

.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 48px;
  padding-top: 8px;
}

.back-icon { cursor: pointer; flex-shrink: 0; }
.nav-title { font-size: 18px; font-weight: 800; color: var(--text-primary); letter-spacing: 0.5px; }
.nav-placeholder { width: 24px; flex-shrink: 0; }

.count-row {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 14px;
  font-weight: 500;
}

.count-num {
  font-size: 18px;
  font-weight: 800;
  color: var(--primary);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 120px;
  gap: 14px;
}

.empty-text { color: var(--text-muted); font-size: 14px; }

.recipe-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.recipe-card {
  background: #fff;
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-xs);
  cursor: pointer;
  transition: all 0.25s var(--ease-smooth);
  border: 1px solid rgba(0,0,0,0.03);
}

.recipe-card:active { transform: scale(0.95); }

.recipe-img-wrap {
  overflow: hidden;
}

.recipe-img {
  width: 100%;
  height: 125px;
  object-fit: cover;
  background: linear-gradient(135deg, #F5F0E8, #EDE4D5);
  transition: transform 0.4s;
}

.recipe-card:active .recipe-img { transform: scale(1.05); }

.recipe-body {
  padding: 10px 12px 12px;
}

.recipe-name {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 2px;
}

.recipe-meta {
  font-size: 11px;
  color: var(--text-muted);
}
</style>
