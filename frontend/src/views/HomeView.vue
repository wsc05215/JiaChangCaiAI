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
      <div v-if="carouselRecipes.length > 0" class="carousel" @touchstart="onTouchStart" @touchend="onTouchEnd">
        <div class="carousel-track" ref="trackRef" :style="trackStyle" @transitionend="onTransitionEnd">
          <div v-for="(item, i) in slides" :key="i" class="carousel-slide" @click="goDetail(item.recipeId)">
            <div class="slide-img-wrap">
              <img class="slide-img" :src="getCover(item.coverImages)" @error="onImgError" />
              <div class="slide-shine" :key="'s' + activeDotIndex"></div>
              <div class="slide-overlay"></div>
              <div class="slide-content" :key="'c' + activeDotIndex">
                <span class="slide-tag">{{ activeDotIndex === 0 ? '今日推荐' : activeDotIndex === 1 ? '人气必吃' : '不容错过' }}</span>
                <div class="slide-title">{{ item.title }}</div>
                <div class="slide-meta">{{ item.cookTime || '30分钟' }} · {{ formatCount(item.favoriteCount) }} 收藏</div>
              </div>
            </div>
          </div>
        </div>
        <div class="carousel-dots">
          <span v-for="(_, i) in carouselRecipes" :key="i" class="carousel-dot" :class="{ active: i === activeDotIndex }" @click.stop="goToSlide(i)"></span>
        </div>
      </div>

      <div class="ai-card" @click="router.push('/ai-chat')">
        <div class="ai-card-body">
          <div class="ai-icon-wrap">
            <div class="ai-icon">
              <svg viewBox="0 0 24 24" width="18" height="18" fill="none">
                <defs>
                  <linearGradient id="aig" x1="2" y1="2" x2="22" y2="22">
                    <stop offset="0%" stop-color="#fff"/>
                    <stop offset="100%" stop-color="#e0e0ff"/>
                  </linearGradient>
                </defs>
                <path d="M9.813 15.904L9 18.75l-.813-2.846a4.5 4.5 0 00-3.09-3.09L2.25 12l2.846-.813a4.5 4.5 0 003.09-3.09L9 5.25l.813 2.846a4.5 4.5 0 003.09 3.09L15.75 12l-2.846.813a4.5 4.5 0 00-3.09 3.09zM18.259 11.023L18 11.75l-.259-.727a2.25 2.25 0 00-1.514-1.514L15.5 9.25l.727-.259a2.25 2.25 0 001.514-1.514L18 6.75l.259.727a2.25 2.25 0 001.514 1.514L20.5 9.25l-.727.259a2.25 2.25 0 00-1.514 1.514z" fill="url(#aig)" stroke="none"/>
                <path d="M9.813 15.904L9 18.75l-.813-2.846m1.626 0a4.5 4.5 0 00-3.09-3.09L2.25 12l2.846-.813m4.717-3.09L9 5.25l.813 2.846m-1.626 0a4.5 4.5 0 003.09 3.09L15.75 12l-2.846.813m-3.09-3.09a4.5 4.5 0 003.09 3.09m-3.09-3.09l.813-2.846m2.277 5.936l-.813 2.846m-1.464-8.782zM18.259 11.023L18 11.75l-.259-.727a2.25 2.25 0 00-1.514-1.514L15.5 9.25l.727-.259a2.25 2.25 0 001.514-1.514L18 6.75l.259.727a2.25 2.25 0 001.514 1.514L20.5 9.25l-.727.259a2.25 2.25 0 00-1.514 1.514z" stroke="url(#aig)" stroke-width="0.4" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
          </div>
          <div class="ai-card-text">
            <div class="ai-card-title">AI 烹饪助手</div>
            <div class="ai-card-desc">想知道某道菜怎么做？问我吧</div>
          </div>
          <div class="ai-arrow">
            <svg viewBox="0 0 16 16" width="14" height="14">
              <path d="M6 4l4 4-4 4" stroke="rgba(255,255,255,0.5)" stroke-width="1.5" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
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
            <div class="recipe-author" @click.stop="goUser(item.authorId)">
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
            <div v-if="item.authorAvatar" class="feed-avatar" @click.stop="goUser(item.authorId)">
              <img :src="item.authorAvatar" class="feed-avatar-img" @error="onAvatarError" />
            </div>
            <div v-else class="feed-avatar feed-avatar-placeholder" @click.stop="goUser(item.authorId)">{{ (item.authorName || '用')[0] }}</div>
            <div class="feed-user">
              <span class="feed-nick">{{ item.authorName || '用户' + item.authorId }}</span>
              <span class="feed-time">{{ formatFeedTime(item.createTime) }}</span>
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
        <div v-for="item in recentProducts" :key="item.id" class="daily-card" @click="goProduct(item.id)">
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
        <div v-for="(item, idx) in filteredSalesProducts" :key="item.id" class="sales-card" @click="goProduct(item.id)">
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

    <transition name="fade">
      <div v-if="toast.show" class="toast-home" :class="toast.type">{{ toast.msg }}</div>
    </transition>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { userStore } from '../store/user'
