<template>
  <div class="home-page">
    <div class="search-bar" @click="router.push('/search')">
      <svg viewBox="0 0 24 24" width="18" height="18" fill="none">
        <circle cx="11" cy="11" r="7" stroke="#C4B5AA" stroke-width="2"/>
        <path d="M16.5 16.5l5 5" stroke="#C4B5AA" stroke-width="2" stroke-linecap="round"/>
      </svg>
      <span class="search-placeholder">搜索菜谱、食材...</span>
    </div>

    <div class="tabs">
      <span class="tab-item" :class="{ active: activeTab === 'recommend' }" @click="activeTab = 'recommend'">推荐</span>
      <span class="tab-item" :class="{ active: activeTab === 'follow' }" @click="activeTab = 'follow'">关注</span>
      <span class="tab-item" :class="{ active: activeTab === 'shop' }" @click="activeTab = 'shop'">商店</span>
    </div>

    <!-- 推荐 -->
    <div v-if="activeTab === 'recommend'" class="tab-content">
      <div v-if="todayRecommend" class="today-card" @click="goDetail(todayRecommend.recipeId)">
        <div class="today-img-wrap">
          <img class="today-img" :src="getCover(todayRecommend.coverImages)" @error="onImgError" />
          <div class="today-overlay"></div>
          <div class="today-content">
            <span class="today-tag">今日推荐</span>
            <div class="today-title-row">
              <div class="today-title">{{ todayRecommend.title }}</div>
              <span class="today-btn" @click.stop="goDetail(todayRecommend.recipeId)">立即查看 <svg viewBox="0 0 16 16" width="12" height="12" fill="none" style="vertical-align: middle"><path d="M6 4l4 4-4 4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/></svg></span>
            </div>
            <div class="today-sub">{{ todayRecommend.cookTime || '30分钟' }} / {{ formatCount(todayRecommend.favoriteCount) }} 收藏</div>
          </div>
        </div>
      </div>

      <div class="section-header">
        <span class="section-title">精选食谱</span>
        <span class="section-more">查看全部 ›</span>
      </div>
      <div class="recipe-grid">
        <div v-for="item in featuredRecipes" :key="item.recipeId" class="recipe-card" @click="goDetail(item.recipeId)">
          <div class="recipe-img-wrap">
            <img class="recipe-img" :src="getCover(item.coverImages)" @error="onImgError" />
            <div class="recipe-img-overlay"></div>
          </div>
          <div class="recipe-body">
            <div class="recipe-name">{{ item.title }}</div>
            <div class="recipe-meta">{{ item.cookTime || '30分钟' }} · {{ formatCount(item.favoriteCount) }}收藏</div>
            <div class="recipe-author">
              <img v-if="item.authorAvatar" :src="item.authorAvatar" class="recipe-author-avatar" @error="onAvatarError" />
              <span v-else class="recipe-author-avatar recipe-author-placeholder">{{ (item.authorName || '用')[0] }}</span>
              <span class="recipe-author-name">{{ item.authorName || '用户' + item.authorId }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 关注 -->
    <div v-else-if="activeTab === 'follow'" class="tab-content">
      <div v-if="followRecipes.length === 0" class="empty-state">
        <svg viewBox="0 0 80 80" width="60" height="60" fill="none">
          <circle cx="40" cy="40" r="38" stroke="#E8DDD2" stroke-width="2" stroke-dasharray="6 4"/>
          <path d="M40 20v40M20 40h40" stroke="#E0D5C8" stroke-width="2" stroke-linecap="round"/>
        </svg>
        <p class="empty-text">{{ currentUser ? '你关注的人还没有发布菜谱' : '登录后查看你关注的博主动态' }}</p>
      </div>
      <div v-else class="feed-list">
        <div v-for="item in followRecipes" :key="item.recipeId" class="feed-card" @click="goDetail(item.recipeId)">
          <div class="feed-header">
            <div v-if="item.authorAvatar" class="feed-avatar">
              <img :src="item.authorAvatar" class="feed-avatar-img" @error="onAvatarError" />
            </div>
            <div v-else class="feed-avatar feed-avatar-placeholder">{{ (item.authorName || '用')[0] }}</div>
            <div class="feed-user">
              <span class="feed-nick">{{ item.authorName || '用户' + item.authorId }}</span>
              <span class="feed-time">刚刚</span>
            </div>
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
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path d="M20.84 4.61a5.5 5.5 0 00-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 00-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 000-7.78z" stroke="#C4B5AA" stroke-width="1.6" fill="none"/>
              </svg>
              {{ formatCount(item.favoriteCount) }}
            </span>
            <span class="feed-action">
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z" stroke="#C4B5AA" stroke-width="1.6" fill="none"/>
              </svg>
              {{ item.commentCount || 0 }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 商店 -->
    <div v-else class="tab-content">
      <div class="shop-section-title">
        <span>每日上新</span>
        <span class="shop-badge">NEW</span>
      </div>
      <div v-if="recentProducts.length === 0" class="empty-state">
        <p class="empty-text">暂无上新商品</p>
      </div>
      <div class="daily-scroll" v-else>
        <div v-for="item in recentProducts" :key="item.id" class="daily-card">
          <div class="daily-img-wrap">
            <img v-if="item.coverImage" :src="item.coverImage" class="daily-img" />
            <div v-else class="daily-img-bg"></div>
          </div>
          <div class="daily-info">
            <div class="daily-name">{{ item.name }}</div>
            <div class="daily-price">{{ item.price != null ? '¥' + item.price : '--' }}</div>
          </div>
        </div>
      </div>

      <div class="shop-section-title">七天销量榜</div>
      <div class="sales-tabs">
        <span v-for="cat in salesCategories" :key="cat"
              class="sales-tab" :class="{ active: activeSalesCat === cat }"
              @click="switchSalesCat(cat)">{{ cat }}</span>
      </div>
      <div v-if="filteredSalesProducts.length === 0" class="empty-state">
        <p class="empty-text">暂无商品</p>
      </div>
      <div class="sales-list" v-else>
        <div v-for="(item, idx) in filteredSalesProducts" :key="item.id" class="sales-card">
          <div class="sales-rank" :class="{ 'rank-top': idx < 3 }">{{ idx + 1 }}</div>
          <div class="sales-img-wrap">
            <img v-if="item.coverImage" :src="item.coverImage" class="sales-img" />
          </div>
          <div class="sales-info">
            <div class="sales-name">{{ item.name }}</div>
            <div class="sales-meta">
              <span class="sales-price">{{ item.price != null ? '¥' + item.price : '--' }}</span>
              <span class="sales-count">7天售{{ item.sales || 0 }}</span>
            </div>
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
  background: var(--gradient-page);
  padding: 0 var(--container-padding) 70px;
}

/* search */
.search-bar {
  display: flex;
  align-items: center;
  height: 44px;
  background: #fff;
  border-radius: var(--radius-full);
  padding: 0 18px;
  margin-top: 14px;
  gap: 10px;
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-light);
  cursor: pointer;
  transition: all 0.25s;
}

