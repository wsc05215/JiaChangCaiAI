<template>
  <div class="favorites-page">
    <div class="search-bar">
      <svg viewBox="0 0 24 24" width="18" height="18" fill="none">
        <circle cx="11" cy="11" r="7" stroke="#C4B5AA" stroke-width="2"/>
        <path d="M16.5 16.5l5 5" stroke="#C4B5AA" stroke-width="2" stroke-linecap="round"/>
      </svg>
      <input class="search-input" type="search" placeholder="搜索收藏的菜谱..." v-model="searchText" />
    </div>

    <div class="tabs">
      <span class="tab-btn" :class="{ active: activeTab === 'recipe' }" @click="activeTab = 'recipe'">全部菜谱</span>
      <span class="tab-btn" :class="{ active: activeTab === 'menu' }" @click="activeTab = 'menu'">菜单</span>
    </div>

    <div v-if="activeTab === 'recipe'" class="tab-content">
      <div v-if="!currentUser" class="empty-state">
        <svg viewBox="0 0 80 80" width="60" height="60" fill="none">
          <circle cx="40" cy="40" r="38" stroke="#E8DDD2" stroke-width="2" stroke-dasharray="6 4"/>
          <rect x="28" y="20" width="24" height="40" rx="3" stroke="#E0D5C8" stroke-width="2"/>
          <path d="M33 33h14M33 39h10" stroke="#E0D5C8" stroke-width="1.5" stroke-linecap="round"/>
        </svg>
        <p class="empty-text">登录后查看收藏的菜谱</p>
      </div>
      <div v-else-if="loading" class="empty-state"><p class="empty-text">加载中...</p></div>
      <div v-else-if="favoriteRecipes.length === 0" class="empty-state">
        <svg viewBox="0 0 80 80" width="60" height="60" fill="none">
          <circle cx="40" cy="40" r="38" stroke="#E8DDD2" stroke-width="2" stroke-dasharray="6 4"/>
          <path d="M40 28c-8 0-12 6-12 6 0-3-4-6-8-6-5 0-8 4-8 8 0 10 16 20 16 20s16-10 16-20c0-4-3-8-8-8z" fill="#F0E4D6" stroke="#E0D5C8" stroke-width="2"/>
        </svg>
        <p class="empty-text">还没有收藏菜谱</p>
      </div>
      <div v-else-if="filteredRecipes.length === 0 && searchText.trim()" class="empty-state">
        <p class="empty-text">没有找到相关菜谱</p>
      </div>
      <div v-else class="favorite-list">
        <div v-for="item in filteredRecipes" :key="item.recipeId" class="fav-card" @click="goDetail(item.recipeId)">
          <div class="fav-img-wrap">
            <img :src="getCover(item.coverImages)" class="fav-img" @error="onImgError" />
            <div class="score-badge" v-if="item.rating != null">
              <svg viewBox="0 0 24 24" width="10" height="10" fill="#FFD700"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
              <span>{{ item.rating }}</span>
            </div>
          </div>
          <div class="fav-info">
            <div class="fav-title">{{ item.title }}</div>
            <div class="fav-meta">{{ item.rating != null ? item.rating + '分  ' : '' }}{{ item.favoriteCount || 0 }}人收藏</div>
            <div class="fav-author">
              <svg viewBox="0 0 24 24" width="13" height="13">
                <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" stroke="#C4B5AA" stroke-width="1.5" fill="none"/>
                <circle cx="12" cy="7" r="4" stroke="#C4B5AA" stroke-width="1.5" fill="none"/>
              </svg>
              <span>{{ item.authorName || '用户' + item.authorId }}</span>
            </div>
          </div>
          <button class="fav-remove-btn" @click.stop="handleRemoveFavorite(item)" title="取消收藏">
            <svg viewBox="0 0 24 24" width="14" height="14">
              <path d="M18 6L6 18M6 6l12 12" stroke="#C4B5AA" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>
        </div>
      </div>
    </div>

    <div v-else class="tab-content">
      <div class="menu-card" @click="goMenuPlan">
        <div class="menu-icon-wrap">
          <svg viewBox="0 0 24 24" width="40" height="40" fill="none">
            <rect x="3" y="3" width="18" height="18" rx="4" stroke="#C4B5AA" stroke-width="1.6"/>
            <path d="M3 9h18M9 21V9" stroke="#C4B5AA" stroke-width="1.6"/>
          </svg>
        </div>
        <div class="menu-title">自定义计划</div>
        <div class="menu-desc">提前规划未来三餐</div>
      </div>
    </div>

    <div v-if="showConfirm" class="modal-overlay" @click.self="showConfirm = false">
      <div class="modal-box">
        <div class="modal-icon">&#x1F494;</div>
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
  try { const arr = JSON.parse(images); return arr[0] || '' } catch { return images }
}

function onImgError(e) { e.target.style.display = 'none' }

function goDetail(id) { router.push('/recipe/' + id) }
function goMenuPlan() { router.push('/menu-plan') }

function handleRemoveFavorite(item) {
  removingItem.value = item
  showConfirm.value = true
}

