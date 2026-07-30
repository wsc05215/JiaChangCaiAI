<template>
  <div class="home-page">
    <!-- 搜索栏 -->
    <div class="search-bar" @click="router.push('/search')">
      <svg class="search-icon" viewBox="0 0 24 24" width="16" height="16">
        <circle cx="11" cy="11" r="7" stroke="#B99E8E" stroke-width="2" fill="none"/>
        <path d="M16.5 16.5l5 5" stroke="#B99E8E" stroke-width="2" stroke-linecap="round"/>
      </svg>
      <input class="search-input" type="search" placeholder="搜索" readonly />
    </div>

    <!-- Tab 导航 -->
    <div class="tabs">
      <span class="tab-item" :class="{ active: activeTab === 'recommend' }" @click="activeTab = 'recommend'">推荐</span>
      <span class="tab-item" :class="{ active: activeTab === 'follow' }" @click="activeTab = 'follow'">关注</span>
      <span class="tab-item" :class="{ active: activeTab === 'shop' }" @click="activeTab = 'shop'">商店</span>
    </div>

    <!-- 推荐 -->
    <div v-if="activeTab === 'recommend'" class="tab-content">
      <!-- 今日推荐 -->
      <div v-if="todayRecommend" class="today-card" @click="goDetail(todayRecommend.recipeId)">
        <img class="today-img" :src="getCover(todayRecommend.coverImages)" @error="onImgError" />
        <div class="today-badge">今日推荐</div>
        <div class="today-info">
          <div class="today-title">{{ todayRecommend.title }}</div>
          <div class="today-meta">{{ todayRecommend.cookTime || '' }} · 收藏{{ formatCount(todayRecommend.favoriteCount) }}</div>
        </div>
        <div class="today-btn">立即查看</div>
      </div>

      <!-- 精选食谱 -->
      <div class="section-header">
        <span class="section-title">精选食谱</span>
        <span class="section-more">查看全部</span>
      </div>
      <div class="recipe-grid">
        <div v-for="item in featuredRecipes" :key="item.recipeId" class="recipe-card" @click="goDetail(item.recipeId)">
          <img class="recipe-img" :src="getCover(item.coverImages)" @error="onImgError" />
          <div class="recipe-name">{{ item.title }}</div>
          <div class="recipe-meta">{{ item.cookTime || '' }} · {{ formatCount(item.favoriteCount) }}收藏</div>
        </div>
      </div>
    </div>

    <!-- 关注 -->
    <div v-else-if="activeTab === 'follow'" class="tab-content">
      <div v-if="followRecipes.length === 0" class="empty-text">
        <p v-if="!currentUser">登录后查看你关注的博主动态</p>
        <p v-else>你关注的人还没有发布菜谱</p>
      </div>
      <div v-else class="feed-list">
        <div v-for="item in followRecipes" :key="item.recipeId" class="feed-card" @click="goDetail(item.recipeId)">
          <div class="feed-header">
            <img v-if="item.authorAvatar" :src="item.authorAvatar" class="feed-avatar" @error="onAvatarError" />
            <span v-else class="feed-avatar-placeholder">{{ (item.authorName || '用户')[0] }}</span>
            <span class="feed-nick">{{ item.authorName || '用户' + item.authorId }}</span>
          </div>
          <div class="feed-desc">{{ item.description }}</div>
          <div class="feed-images" v-if="getAllCovers(item.coverImages).length > 0">
            <img
              v-for="(img, i) in getAllCovers(item.coverImages).slice(0, 3)"
              :key="i"
              :src="img"
              class="feed-img"
              :class="{ 'feed-img-single': getAllCovers(item.coverImages).length === 1 }"
              @error="onImgError"
            />
          </div>
          <div class="feed-actions">
            <span class="feed-action">
              <svg viewBox="0 0 24 24" width="18" height="18">
                <path d="M20.84 4.61a5.5 5.5 0 00-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 00-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 000-7.78z" stroke="#B99E8E" stroke-width="1.5" fill="none"/>
              </svg>
              {{ formatCount(item.favoriteCount) }}
            </span>
            <span class="feed-action">
              <svg viewBox="0 0 24 24" width="18" height="18">
                <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z" stroke="#B99E8E" stroke-width="1.5" fill="none"/>
              </svg>
              {{ item.commentCount || 0 }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 商店 -->
    <div v-else class="tab-content">
      <!-- 每日上新 -->
      <div class="shop-section-title">每日上新</div>
      <div v-if="recentProducts.length === 0" class="empty-text">暂无上新商品</div>
      <div class="daily-scroll" v-else>
        <div v-for="item in recentProducts" :key="item.id" class="daily-card">
          <div class="daily-img-wrap">
            <img v-if="item.coverImage" :src="item.coverImage" class="daily-img" />
          </div>
          <div class="daily-name">{{ item.name }}</div>
          <div class="daily-price">{{ item.price != null ? '￥' + item.price : '--' }}</div>
        </div>
      </div>

      <!-- 七天销量榜 -->
      <div class="shop-section-title">七天销量榜</div>
      <div class="sales-tabs">
        <span v-for="cat in salesCategories" :key="cat"
              class="sales-tab" :class="{ active: activeSalesCat === cat }"
              @click="switchSalesCat(cat)">{{ cat }}</span>
      </div>
      <div v-if="filteredSalesProducts.length === 0" class="empty-text">暂无商品</div>
      <div class="sales-list" v-else>
        <div v-for="item in filteredSalesProducts" :key="item.id" class="sales-card">
          <div class="sales-img-wrap">
            <img v-if="item.coverImage" :src="item.coverImage" class="sales-img" />
          </div>
          <div class="sales-info">
            <div class="sales-name">{{ item.name }}</div>
            <div class="sales-price">{{ item.price != null ? '￥' + item.price : '--' }}</div>
            <div class="sales-count">7天销量{{ item.sales || 0 }}</div>
          </div>
        </div>
      </div>
    </div>

    <AppTabbar />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { userStore } from '../store/user'
import { getAllRecipes, getFollowRecipes } from '../api/recipe'
import { getRecentProducts, getSalesRanking, getSalesRankingByCategory } from '../api/shop'
import AppTabbar from '../components/AppTabbar.vue'

const router = useRouter()
const activeTab = ref('recommend')
const recipes = ref([])
const followRecipes = ref([])
const currentUser = computed(() => userStore.user)

// 商店
const recentProducts = ref([])
const salesProducts = ref([])
const activeSalesCat = ref('全部')
const salesCategories = ['全部', '果蔬', '肉蛋', '海鲜', '速食']

const todayRecommend = computed(() => recipes.value.length > 0 ? recipes.value[0] : null)
const featuredRecipes = computed(() => recipes.value.slice(1))

const filteredSalesProducts = computed(() => {
  if (activeSalesCat.value === '全部') return salesProducts.value
  return salesProducts.value.filter(p => p.category === activeSalesCat.value)
})

onMounted(async () => {
  try {
    const [allRes, followRes] = await Promise.all([
      getAllRecipes(),
      currentUser.value ? getFollowRecipes(currentUser.value.userId) : Promise.resolve({ data: [] })
    ])
    recipes.value = allRes.data || []
    followRecipes.value = followRes.data || []
  } catch {
    recipes.value = []
    followRecipes.value = []
  }
})

watch(activeTab, async (tab) => {
  if (tab === 'follow' && currentUser.value) {
    try {
      const res = await getFollowRecipes(currentUser.value.userId)
      followRecipes.value = res.data || []
    } catch { /* ignore */ }
  }
  if (tab === 'shop') {
    fetchShopData()
  }
})

async function fetchShopData() {
  try {
    const [recentRes, salesRes] = await Promise.all([
      getRecentProducts(),
      getSalesRanking()
    ])
    recentProducts.value = recentRes.data || []
    salesProducts.value = salesRes.data || []
  } catch (e) {
    console.error('获取商店数据失败:', e)
  }
}

async function switchSalesCat(cat) {
  activeSalesCat.value = cat
  try {
    const res = cat === '全部'
      ? await getSalesRanking()
      : await getSalesRankingByCategory(cat)
    salesProducts.value = res.data || []
  } catch (e) {
    console.error('切换分类失败:', e)
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

function formatCount(n) {
  if (!n) return '0'
  if (n >= 1000) return (n / 1000).toFixed(1) + 'k'
  return String(n)
}

function getAllCovers(images) {
  if (!images) return []
  try {
    const arr = JSON.parse(images)
    return Array.isArray(arr) ? arr.filter(Boolean) : []
  } catch {
    return []
  }
}

function onImgError(e) {
  e.target.style.display = 'none'
}

function onAvatarError(e) {
  e.target.style.display = 'none'
}

function goDetail(id) {
  router.push('/recipe/' + id)
}
</script>

<style scoped>
.home-page {
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

/* Tab 导航 */
.tabs {
  display: flex;
  gap: 24px;
  margin-top: 16px;
}

.tab-item {
  font-size: 16px;
  color: #B99E8E;
  cursor: pointer;
  padding-bottom: 6px;
  border-bottom: 2px solid transparent;
  transition: all 0.2s;
}

.tab-item.active {
  color: var(--orange);
  border-bottom-color: var(--orange);
  font-weight: 600;
}

.tab-content {
  margin-top: 14px;
}

/* 今日推荐 */
.today-card {
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  background: var(--white);
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}

.today-img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  background: #f0e4d6;
}

.today-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  background: var(--orange);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  padding: 4px 12px;
  border-radius: 10px;
}

.today-info {
  padding: 14px 14px 8px;
}

.today-title {
  font-size: 18px;
  font-weight: 700;
  color: #333;
}

.today-meta {
  font-size: 12px;
  color: var(--gray);
  margin-top: 4px;
}

.today-btn {
  margin: 0 14px 14px;
  display: inline-block;
  background: var(--orange);
  color: #fff;
  font-size: 13px;
  padding: 8px 24px;
  border-radius: 20px;
  cursor: pointer;
}

.today-btn:active {
  opacity: 0.8;
}

/* 精选食谱 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 20px 0 12px;
}

.section-title {
  font-size: 16px;
  font-weight: 700;
  color: #333;
}

.section-more {
  font-size: 13px;
  color: var(--brown);
}

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

.empty-text {
  text-align: center;
  color: #B99E8E;
  padding-top: 120px;
  font-size: 14px;
}

/* 关注 feed */
.feed-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.feed-card {
  background: var(--white);
  border-radius: 14px;
  padding: 14px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.04);
  cursor: pointer;
}

.feed-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.feed-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
  background: #f0e4d6;
}

