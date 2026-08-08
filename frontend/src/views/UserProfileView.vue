<template>
  <div class="profile-page">
    <!-- 导航 -->
    <div class="nav-bar">
      <div class="back-btn" @click="$goBack()">
        <svg viewBox="0 0 24 24" width="22" height="22">
          <path d="M15 18l-6-6 6-6" stroke="#5a524c" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <span class="nav-title">用户主页</span>
      <div class="nav-placeholder"></div>
    </div>

    <div v-if="loading" class="loading-wrap"><p>加载中...</p></div>

    <template v-else-if="profileUser">
      <!-- 用户信息卡 -->
      <div class="user-card">
        <div class="avatar-wrap">
          <img v-if="profileUser.avatar && !avatarFailed" :src="profileUser.avatar" class="avatar-img" @error="avatarFailed = true" />
          <div v-else class="avatar-placeholder">{{ (profileUser.nickName || profileUser.username || '用')[0] }}</div>
        </div>
        <div class="user-name">{{ profileUser.nickName || profileUser.username || '用户' + profileUser.userId }}</div>
        <div class="recipe-count">发布了 {{ recipes.length }} 个菜谱</div>
        <button
          v-if="!isSelf && followChecked"
          class="follow-btn"
          :class="{ followed: isFollowed }"
          @click="handleFollow"
        >{{ isFollowed ? '已关注' : '+ 关注' }}</button>
      </div>

      <!-- 菜谱列表 -->
      <div class="section-title">
        <span class="section-dot"></span>
        TA的菜谱
      </div>

      <div v-if="recipes.length === 0" class="empty-state">
        <p class="empty-text">暂无发布的菜谱</p>
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
    </template>

    <div v-else class="loading-wrap"><p>用户不存在</p></div>

    <transition name="fade">
      <div v-if="toast.show" class="toast" :class="toast.type">{{ toast.msg }}</div>
    </transition>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { userStore } from '../store/user'
import { getUserDetail } from '../api/auth'
import { getOwnRecipes } from '../api/recipe'
import { follow, unfollow, checkFollowing } from '../api/follow'

const route = useRoute()
const router = useRouter()
const profileUser = ref(null)
const recipes = ref([])
const loading = ref(true)
const isFollowed = ref(false)
const followChecked = ref(false)
const avatarFailed = ref(false)
const toast = reactive({ show: false, msg: '', type: 'success' })

const currentUser = computed(() => userStore.user)
const isSelf = computed(() => currentUser.value?.userId == route.params.id)

onMounted(async () => {
  const userId = route.params.id
  if (!userId) { loading.value = false; return }
  try {
    const [userRes, recipeRes] = await Promise.all([
      getUserDetail(userId),
      getOwnRecipes(userId)
    ])
    profileUser.value = userRes.data
    recipes.value = recipeRes.data || []
    if (currentUser.value && !isSelf.value) {
      try {
        const followRes = await checkFollowing(currentUser.value.userId, userId)
        isFollowed.value = followRes.data === true
      } catch { /* ignore */ }
    }
    followChecked.value = true
  } catch {
    profileUser.value = null
  } finally {
    loading.value = false
  }
})

function showToast(msg, type = 'success') {
  toast.msg = msg
  toast.type = type
  toast.show = true
  setTimeout(() => { toast.show = false }, 2000)
}

async function handleFollow() {
  if (!currentUser.value) { showToast('请先登录', 'error'); return }
  const myId = currentUser.value.userId
  const authorId = route.params.id
  try {
    if (isFollowed.value) {
      const res = await unfollow(myId, authorId)
      if (res.data === true) isFollowed.value = false
    } else {
      const res = await follow(myId, authorId)
      if (res.data === true) isFollowed.value = true
    }
  } catch (e) {
    console.error('关注操作失败:', e)
  }
}

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
.profile-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: var(--gradient-page);
  padding-top: env(safe-area-inset-top, 0px);
  padding-bottom: 20px;
}

.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: rgba(255,255,255,0.9);
  backdrop-filter: blur(20px);
  position: sticky;
  top: 0;
  z-index: 10;
}

.back-btn { width: 36px; height: 36px; display: flex; align-items: center; justify-content: center; cursor: pointer; }
.nav-title { font-family: var(--font-heading); font-size: 17px; font-weight: 700; color: var(--text-primary); }
.nav-placeholder { width: 36px; }

.loading-wrap {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 60vh;
  color: var(--text-muted);
  font-size: 15px;
}

/* 用户信息卡 */
.user-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 16px 24px;
  background: #fff;
  margin: 0 16px 12px;
  border-radius: 16px;
  box-shadow: var(--shadow-xs);
}

.avatar-wrap {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  overflow: hidden;
  margin-bottom: 12px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--gradient-primary);
  color: #fff;
  font-size: 28px;
  font-weight: 700;
}

.user-name {
  font-family: var(--font-heading);
  font-size: 20px;
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.recipe-count {
  font-size: 13px;
  color: var(--text-muted);
  margin-bottom: 14px;
}

.follow-btn {
  padding: 8px 28px;
  border-radius: 20px;
  background: var(--gradient-primary);
  color: #fff;
  font-size: 14px;
  font-weight: 700;
  box-shadow: var(--shadow-primary);
  transition: all 0.2s;
  cursor: pointer;
  border: none;
}

.follow-btn.followed {
  background: #e8e3db;
  color: var(--text-muted);
  box-shadow: none;
}

.follow-btn:active { transform: scale(0.94); }

/* 菜谱 */
.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-family: var(--font-heading);
  font-size: 17px;
  font-weight: 800;
  color: var(--text-primary);
  padding: 8px 16px 12px;
  letter-spacing: 0.5px;
}

.section-dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: var(--primary);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 80px;
  gap: 14px;
}

.empty-text { color: var(--text-muted); font-size: 14px; }

.recipe-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  padding: 0 16px;
}

.recipe-card {
  background: #fff;
  border-radius: var(--radius-md);
  overflow: hidden;
  box-shadow: var(--shadow-xs);
  cursor: pointer;
  transition: all 0.25s;
  border: 1px solid rgba(255, 122, 51, 0.04);
}

.recipe-card:active { transform: scale(0.95); }

.recipe-img-wrap { overflow: hidden; }

.recipe-img {
  width: 100%;
  height: 125px;
  object-fit: cover;
  background: linear-gradient(135deg, #F5F0E8, #EDE4D5);
  transition: transform 0.4s;
}

.recipe-card:active .recipe-img { transform: scale(1.05); }

.recipe-body { padding: 10px 12px 12px; }

.recipe-name {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 2px;
}

.recipe-meta { font-size: 11px; color: var(--text-muted); }

.toast {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  padding: 14px 36px;
  border-radius: 14px;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  z-index: 999;
  pointer-events: none;
  letter-spacing: 1px;
  font-family: var(--font-heading);
}

.toast.success { background: rgba(18, 30, 31, 0.88); }
.toast.error { background: rgba(180, 60, 20, 0.9); }

.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