async function confirmRemove() {
  const item = removingItem.value
  if (!item || !currentUser.value) { showConfirm.value = false; return }
  try {
    await removeFavorite(currentUser.value.userId, item.recipeId)
    favoriteRecipes.value = favoriteRecipes.value.filter(r => r.recipeId !== item.recipeId)
  } catch { /* ignore */ } finally {
    showConfirm.value = false
    removingItem.value = null
  }
}
</script>

<style scoped>
.favorites-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: var(--gradient-page);
  padding: max(env(safe-area-inset-top), 0) 15px 70px;
}

.search-bar {
  display: flex;
  align-items: center;
  height: 42px;
  background: #fff;
  border-radius: var(--radius-full);
  padding: 0 18px;
  margin-top: 14px;
  gap: 10px;
  box-shadow: var(--shadow-xs);
  border: 1px solid var(--border-light);
  transition: all 0.2s;
}

.search-bar:focus-within {
  border-color: var(--primary);
  box-shadow: 0 0 0 5px rgba(230,126,34,0.06);
}

.search-input {
  flex: 1;
  font-size: 14px;
  color: var(--text-primary);
  background: transparent;
  border: none; outline: none;
}

.search-input::placeholder { color: var(--text-placeholder); }

.tabs {
  display: flex;
  gap: 12px;
  margin-top: 18px;
}

.tab-btn {
  font-size: 14px;
  color: var(--text-secondary);
  padding: 9px 24px;
  border-radius: var(--radius-full);
  background: #fff;
  cursor: pointer;
  transition: all 0.25s;
  font-weight: 700;
  box-shadow: var(--shadow-xs);
}

.tab-btn.active {
  background: var(--gradient-primary);
  color: #fff;
  box-shadow: var(--shadow-primary);
}

.tab-content { margin-top: 16px; }

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 80px;
  gap: 14px;
}

.empty-text { color: var(--text-muted); font-size: 14px; }

.favorite-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.fav-card {
  display: flex;
  gap: 14px;
  background: #fff;
  border-radius: var(--radius-xl);
  padding: 12px;
  box-shadow: var(--shadow-xs);
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
  border: 1px solid rgba(0,0,0,0.03);
}

.fav-card:active { transform: scale(0.98); }

.fav-img-wrap {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: var(--radius-md);
  overflow: hidden;
  flex-shrink: 0;
  background: linear-gradient(135deg, #F5F0E8, #EDE4D5);
}

.fav-img {
  width: 100%; height: 100%;
  object-fit: cover;
}

.score-badge {
  position: absolute;
  top: 8px; left: 8px;
  display: flex;
  align-items: center;
  gap: 3px;
  background: rgba(30,21,15,0.6);
  backdrop-filter: blur(8px);
  color: #fff;
  font-size: 11px; font-weight: 700;
  padding: 3px 9px;
  border-radius: var(--radius-xs);
}

.fav-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-width: 0;
}

.fav-title {
  font-size: 16px;
  font-weight: 800;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 8px;
  padding-right: 28px;
}

.fav-meta {
  font-size: 12px;
  color: var(--text-muted);
  margin-bottom: 18px;
}

.fav-author {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: var(--text-placeholder);
}

.fav-remove-btn {
  position: absolute;
  top: 12px; right: 12px;
  width: 30px; height: 30px;
  border-radius: 50%;
  background: rgba(0,0,0,0.03);
  border: none;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer;
  z-index: 2;
  transition: all 0.2s;
}

.fav-remove-btn:active {
  background: rgba(230,126,34,0.08);
}

/* menu */
.menu-card {
  background: #fff;
  border-radius: var(--radius-xl);
  padding: 36px 24px;
  text-align: center;
  box-shadow: var(--shadow-xs);
  border: 1px solid rgba(0,0,0,0.03);
  cursor: pointer;
  transition: all 0.2s;
}
.menu-card:active { transform: scale(0.98); }

.menu-icon-wrap { margin-bottom: 16px; }
.menu-title { font-size: 18px; font-weight: 800; color: var(--text-primary); margin-bottom: 6px; }
.menu-desc { font-size: 13px; color: var(--text-muted); }


/* modal */
.modal-overlay {
  position: fixed; inset: 0;
  background: rgba(30,21,15,0.4);
  backdrop-filter: blur(6px);
  display: flex; align-items: center; justify-content: center;
  z-index: 1000;
}

.modal-box {
  width: 290px;
  background: #fff;
  border-radius: var(--radius-xl);
  padding: 30px 22px 22px;
  text-align: center;
  box-shadow: var(--shadow-lg);
}

.modal-icon { font-size: 32px; margin-bottom: 10px; }
.modal-title { font-size: 18px; font-weight: 800; color: var(--text-primary); margin-bottom: 8px; }
.modal-desc { font-size: 14px; color: var(--text-secondary); margin-bottom: 24px; line-height: 1.5; }

.modal-actions { display: flex; gap: 12px; }
.modal-btn {
  flex: 1; height: 44px; border-radius: var(--radius-full);
  font-size: 14px; font-weight: 700; border: none; cursor: pointer;
  transition: all 0.2s;
}
.modal-btn:active { transform: scale(0.96); }
.modal-btn-cancel { background: var(--divider); color: var(--text-secondary); }
.modal-btn-confirm {
  background: var(--gradient-primary);
  color: #fff;
  box-shadow: var(--shadow-primary);
}
</style>