.search-bar:active {
  box-shadow: var(--shadow-sm);
  border-color: var(--primary-lighter);
}

.search-placeholder {
  font-family: var(--font-body);
  font-size: 14px;
  color: var(--text-placeholder);
}

/* tabs */
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
  position: relative;
}

.tab-item.active {
  color: var(--primary);
  border-bottom-color: var(--primary-lighter);
  font-weight: 800;
}

.tab-content {
  margin-top: 10px;
}

/* today's pick */
.today-card {
  position: relative;
  border-radius: var(--radius-xl);
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.35s var(--ease-smooth);
  box-shadow: var(--shadow-md);
}

.today-card:active { transform: scale(0.975); }

.today-img-wrap {
  position: relative;
}

.today-img {
  width: 100%;
  height: 280px;
  object-fit: cover;
  background: linear-gradient(135deg, #E8E0D5, #DDD4C5);
  transition: transform 0.5s var(--ease-smooth);
}

.today-card:hover .today-img { transform: scale(1.02); }

.today-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg,
    rgba(18, 30, 31, 0.05) 0%,
    rgba(18, 30, 31, 0.02) 30%,
    rgba(18, 30, 31, 0.45) 70%,
    rgba(18, 30, 31, 0.72) 100%);
}

.today-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 24px 20px 20px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.today-tag {
  display: inline-block;
  font-family: var(--font-heading);
  font-size: 11px;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(135deg, #f09055, #e8783d);
  padding: 5px 14px;
  border-radius: 20px;
  letter-spacing: 1.5px;
  align-self: flex-start;
  box-shadow: 0 2px 14px rgba(225, 115, 45, 0.35);
}

.today-title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
}

