<template>
  <div class="my-recipes-page">
    <!-- 顶部导航 -->
    <div class="nav-bar">
      <svg class="back-icon" viewBox="0 0 24 24" width="24" height="24" @click="$router.back()">
        <path d="M15 18l-6-6 6-6" stroke="#333" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <span class="nav-title">我发布的菜谱</span>
      <span class="nav-placeholder"></span>
    </div>

    <!-- 食谱数量 -->
    <div class="count-row" v-if="recipes.length > 0">
      共 {{ recipes.length }} 个作品
    </div>

    <!-- 空状态 -->
    <div v-if="loading" class="empty-text">加载中...</div>
    <div v-else-if="recipes.length === 0" class="empty-text">还没有发布过菜谱</div>

    <!-- 菜谱网格 -->
    <div v-else class="recipe-grid">
      <div v-for="item in recipes" :key="item.recipeId" class="recipe-card" @click="goDetail(item.recipeId)">
        <img class="recipe-img" :src="getCover(item.coverImages)" @error="onImgError" />
        <div class="recipe-name">{{ item.title }}</div>
        <div class="recipe-meta">{{ item.cookTime || '' }} · {{ formatCount(item.likeCount) }}收藏</div>
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
  if (!user || !user.userId) {
    router.push('/')
    return
  }
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
  try {
    const arr = JSON.parse(images)
    return arr[0] || ''
  } catch {
    return images
  }
}

function formatCount(n) {
  if (!n) return '0'
  if (n >= 1000) return (n / 1000).toFixed(1) + 'k'
  return String(n)
}

function onImgError(e) {
  e.target.style.display = 'none'
}

function goDetail(id) {
  router.push('/recipe/' + id)
}
</script>

<style scoped>
.my-recipes-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: var(--bg);
  padding: 0 15px 20px;
}

/* 顶部导航 */
.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 48px;
  padding-top: 6px;
}

.back-icon {
  cursor: pointer;
  flex-shrink: 0;
}

.nav-title {
  font-size: 17px;
  font-weight: 600;
  color: #333;
}

.nav-placeholder {
  width: 24px;
  flex-shrink: 0;
}

/* 数量 */
.count-row {
  font-size: 13px;
  color: var(--gray);
  margin-bottom: 12px;
}

/* 空状态 */
.empty-text {
  text-align: center;
  color: #B99E8E;
  padding-top: 160px;
  font-size: 14px;
}

/* 菜谱网格 */
.recipe-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.recipe-card {
  background: var(--white);
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0,0,0,0.04);
  cursor: pointer;
  transition: transform 0.15s;
}

.recipe-card:active {
  transform: scale(0.97);
}

.recipe-img {
  width: 100%;
  height: 120px;
  object-fit: cover;
  background: #f0e4d6;
}

.recipe-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  padding: 8px 10px 2px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.recipe-meta {
  font-size: 11px;
  color: var(--gray);
  padding: 0 10px 10px;
}
</style>
