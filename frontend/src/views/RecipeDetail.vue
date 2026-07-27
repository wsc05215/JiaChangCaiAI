<template>
  <div class="detail-page" v-if="recipe">
    <!-- 顶部导航 -->
    <div class="top-bar">
      <div class="top-back" @click="$router.back()">
        <svg viewBox="0 0 24 24" width="22" height="22">
          <path d="M15 18l-6-6 6-6" stroke="#333" stroke-width="2" fill="none" stroke-linecap="round"/>
        </svg>
      </div>
    </div>

    <!-- 封面图 -->
    <img class="cover-img" :src="getCover(recipe.coverImages)" @error="onImgError" />

    <!-- 标题评分区 -->
    <div class="title-row">
      <div class="detail-title">{{ recipe.title }}</div>
      <div class="rating-badge">{{ recipe.rating || '0.0' }}</div>
    </div>
    <div class="rating-label">佳尝菜评分</div>

    <!-- 作者信息 -->
    <div class="author-row">
      <div class="author-avatar">{{ authorName.charAt(0) }}</div>
      <div class="author-info">
        <div class="author-name">{{ authorName }}</div>
      </div>
      <button class="follow-btn" @click="handleFollow">{{ isFollowed ? '已关注' : '关注' }}</button>
    </div>

    <!-- 描述 -->
    <p class="detail-desc">{{ recipe.description }}</p>

    <!-- 用时/难度/热量 -->
    <div class="info-bar">
      <div class="info-item">
        <div class="info-label">用时</div>
        <div class="info-val">{{ recipe.cookTime || '--' }}</div>
      </div>
      <div class="info-divider"></div>
      <div class="info-item">
        <div class="info-label">难度</div>
        <div class="info-val">{{ recipe.difficulty || '--' }}</div>
      </div>
      <div class="info-divider"></div>
      <div class="info-item">
        <div class="info-label">热量</div>
        <div class="info-val">{{ recipe.calories || '--' }}</div>
      </div>
    </div>

    <!-- 用料清单 -->
    <div class="section-title">用料清单</div>
    <div class="ingredient-card" v-for="(item, i) in ingredients" :key="i">
      <span>{{ item.name }}</span>
      <span class="ing-amount">{{ item.amount }}</span>
    </div>

    <!-- 烹饪步骤 -->
    <div class="section-title">烹饪步骤</div>
    <div class="step-item" v-for="(step, i) in steps" :key="i">
      <div class="step-num">{{ i + 1 }}</div>
      <div class="step-desc">{{ step.desc }}</div>
    </div>

    <!-- 底部操作栏 -->
    <div class="bottom-bar">
      <div class="action-item" @click="handleComment">
        <svg viewBox="0 0 24 24" width="22" height="22">
          <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z" stroke="#333" stroke-width="1.5" fill="none"/>
        </svg>
        <span>{{ recipe.commentCount || 0 }}</span>
      </div>
      <div class="action-item" :class="{ liked: isLiked }" @click="toggleLike">
        <svg viewBox="0 0 24 24" width="22" height="22">
          <path d="M20.84 4.61a5.5 5.5 0 00-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 00-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 000-7.78z" :stroke="isLiked ? '#FF7A3D' : '#333'" :fill="isLiked ? '#FF7A3D' : 'none'" stroke-width="1.5"/>
        </svg>
        <span>{{ recipe.likeCount || 0 }}</span>
      </div>
      <input class="comment-input" type="text" placeholder="说点什么......" v-model="commentText" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { userStore } from '../store/user'

const route = useRoute()
const router = useRouter()
const recipe = ref(null)
const ingredients = ref([])
const steps = ref([])
const isLiked = ref(false)
const isFollowed = ref(false)
const commentText = ref('')

const authorName = computed(() => '用户' + (recipe.value?.authorId || ''))

onMounted(async () => {
  const id = route.params.id
  try {
    const res = await fetch('/recipe/getAllRecipe')
    const data = await res.json()
    const found = data.find(r => r.recipeId == id)
    if (found) {
      recipe.value = found
      ingredients.value = parseJson(found.ingredients)
      steps.value = parseJson(found.steps)
    }
  } catch {
    router.back()
  }
})

function parseJson(val) {
  if (!val) return []
  if (typeof val === 'string') {
    try { return JSON.parse(val) } catch { return [] }
  }
  return val
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

function toggleLike() {
  isLiked.value = !isLiked.value
  if (isLiked.value) recipe.value.likeCount++
  else recipe.value.likeCount--
}

function handleFollow() {
  isFollowed.value = !isFollowed.value
}

function handleComment() {
  if (commentText.value.trim()) {
    recipe.value.commentCount++
    commentText.value = ''
  }
}
</script>

<style scoped>
.detail-page {
  background: var(--bg);
  min-height: 100vh;
  padding-bottom: 60px;
}

.top-bar {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  padding: 10px 15px;
  display: flex;
  z-index: 10;
}

.top-back {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: rgba(255,255,255,0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.cover-img {
  width: 100%;
  height: 280px;
  object-fit: cover;
  background: #f0e4d6;
}

.title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 16px 15px 0;
}

.detail-title {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  flex: 1;
}

.rating-badge {
  font-size: 20px;
  font-weight: 700;
  color: #3c2012c8;
  flex-shrink: 0;
  margin-left: 12px;
}

.rating-label {
  font-size: 12px;
  color: var(--gray);
  padding: 2px 15px 0;
}

.author-row {
  display: flex;
  align-items: center;
  padding: 14px 15px;
  gap: 8px;
}

.author-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--orange);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}

.author-info {
  flex: 1;
}

.author-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.follow-btn {
  padding: 6px 16px;
  border-radius: 14px;
  background: var(--orange);
  color: #fff;
  font-size: 13px;
  border: none;
  cursor: pointer;
}

.follow-btn:active {
  opacity: 0.8;
}

.detail-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  padding: 0 15px;
  margin-top: 4px;
}

.info-bar {
  display: flex;
  align-items: center;
  margin: 16px 15px;
  background: var(--white);
  border-radius: 12px;
  padding: 12px 0;
}

.info-item {
  flex: 1;
  text-align: center;
}

.info-label {
  font-size: 12px;
  color: var(--gray);
}

.info-val {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-top: 2px;
}

.info-divider {
  width: 1px;
  height: 30px;
  background: #e8ddd2;
}

.section-title {
  font-size: 16px;
  font-weight: 700;
  color: #333;
  padding: 16px 15px 8px;
}

.ingredient-card {
  display: flex;
  justify-content: space-between;
  margin: 0 15px;
  padding: 10px 14px;
  background: var(--white);
  border-radius: 10px;
  margin-bottom: 6px;
  font-size: 14px;
  color: #333;
}

.ing-amount {
  color: var(--gray);
}

.step-item {
  display: flex;
  gap: 12px;
  margin: 0 15px 14px;
  padding: 12px 14px;
  background: var(--white);
  border-radius: 10px;
}

.step-num {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: var(--orange);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.step-desc {
  font-size: 14px;
  color: #444;
  line-height: 1.6;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 375px;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 10px 15px;
  background: #fff;
  border-top: 1px solid #eee;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1px;
  font-size: 11px;
  color: #333;
  cursor: pointer;
}

.action-item.liked {
  color: var(--orange);
}

.comment-input {
  flex: 1;
  height: 34px;
  border-radius: 17px;
  background: #f5ece3;
  padding: 0 14px;
  font-size: 13px;
}
</style>