import { getAllRecipes, getFollowRecipes } from '../api/recipe'
import { getRecentProducts, getSalesRanking, getSalesRankingByCategory } from '../api/shop'
import { getUnreadCount } from '../api/comment'
import AppTabbar from '../components/AppTabbar.vue'

const router = useRouter()
const TAB_KEY = 'home_active_tab'
const activeTab = ref(sessionStorage.getItem(TAB_KEY) || 'recommend')
const recipes = ref([])
const toast = reactive({ show: false, msg: '', type: 'success' })
const followRecipes = ref([])
const currentUser = computed(() => userStore.user)

const recentProducts = ref([])
const salesProducts = ref([])
const activeSalesCat = ref('全部')
const salesCategories = ['全部', '果蔬', '肉蛋', '海鲜', '速食']

const carouselRecipes = computed(() => recipes.value.slice(0, 3))
const featuredRecipes = computed(() => recipes.value.slice(3))

// 无缝循环轮播：首尾各克隆一张，边界处瞬间归位，避免回卷抖动
const currentSlide = ref(1)
const trackRef = ref(null)
let autoTimer = null

const slides = computed(() => {
  const list = carouselRecipes.value
  if (list.length <= 1) return list
  return [list[list.length - 1], ...list, list[0]]
})

const trackStyle = computed(() => ({
  transform: `translateX(-${currentSlide.value * 100}%)`
}))

// 当前真实第几张（用于圆点高亮/角标），忽略克隆位
const activeDotIndex = computed(() => {
  const n = carouselRecipes.value.length
  if (n <= 1) return 0
  if (currentSlide.value === n + 1) return 0
  if (currentSlide.value === 0) return n - 1
  return currentSlide.value - 1
})

// 到达首/尾克隆位后无动画跳回真实幻灯片。
// 直接改 DOM 同步生效——若只改响应式变量，Vue 会异步批量更新 DOM，
// 归位就会带着过渡动画往回滑过所有图片（边界抖动的根源）。
function snapToReal() {
  const el = trackRef.value
  const n = carouselRecipes.value.length
  if (!el || n <= 1) return
  const target = currentSlide.value === 0 ? n : 1
  el.style.transition = 'none'
  el.style.transform = `translateX(-${target * 100}%)`
  void el.offsetWidth
  currentSlide.value = target
  el.style.transition = ''
}

function onTransitionEnd(e) {
  if (e.target !== trackRef.value) return
  const n = carouselRecipes.value.length
  if (n <= 1) return
  if (currentSlide.value === n + 1 || currentSlide.value === 0) {
    snapToReal()
  }
}

function goToSlide(i) {
  const n = carouselRecipes.value.length
  if (n <= 1) return
  if (currentSlide.value === 0 || currentSlide.value === n + 1) {
    snapToReal()
  }
  currentSlide.value = i + 1
  resetAutoPlay()
}

