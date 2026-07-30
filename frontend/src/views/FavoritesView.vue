<template>
  <div class="favorites-page">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <svg class="search-icon" viewBox="0 0 24 24" width="16" height="16">
        <circle cx="11" cy="11" r="7" stroke="#B99E8E" stroke-width="2" fill="none"/>
        <path d="M16.5 16.5l5 5" stroke="#B99E8E" stroke-width="2" stroke-linecap="round"/>
      </svg>
      <input class="search-input" type="search" placeholder="搜索" v-model="searchText" />
    </div>

    <!-- Tab 切换 -->
    <div class="tabs">
      <span class="tab-btn" :class="{ active: activeTab === 'recipe' }" @click="activeTab = 'recipe'">全部菜谱</span>
      <span class="tab-btn" :class="{ active: activeTab === 'menu' }" @click="activeTab = 'menu'">菜单</span>
    </div>

    <!-- 全部菜谱 -->
    <div v-if="activeTab === 'recipe'" class="tab-content">
      <div v-if="!currentUser" class="empty-text">登录后查看收藏的菜谱</div>
      <div v-else-if="loading" class="empty-text">加载中...</div>
      <div v-else-if="favoriteRecipes.length === 0" class="empty-text">还没有收藏菜谱</div>
      <div v-else-if="filteredRecipes.length === 0 && searchText.trim()" class="empty-text">没有找到相关菜谱</div>
      <div v-else class="favorite-list">
        <div v-for="item in filteredRecipes" :key="item.recipeId" class="fav-card" @click="goDetail(item.recipeId)">
          <div class="fav-img-wrap">
            <img :src="getCover(item.coverImages)" class="fav-img" @error="onImgError" />
            <div class="score-badge" v-if="item.rating != null">
              <svg viewBox="0 0 24 24" width="10" height="10">
                <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" fill="#fff" stroke="none"/>
              </svg>
              <span>{{ item.rating }}</span>
            </div>
          </div>
          <div class="fav-info">
            <div class="fav-title">{{ item.title }}</div>
            <div class="fav-meta">{{ item.rating != null ? item.rating + '分  ' : '' }}{{ item.favoriteCount || 0 }}人收藏</div>
            <div class="fav-author">
              <svg viewBox="0 0 24 24" width="12" height="12">
                <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" stroke="#B99E8E" stroke-width="1.5" fill="none"/>
                <circle cx="12" cy="7" r="4" stroke="#B99E8E" stroke-width="1.5" fill="none"/>
              </svg>
              <span>{{ item.authorName || '用户' + item.authorId }}</span>
            </div>
          </div>
          <button class="fav-remove-btn" @click.stop="handleRemoveFavorite(item)" title="取消收藏">
            <svg viewBox="0 0 24 24" width="16" height="16">
              <path d="M18 6L6 18M6 6l12 12" stroke="#999" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- 菜单 -->
    <div v-else class="tab-content">
      <div class="menu-card">
        <div class="menu-img-wrap">
          <svg viewBox="0 0 24 24" width="48" height="48">
            <rect x="3" y="3" width="18" height="18" rx="3" stroke="#B99E8E" stroke-width="1.5" fill="none"/>
            <path d="M3 9h18" stroke="#B99E8E" stroke-width="1.5"/>
            <path d="M9 21V9" stroke="#B99E8E" stroke-width="1.5"/>
          </svg>
        </div>
        <div class="menu-title">自定义计划</div>
        <div class="menu-desc">提前规划未来三餐</div>
      </div>
    </div>

    <!-- 删除确认弹窗 -->
    <div v-if="showConfirm" class="modal-overlay" @click.self="showConfirm = false">
      <div class="modal-box">
        <div class="modal-title">取消收藏</div>
        <div class="modal-desc">确定取消收藏「{{ removingItem?.title }}」吗？</div>
        <div class="modal-actions">
          <button class="modal-btn modal-btn-cancel" @click="showConfirm = false">再想想</button>
          <button class="modal-btn modal-btn-confirm" @click="confirmRemove">确定</button>
        </div>
      </div>
    </div>

    <AppTabbar />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { userStore } from '../store/user'
import { getAllRecipes } from '../api/recipe'
import { getFavoriteRecipeIds, removeFavorite } from '../api/favorite'
import AppTabbar from '../components/AppTabbar.vue'

const router = useRouter()
const activeTab = ref('recipe')
const searchText = ref('')
const loading = ref(false)
const favoriteRecipes = ref([])
const showConfirm = ref(false)
const removingItem = ref(null)