.today-title {
  font-family: var(--font-heading);
  font-size: 21px;
  font-weight: 800;
  color: #fff;
  line-height: 1.25;
  letter-spacing: 0.3px;
  flex: 1;
  min-width: 0;
}

.today-sub {
  font-family: var(--font-body);
  font-size: 13px;
  color: rgba(255, 255, 255, 0.72);
  font-weight: 400;
}

.today-btn {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  font-family: var(--font-heading);
  font-size: 12px;
  font-weight: 600;
  color: var(--primary);
  background: #fff;
  border: none;
  padding: 7px 16px;
  border-radius: 20px;
  letter-spacing: 0.4px;
  transition: all 0.25s;
  white-space: nowrap;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.today-btn:active {
  background: #faf5f0;
  transform: scale(0.94);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

/* section header */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 26px 0 14px;
}

.section-title {
  font-family: var(--font-heading);
  font-size: 18px;
  font-weight: 800;
  color: var(--text-primary);
  letter-spacing: 0.5px;
}

.section-more {
  font-family: var(--font-heading);
  font-size: 13px;
  color: var(--primary-lighter);
  font-weight: 700;
  cursor: pointer;
}

/* recipe grid */
.recipe-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.recipe-card {
  background: #fff;
  border-radius: var(--radius-md);
  overflow: hidden;
  box-shadow: 0 2px 16px rgba(255, 122, 51, 0.08), 0 1px 3px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: all 0.25s var(--ease-smooth);
  border: 1px solid rgba(255, 122, 51, 0.05);
}

.recipe-card:active { transform: scale(0.95); }

.recipe-img-wrap {
  position: relative;
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

.recipe-img-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, transparent 60%, rgba(0,0,0,0.02));
}

.recipe-body {
  padding: 10px 12px 12px;
}

.recipe-name {
  font-family: var(--font-heading);
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 2px;
}

.recipe-meta {
  font-family: var(--font-body);
  font-size: 11px;
  color: var(--text-muted);
  margin-bottom: 8px;
}

.recipe-author {
  display: flex;
  align-items: center;
  gap: 6px;
}

.recipe-author-avatar {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.recipe-author-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--gradient-primary);
  color: #fff;
  font-family: var(--font-heading);
  font-size: 9px;
  font-weight: 700;
}

.recipe-author-name {
  font-family: var(--font-body);
  font-size: 11px;
  color: var(--text-secondary);
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* empty state */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 80px;
  gap: 14px;
}

.empty-text {
  font-family: var(--font-body);
  color: var(--text-muted);
  font-size: 14px;
}

/* feed */
.feed-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feed-card {
  background: #fff;
  border-radius: var(--radius-xl);
  padding: 16px;
  box-shadow: var(--shadow-card);
  cursor: pointer;
  border: 1px solid rgba(255, 122, 51, 0.04);
  transition: all 0.2s;
}

.feed-card:active { transform: scale(0.98); }

.feed-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.feed-avatar {
  width: 36px; height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background: var(--gradient-primary);
  color: #fff;
  font-family: var(--font-heading);
  font-size: 15px;
  font-weight: 700;
  box-shadow: var(--shadow-primary);
  overflow: hidden;
}