.feed-avatar-placeholder {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--orange);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.feed-nick {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.feed-desc {
  font-size: 13px;
  color: #555;
  line-height: 1.5;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.feed-images {
  display: flex;
  gap: 6px;
  border-radius: 10px;
  overflow: hidden;
  margin-bottom: 10px;
}

.feed-img {
  width: calc(50% - 3px);
  height: 140px;
  object-fit: cover;
  background: #f0e4d6;
}

.feed-img-single {
  width: 100%;
  height: 200px;
}

.feed-actions {
  display: flex;
  gap: 20px;
}

.feed-action {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--brown);
}

/* 商店 */
.shop-section-title {
  font-size: 16px;
  font-weight: 700;
  color: #333;
  margin-bottom: 12px;
}

.daily-scroll {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding-bottom: 8px;
  margin-bottom: 24px;
  -webkit-overflow-scrolling: touch;
}

.daily-scroll::-webkit-scrollbar {
  display: none;
}

.daily-card {
  flex: 0 0 110px;
  background: var(--white);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.daily-img-wrap {
  width: 110px;
  height: 90px;
  background: #f0e4d6;
  border-radius: 10px 10px 0 0;
  overflow: hidden;
}

.daily-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.daily-name {
  font-size: 12px;
  font-weight: 600;
  color: #333;
  padding: 6px 8px 2px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.daily-price {
  font-size: 12px;
  color: var(--orange);
  padding: 0 8px 8px;
  font-weight: 600;
}

.sales-tabs {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  margin-bottom: 14px;
}

.sales-tab {
  font-size: 12px;
  color: var(--brown);
  padding: 4px 12px;
  border-radius: 14px;
  background: #f5ebe0;
  cursor: pointer;
  white-space: nowrap;
}

.sales-tab.active {
  background: var(--orange);
  color: #fff;
}

.sales-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.sales-card {
  display: flex;
  gap: 12px;
  background: var(--white);
  border-radius: 12px;
  padding: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.sales-img-wrap {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  background: #f0e4d6;
  flex-shrink: 0;
  overflow: hidden;
}

.sales-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.sales-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 4px;
}

.sales-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.sales-price {
  font-size: 13px;
  color: var(--orange);
  font-weight: 600;
}

.sales-count {
  font-size: 11px;
  color: var(--gray);
}
</style>