function nextSlide() {
  const n = carouselRecipes.value.length
  if (n <= 1) return
  if (currentSlide.value >= n + 1) { snapToReal(); return }
  currentSlide.value++
}

function prevSlide() {
  const n = carouselRecipes.value.length
  if (n <= 1) return
  if (currentSlide.value <= 0) { snapToReal(); return }
  currentSlide.value--
}

function resetAutoPlay() {
  clearInterval(autoTimer)
  autoTimer = setInterval(nextSlide, 4000)
}

let touchStartX = 0
function onTouchStart(e) {
  touchStartX = e.touches[0].clientX
  clearInterval(autoTimer)
}
function onTouchEnd(e) {
  const dx = e.changedTouches[0].clientX - touchStartX
  if (dx < -40) nextSlide()
  else if (dx > 40) prevSlide()
  resetAutoPlay()
}

// 数据量变化时修正起始位：单张/空时停在 0，否则停在真实第一张
watch(() => carouselRecipes.value.length, (n) => {
  if (n <= 1) currentSlide.value = 0
  else if (currentSlide.value <= 0) currentSlide.value = 1
})

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
  if (activeTab.value === 'shop') fetchShopData()
  resetAutoPlay()
  if (currentUser.value) startNotifyPolling()
})

onUnmounted(() => {
  clearInterval(autoTimer)
  if (notifyTimer) clearInterval(notifyTimer)
})

let notifyTimer = null
let lastUnread = 0

function startNotifyPolling() {
  getUnreadCount(currentUser.value.userId).then(res => {
    lastUnread = res.data || 0
  }).catch(() => {})
  notifyTimer = setInterval(async () => {
    try {
      const res = await getUnreadCount(currentUser.value.userId)
      const count = res.data || 0
      if (count > lastUnread) {
        showToast('有人赞了你的评论')
      }
      lastUnread = count
    } catch { /* ignore */ }
  }, 15000)
}

function showToast(msg, type = 'success') {
  toast.msg = msg
  toast.type = type
  toast.show = true
  setTimeout(() => { toast.show = false }, 3000)
}