.feed-avatar-img {
  width: 100%; height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.feed-user {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.feed-nick {
  font-family: var(--font-heading);
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary);
}

.feed-time {
  font-family: var(--font-body);
  font-size: 11px;
  color: var(--text-muted);
}

.feed-desc {
  font-family: var(--font-body);
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.65;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.feed-images {
  display: flex;
  gap: 6px;
  border-radius: var(--radius-md);
  overflow: hidden;
  margin-bottom: 12px;
}

.feed-img {
  width: calc(50% - 3px);
  height: 140px;
  object-fit: cover;
  background: linear-gradient(135deg, #F5F0E8, #EDE4D5);
}

.feed-img-single {
  width: 100%;
  height: 200px;
}

.feed-actions {
  display: flex;
  gap: 28px;
}

.feed-action {
  display: flex;
  align-items: center;
  gap: 5px;
  font-family: var(--font-body);
  font-size: 13px;
  color: var(--text-muted);
  font-weight: 500;
}

/* shop */
.shop-section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-family: var(--font-heading);
  font-size: 17px;
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 14px;
  letter-spacing: 0.5px;
}

.shop-badge {
  font-family: var(--font-heading);
  font-size: 10px;
  font-weight: 800;
  color: #fff;
  background: var(--gradient-primary);
  padding: 2px 8px;
  border-radius: var(--radius-xs);
  letter-spacing: 1px;
}

.daily-scroll {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 6px;
  margin-bottom: 28px;
  -webkit-overflow-scrolling: touch;
}

.daily-scroll::-webkit-scrollbar { display: none; }

.daily-card {
  flex: 0 0 120px;
  background: #fff;
  border-radius: var(--radius-md);
  overflow: hidden;
  box-shadow: var(--shadow-xs);
  border: 1px solid rgba(255, 122, 51, 0.04);
  transition: all 0.2s;
}

.daily-card:active { transform: scale(0.95); }

.daily-img-wrap {
  width: 120px;
  height: 100px;
  background: linear-gradient(135deg, #F5F0E8, #EDE4D5);
  overflow: hidden;
}

.daily-img-bg {
  width: 100%; height: 100%;
  background: linear-gradient(135deg, #F5F0E8, #EDE4D5);
}

.daily-img {
  width: 100%; height: 100%;
  object-fit: cover;
}

.daily-info {
  padding: 8px 10px 10px;
}

.daily-name {
  font-family: var(--font-heading);
  font-size: 12px;
  font-weight: 600;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.daily-price {
  font-family: var(--font-heading);
  font-size: 14px;
  font-weight: 800;
  color: var(--primary);
  margin-top: 4px;
}

/* sales */
.sales-tabs {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.sales-tab {
  font-family: var(--font-heading);
  font-size: 13px;
  color: var(--text-secondary);
  padding: 7px 18px;
  border-radius: var(--radius-full);
  background: var(--primary-bg);
  cursor: pointer;
  white-space: nowrap;
  font-weight: 600;
  transition: all 0.25s;
}

.sales-tab.active {
  background: var(--gradient-primary);
  color: #fff;
  font-weight: 700;
  box-shadow: var(--shadow-primary);
}

.sales-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.sales-card {
  display: flex;
  align-items: center;
  gap: 14px;
  background: #fff;
  border-radius: var(--radius-md);
  padding: 12px;
  box-shadow: var(--shadow-xs);
  border: 1px solid rgba(255, 122, 51, 0.04);
  transition: all 0.2s;
}

.sales-card:active { transform: scale(0.98); }

.sales-rank {
  width: 22px;
  font-family: var(--font-heading);
  font-size: 14px;
  font-weight: 800;
  color: var(--text-muted);
  text-align: center;
  flex-shrink: 0;
}

.sales-rank.rank-top {
  color: var(--primary);
}

.sales-img-wrap {
  width: 75px;
  height: 75px;
  border-radius: var(--radius-sm);
  background: linear-gradient(135deg, #F5F0E8, #EDE4D5);
  flex-shrink: 0;
  overflow: hidden;
}

.sales-img {
  width: 100%; height: 100%;
  object-fit: cover;
}

.sales-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.sales-name {
  font-family: var(--font-heading);
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.sales-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.sales-price {
  font-family: var(--font-heading);
  font-size: 15px;
  font-weight: 800;
  color: var(--primary);
}

.sales-count {
  font-family: var(--font-body);
  font-size: 11px;
  color: var(--text-muted);
}
</style>