const currentUser = computed(() => userStore.user)

const filteredRecipes = computed(() => {
  if (!searchText.value.trim()) return favoriteRecipes.value
  const keyword = searchText.value.trim().toLowerCase()
  return favoriteRecipes.value.filter(r => r.title && r.title.toLowerCase().includes(keyword))
})

onMounted(async () => {
  if (!currentUser.value) return
  loading.value = true
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

function onImgError(e) {
  e.target.style.display = 'none'
}

function goDetail(id) {
  router.push('/recipe/' + id)
}

function handleRemoveFavorite(item) {
  removingItem.value = item
  showConfirm.value = true
}

async function confirmRemove() {
  const item = removingItem.value
  if (!item || !currentUser.value) {
    showConfirm.value = false
    return
  }
  try {
    await removeFavorite(currentUser.value.userId, item.recipeId)
    favoriteRecipes.value = favoriteRecipes.value.filter(r => r.recipeId !== item.recipeId)
  } catch {
    // ignore
  } finally {
    showConfirm.value = false
    removingItem.value = null
  }
}
</script>

<style scoped>
.favorites-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: var(--bg);
  padding: 0 15px 70px;
}

/* 搜索栏 */
.search-bar {
  display: flex;
  align-items: center;
  height: 36px;
  background: #f0e4d6;
  border-radius: 18px;
  padding: 0 14px;
  margin-top: 10px;
  gap: 8px;
}

.search-icon {
  flex-shrink: 0;
}

.search-input {
  flex: 1;
  font-size: 14px;
  color: #333;
  background: transparent;
  border: none;
  outline: none;
}

.search-input::placeholder {
  color: #B99E8E;
}

/* Tab 切换 */
.tabs {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.tab-btn {
  font-size: 14px;
  color: var(--brown);
  padding: 8px 20px;
  border-radius: 18px;
  background: #f5ebe0;
  cursor: pointer;
  transition: all 0.2s;
}

.tab-btn.active {
  background: var(--orange);
  color: #fff;
  font-weight: 600;
}

.tab-content {
  margin-top: 14px;
}

/* 空状态 */
.empty-text {
  text-align: center;
  color: #B99E8E;
  padding-top: 120px;
  font-size: 14px;
}

/* 收藏列表 */
.favorite-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.fav-card {
  display: flex;
  gap: 12px;
  background: var(--white);
  border-radius: 16px;
  padding: 10px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.04);
  cursor: pointer;
  transition: transform 0.15s;
  position: relative;
}

.fav-card:active {
  transform: scale(0.98);
}

.fav-img-wrap {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 10px;
  overflow: hidden;
  flex-shrink: 0;
  background: #f0e4d6;
}

.fav-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.score-badge {
  position: absolute;
  top: 6px;
  left: 6px;
  display: flex;
  align-items: center;
  gap: 3px;
  background: rgba(0,0,0,0.45);
  color: #fff;
  font-size: 11px;
  font-weight: 600;
  padding: 3px 7px;
  border-radius: 10px;
}

.fav-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-width: 0;
}

.fav-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 6px;
  padding-right: 28px;
}

.fav-meta {
  font-size: 12px;
  color: var(--gray);
  margin-bottom: 20px;
}

.fav-author {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--brown);
}

.fav-remove-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(0,0,0,0.04);
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 2;
  transition: background 0.15s;
}

.fav-remove-btn:active {
  background: rgba(255,0,0,0.1);
}

/* 菜单 */
.menu-card {
  background: var(--white);
  border-radius: 16px;
  padding: 24px;
  text-align: center;
  box-shadow: 0 2px 10px rgba(0,0,0,0.04);
}

.menu-img-wrap {
  margin-bottom: 14px;
}

.menu-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
}

.menu-desc {
  font-size: 13px;
  color: var(--gray);
}

/* 确认弹窗 */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-box {
  width: 280px;
  background: #fff;
  border-radius: 16px;
  padding: 24px 20px 20px;
  text-align: center;
}

.modal-title {
  font-size: 16px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.modal-desc {
  font-size: 13px;
  color: #666;
  margin-bottom: 20px;
  line-height: 1.5;
}

.modal-actions {
  display: flex;
  gap: 10px;
}

.modal-btn {
  flex: 1;
  height: 40px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: opacity 0.15s;
}

.modal-btn:active {
  opacity: 0.8;
}

.modal-btn-cancel {
  background: #f0e4d6;
  color: #8B7355;
}

.modal-btn-confirm {
  background: var(--orange);
  color: #fff;
}
</style>