watch(activeTab, async (tab) => {
  sessionStorage.setItem(TAB_KEY, tab)
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

function goProduct(id) {
  router.push('/product/' + id)
}

function goUser(userId) {
  if (userId) router.push('/user/' + userId)
}

function formatFeedTime(val) {
  if (!val) return ''
  let d
  if (Array.isArray(val)) {
    d = new Date(val[0], val[1] - 1, val[2], val[3] || 0, val[4] || 0, val[5] || 0)
  } else {
    d = new Date(val)
  }
  if (isNaN(d.getTime())) return ''
  const now = new Date()
  const diff = now - d
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
  return (d.getMonth() + 1) + '-' + d.getDate()
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
  padding: calc(14px + env(safe-area-inset-top, 0px)) var(--container-padding) 70px;
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

/* AI card — animated warm glass */
.ai-card {
  position: relative;
  margin-top: 18px;
  background: linear-gradient(120deg, rgba(255,249,243,0.94), rgba(255,238,224,0.94), rgba(255,249,243,0.94), rgba(255,242,230,0.94));
  background-size: 300% 300%;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 20px;
  cursor: pointer;
  border: 1px solid rgba(255,140,90,0.28);
  box-shadow: 0 4px 24px rgba(255,122,51,0.12), 0 1px 4px rgba(0,0,0,0.04);
  transition: all 0.35s;
  overflow: hidden;
  animation: aiCardBgFlow 8s ease-in-out infinite, aiCardBreath 3.5s ease-in-out infinite;
}

/* 围绕按钮流动的光边 */
@property --flow-angle {
  syntax: '<angle>';
  initial-value: 0deg;
  inherits: false;
}

.ai-card::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  padding: 1.5px;
  background: rgba(255,150,90,0.22); /* 不支持 @property 时的静态边框兜底 */
  background: conic-gradient(
    from var(--flow-angle),
    transparent 0deg,
    rgba(255,150,90,0.95) 45deg,
    rgba(255,215,175,0.75) 90deg,
    transparent 150deg,
    transparent 210deg,
    rgba(255,160,100,0.85) 300deg,
    transparent 360deg
  );
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  mask-composite: exclude;
  animation: aiBorderFlow 3.5s linear infinite;
  pointer-events: none;
  z-index: 2;
}

@keyframes aiBorderFlow {
  from { --flow-angle: 0deg; }
  to   { --flow-angle: 360deg; }
}

/* 卡片扫光 */
.ai-card::after {
  content: '';
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  width: 30%;
  background: linear-gradient(105deg, transparent, rgba(255,255,255,0.22) 50%, transparent);
  transform: translateX(-160%) skewX(-18deg);
  animation: aiShine 4.5s ease-in-out infinite;
  pointer-events: none;
  border-radius: inherit;
}

.ai-card:active { transform: scale(0.98); }

@keyframes aiCardBgFlow {
  0%, 100% { background-position: 0% 50%; }
  50%      { background-position: 100% 50%; }
}

@keyframes aiCardBreath {
  0%, 100% { box-shadow: 0 4px 24px rgba(255,122,51,0.12), 0 1px 4px rgba(0,0,0,0.04); }
  50%      { box-shadow: 0 8px 34px rgba(255,122,51,0.30), 0 2px 12px rgba(0,0,0,0.07); }
}

@keyframes aiShine {
  0%, 55% { transform: translateX(-160%) skewX(-18deg); }
  100%    { transform: translateX(600%) skewX(-18deg); }
}

.ai-card-body {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 16px;
}

/* 图标容器：整体呼吸缩放 */
.ai-icon-wrap {
  position: relative;
  flex-shrink: 0;
  width: 46px; height: 46px;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: aiWrapBreath 3.5s ease-in-out infinite;
}

.ai-icon {
  width: 44px; height: 44px;
  border-radius: 16px;
  background: linear-gradient(135deg, rgba(255,122,51,0.30), rgba(255,160,100,0.22));
  border: 1px solid rgba(255,122,51,0.38);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  animation: aiIconGlow 2.6s ease-in-out infinite;
}

.ai-icon svg {
  animation: aiSparkleTwinkle 3s ease-in-out infinite;
}

@keyframes aiWrapBreath {
  0%, 100% { transform: scale(1); }
  50%      { transform: scale(1.07); }
}

@keyframes aiIconGlow {
  0%, 100% { box-shadow: 0 0 8px rgba(255,140,80,0.25); }
  50%      { box-shadow: 0 0 20px rgba(255,140,80,0.55); }
}

@keyframes aiSparkleTwinkle {
  0%, 100% { transform: scale(1); opacity: 1; }
  50%      { transform: scale(1.18); opacity: 0.8; }
}

.ai-card-text { flex: 1; }

.ai-card-title {
  font-size: 15px;
  font-weight: 700;
  margin-bottom: 4px;
  letter-spacing: 0.5px;
  background: linear-gradient(90deg, #4A2010, #C06535, #4A2010);
  background-size: 200% 100%;
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  color: transparent;
  animation: aiTitleShimmer 4.5s ease-in-out infinite;
}

@keyframes aiTitleShimmer {
  0%, 100% { background-position: 0% 50%; }
  50%      { background-position: 100% 50%; }
}

.ai-card-desc {
  font-size: 12px;
  color: #8B6040;
  font-weight: 500;
}

.ai-arrow {
  flex-shrink: 0;
  opacity: 0.6;
  transition: all 0.3s;
}

.ai-card:active .ai-arrow {
  opacity: 1;
  transform: translateX(2px);
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

/* ============ carousel ============ */
.carousel {
  position: relative;
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: 0 12px 48px rgba(30, 21, 15, 0.10), 0 3px 12px rgba(30, 21, 15, 0.05);
}

.carousel-track {
  display: flex;
  transition: transform 0.6s cubic-bezier(0.22, 0.61, 0.36, 1);
}

.carousel-slide {
  flex: 0 0 100%;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}

/* image */
.slide-img-wrap {
  position: relative;
  overflow: hidden;
  height: 290px;
}

.slide-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  background: linear-gradient(135deg, #E8E0D5, #DDD4C5);
}

/* light sweep */
.slide-shine {
  position: absolute;
  inset: 0;
  z-index: 1;
  pointer-events: none;
  background: linear-gradient(110deg,
    transparent 35%,
    rgba(255,255,255,0.00) 40%,
    rgba(255,255,255,0.08) 44%,
    rgba(255,255,255,0.04) 48%,
    transparent 53%);
  animation: shineSweep 5s ease-in-out forwards;
}

@keyframes shineSweep {
  0%   { opacity: 0; }
  20%  { opacity: 1; }
  80%  { opacity: 1; }
  100% { opacity: 0; }
}

/* overlay */
.slide-overlay {
  position: absolute;
  inset: 0;
  z-index: 2;
  background: linear-gradient(180deg,
    rgba(18, 30, 31, 0.02) 0%,
    rgba(18, 30, 31, 0.00) 38%,
    rgba(18, 30, 31, 0.32) 70%,
    rgba(18, 30, 31, 0.78) 100%);
}

/* content — spring-like pop */
.slide-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 3;
  padding: 30px 22px 22px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.slide-tag {
  display: inline-block;
  font-size: 10px;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(135deg, #f09055, #e8783d);
  padding: 5px 14px;
  border-radius: 20px;
  letter-spacing: 2px;
  align-self: flex-start;
  box-shadow: 0 3px 18px rgba(225, 115, 45, 0.40);
  animation: popIn 0.6s 0.06s cubic-bezier(0.17, 0.84, 0.44, 1) both;
}

.slide-title {
  font-family: var(--font-heading);
  font-size: 22px;
  font-weight: 800;
  color: #fff;
  line-height: 1.25;
  letter-spacing: 0.4px;
  text-shadow: 0 2px 16px rgba(0,0,0,0.22);
  animation: popIn 0.6s 0.12s cubic-bezier(0.17, 0.84, 0.44, 1) both;
}

.slide-meta {
  font-family: var(--font-body);
  font-size: 13px;
  color: rgba(255, 255, 255, 0.80);
  font-weight: 400;
  animation: popIn 0.6s 0.18s cubic-bezier(0.17, 0.84, 0.44, 1) both;
}

@keyframes popIn {
  0%   { opacity: 0; transform: translateY(16px) scale(0.94); }
  65%  { opacity: 1; transform: translateY(-2px) scale(1.01); }
  100% { opacity: 1; transform: translateY(0) scale(1); }
}

/* dots */
.carousel-dots {
  position: absolute;
  bottom: 18px;
  right: 18px;
  display: flex;
  gap: 8px;
  z-index: 4;
  padding: 6px 10px;
  background: rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-radius: 12px;
}

.carousel-dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.45);
  cursor: pointer;
  transition: all 0.45s cubic-bezier(0.17, 0.84, 0.44, 1);
}

.carousel-dot.active {
  width: 24px;
  border-radius: 3px;
  background: #fff;
  box-shadow: 0 0 10px rgba(255,255,255,0.45);
  animation: dotPulse 4s ease-in-out infinite;
}

@keyframes dotPulse {
  0%, 100% { box-shadow: 0 0 8px rgba(255,255,255,0.35); }
  50%      { box-shadow: 0 0 16px rgba(255,255,255,0.65); }
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

.toast-home {
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

.toast-home.success { background: rgba(18, 30, 31, 0.88); }
.toast-home.error { background: rgba(180, 60, 20, 0.9); }

.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
