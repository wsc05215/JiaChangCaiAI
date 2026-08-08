<template>
  <div class="detail-page" v-if="recipe">
    <div class="top-bar">
      <div class="top-back" @click="$goBack()">
        <svg viewBox="0 0 24 24" width="24" height="24">
          <path d="M15 18l-6-6 6-6" stroke="#fff" stroke-width="2.5" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <div v-if="isOwnRecipe" class="top-delete" @click="handleDeleteRecipe">
        <svg viewBox="0 0 24 24" width="20" height="20">
          <path d="M3 6h18M8 6V4a2 2 0 012-2h4a2 2 0 012 2v2M19 6l-1 14a2 2 0 01-2 2H8a2 2 0 01-2-2L5 6" stroke="#fff" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
    </div>

    <div class="cover-wrap"
         @touchstart="onTouchStart"
         @touchend="onTouchEnd"
         @mousedown="onTouchStart"
         @mouseup="onTouchEnd">
      <div class="carousel-track" :style="{ transform: 'translateX(-' + currentSlide * 100 + '%)' }">
        <div class="carousel-slide" v-for="(img, i) in allImages" :key="'img'+i">
          <img :src="img" class="cover-img" @error="onImgError" />
        </div>
        <div class="carousel-slide" v-if="recipe.video">
          <video ref="coverVideo"
                 :src="recipe.video"
                 class="cover-img"
                 muted
                 playsinline
                 loop
                 autoplay
                 @error="onImgError"></video>
          <div class="cover-gradient"></div>
        </div>
      </div>
      <div class="cover-gradient"></div>
      <div class="carousel-dots" v-if="allSlides > 1">
        <span v-for="i in allSlides" :key="i"
              class="carousel-dot" :class="{ active: i - 1 === currentSlide }"
              @click="currentSlide = i - 1"></span>
      </div>
    </div>

    <div class="content-area">
      <div class="title-row">
        <div class="detail-title">{{ recipe.title }}</div>
        <div class="rating-badge">
          <svg viewBox="0 0 24 24" width="16" height="16" fill="#FF5E2C"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
          {{ recipe.rating || '0.0' }}
        </div>
      </div>

      <div class="author-row">
        <div class="author-avatar" @click="goUser(recipe.authorId)">
          <img v-if="recipe.authorAvatar && !authorAvatarFailed" :src="recipe.authorAvatar" class="author-avatar-img" @error="authorAvatarFailed = true" />
          <span v-else>{{ authorName.charAt(0) }}</span>
        </div>
        <div class="author-info" @click="goUser(recipe.authorId)">
          <div class="author-name">{{ authorName }}</div>
          <div class="author-label">食谱创作者</div>
        </div>
        <button v-if="followChecked && !isOwnRecipe" class="follow-btn" :class="{ followed: isFollowed }" @click="handleFollow">
          {{ isFollowed ? '已关注' : '+ 关注' }}
        </button>
      </div>

      <p class="detail-desc">{{ recipe.description }}</p>

      <div class="info-bar">
        <div class="info-item">
          <div class="info-icon">&#x23F1;</div>
          <div class="info-val">{{ recipe.cookTime || '--' }}</div>
          <div class="info-label">用时</div>
        </div>
        <div class="info-item">
          <div class="info-icon">&#x1F4AA;</div>
          <div class="info-val">{{ recipe.difficulty || '--' }}</div>
          <div class="info-label">难度</div>
        </div>
        <div class="info-item">
          <div class="info-icon">&#x1F525;</div>
          <div class="info-val">{{ recipe.calories || '--' }}</div>
          <div class="info-label">热量</div>
        </div>
      </div>

      <div class="section-title">
        <span class="section-dot"></span>
        用料清单
      </div>
      <div class="ingredient-list">
        <div class="ingredient-card" v-for="(item, i) in ingredients" :key="i">
          <span class="ing-dot"></span>
          <span class="ing-name">{{ item.name }}</span>
          <span class="ing-amount">{{ item.amount }}</span>
        </div>
      </div>

      <div class="section-title">
        <span class="section-dot"></span>
        烹饪步骤
      </div>
      <div class="steps-list">
        <div class="step-item" v-for="(step, i) in steps" :key="i">
          <div class="step-num">{{ i + 1 }}</div>
          <div class="step-content">
            <div class="step-desc">{{ step.desc }}</div>
          </div>
        </div>
      </div>

      <div class="section-title">
        <span class="section-dot"></span>
        视频教程
      </div>
      <div class="video-section">
        <div v-if="recipe.video" class="video-player-wrap">
          <video :src="recipe.video" controls preload="metadata"
                 class="video-player"
                 playsinline></video>
        </div>
        <div v-else class="video-empty">
          <p>该用户未发布教程视频哦~</p>
        </div>
      </div>

      <div class="section-title">
        <span class="section-dot"></span>
        评论 ({{ comments.length }})
      </div>
      <div class="comment-list" v-if="topLevelComments.length">
        <div class="comment-card" v-for="c in topLevelComments" :key="c.commentId">
          <div class="comment-avatar">{{ getCommentAuthor(c.userId).charAt(0) }}</div>
          <div class="comment-body">
            <div class="comment-header">
              <span class="comment-name">{{ getCommentAuthor(c.userId) }}</span>
              <span class="comment-time">{{ formatTime(c.createTime) }}</span>
            </div>
            <div class="comment-text">{{ c.content }}</div>
            <div class="comment-actions">
              <span class="comment-like-btn" :class="{ liked: likedComments[c.commentId] }" @click.stop="toggleCommentLike(c)">
                <svg viewBox="0 0 24 24" width="14" height="14">
                  <path d="M14 9V5a3 3 0 00-3-3l-4 9v11h11.28a2 2 0 002-2l1.08-6.5a2 2 0 00-2-2.17H16a1 1 0 01-1-1.5L17 7.5A2 2 0 0015.28 5H14z" :fill="likedComments[c.commentId] ? '#FF5E2C' : 'none'" :stroke="likedComments[c.commentId] ? '#FF5E2C' : '#C4B5AA'" stroke-width="1.5"/>
                </svg>
                <span v-if="c.likeCount > 0">{{ c.likeCount }}</span>
              </span>
              <span class="comment-reply-btn" @click="setReply(c)">回复</span>
            </div>
            <div class="reply-item" v-for="r in getReplies(c.commentId)" :key="r.commentId">
              <div class="comment-avatar small">{{ getCommentAuthor(r.userId).charAt(0) }}</div>
              <div class="comment-body">
                <div class="comment-header">
                  <span class="comment-name">{{ getCommentAuthor(r.userId) }}</span>
                  <span class="comment-time">{{ formatTime(r.createTime) }}</span>
                </div>
                <div class="comment-text">{{ r.content }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="comment-empty" v-else>
        <p>暂无评论，快来抢沙发吧~</p>
      </div>
    </div>

    <div class="bottom-bar">
      <div class="action-item" @click="handleComment">
        <svg viewBox="0 0 24 24" width="22" height="22">
          <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z" stroke="currentColor" stroke-width="1.8" fill="none" stroke-linecap="round"/>
        </svg>
        <span>{{ recipe.commentCount || 0 }}</span>
      </div>
      <div class="action-item" :class="{ liked: isFavorited }" @click="toggleFavorite">
        <svg viewBox="0 0 24 24" width="22" height="22">
          <path d="M20.84 4.61a5.5 5.5 0 00-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 00-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 000-7.78z" :stroke="isFavorited ? '#FF5E2C' : '#6B5E52'" :fill="isFavorited ? '#FF5E2C' : 'none'" stroke-width="1.8"/>
        </svg>
        <span>{{ recipe.favoriteCount || 0 }}</span>
      </div>
      <div class="comment-input-wrap">
        <span class="reply-hint" v-if="replyTo">
          回复 @{{ getCommentAuthor(replyTo.userId) }}
          <svg @click="cancelReply" viewBox="0 0 24 24" width="14" height="14" class="reply-cancel">
            <path d="M18 6L6 18M6 6l12 12" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round"/>
          </svg>
        </span>
        <input class="comment-input" type="text" :placeholder="replyTo ? '回复 ' + getCommentAuthor(replyTo.userId) + '...' : '说点什么......'" v-model="commentText" />
      </div>
      <button class="send-comment-btn" @click="handleComment">发送</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { userStore } from '../store/user'
import { checkFavorited, addFavorite, removeFavorite } from '../api/favorite'
import { follow, unfollow, checkFollowing } from '../api/follow'
import { getComments, addComment, toggleCommentLike as toggleLikeApi, checkCommentLiked } from '../api/comment'
import { getUserDetail } from '../api/auth'
import { deleteRecipe } from '../api/recipe'

const route = useRoute()
const router = useRouter()
const recipe = ref(null)
const ingredients = ref([])
const steps = ref([])
const isFavorited = ref(false)
const isFollowed = ref(false)
const followChecked = ref(false)
const authorAvatarFailed = ref(false)
const commentText = ref('')
const comments = ref([])
const replyTo = ref(null)
const commentAuthors = ref({})
const likedComments = ref({})

const currentUser = computed(() => userStore.user)
const isOwnRecipe = computed(() => currentUser.value?.userId === recipe.value?.authorId)
const authorName = computed(() => recipe.value?.authorName || ('用户' + (recipe.value?.authorId || '')))

// 轮播
const currentSlide = ref(0)
const touchStartX = ref(0)
const coverVideo = ref(null)

const allImages = computed(() => parseJson(recipe.value?.coverImages))
const allSlides = computed(() => {
  const imgs = allImages.value.length
  return recipe.value?.video ? imgs + 1 : imgs
})

function onTouchStart(e) {
  touchStartX.value = e.touches ? e.touches[0].clientX : e.clientX
}

function onTouchEnd(e) {
  const endX = e.changedTouches ? e.changedTouches[0].clientX : e.clientX
  const diff = touchStartX.value - endX
  if (Math.abs(diff) > 50) {
    if (diff > 0 && currentSlide.value < allSlides.value - 1) {
      currentSlide.value++
    } else if (diff < 0 && currentSlide.value > 0) {
      currentSlide.value--
    }
  }
}

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
      if (currentUser.value) {
        try {
          const favRes = await checkFavorited(currentUser.value.userId, found.recipeId)
          isFavorited.value = favRes.data === true
        } catch { /* ignore */ }
        if (found.authorId && currentUser.value.userId !== found.authorId) {
          try {
            const followRes = await checkFollowing(currentUser.value.userId, found.authorId)
            isFollowed.value = followRes.data === true
          } catch { /* ignore */ }
          finally { followChecked.value = true }
        } else {
          followChecked.value = true
        }
      loadComments()
      }
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

function onImgError(e) { e.target.style.display = 'none' }

async function loadComments() {
  if (!recipe.value) return
  try {
    const res = await getComments(recipe.value.recipeId)
    comments.value = res.data || []
    recipe.value.commentCount = comments.value.length
    comments.value.forEach(c => fetchAuthorName(c.userId))
    if (currentUser.value) {
      const uid = currentUser.value.userId
      const results = await Promise.all(
        comments.value.map(c =>
          checkCommentLiked(c.commentId, uid).then(r => ({ id: c.commentId, liked: r.data })).catch(() => ({ id: c.commentId, liked: false }))
        )
      )
      results.forEach(r => { likedComments.value[r.id] = r.liked })
    }
  } catch { /* ignore */ }
}

async function toggleCommentLike(comment) {
  if (!currentUser.value) return
  const uid = currentUser.value.userId
  try {
    const res = await toggleLikeApi(comment.commentId, uid)
    if (res.data) {
      likedComments.value[comment.commentId] = res.data.liked
      if (res.data.liked) {
        comment.likeCount = (comment.likeCount || 0) + 1
      } else {
        comment.likeCount = Math.max((comment.likeCount || 0) - 1, 0)
      }
    }
  } catch { /* ignore */ }
}

async function fetchAuthorName(userId) {
  if (!userId || commentAuthors.value[userId]) return
  try {
    const res = await getUserDetail(userId)
    commentAuthors.value[userId] = res.data?.nickName || res.data?.username || ('用户' + userId)
  } catch {
    commentAuthors.value[userId] = '用户' + userId
  }
}

function getCommentAuthor(userId) {
  return commentAuthors.value[userId] || '用户' + userId
}

function formatTime(val) {
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

const topLevelComments = computed(() =>
  comments.value.filter(c => !c.parentId)
)

function getReplies(parentId) {
  return comments.value.filter(c => c.parentId === parentId)
}

function setReply(comment) {
  replyTo.value = comment
  commentText.value = ''
}

function cancelReply() {
  replyTo.value = null
  commentText.value = ''
}

async function toggleFavorite() {
  if (!currentUser.value || !recipe.value) return
  const userId = currentUser.value.userId
  const recipeId = recipe.value.recipeId
  try {
    if (isFavorited.value) {
      await removeFavorite(userId, recipeId)
      isFavorited.value = false
      if (recipe.value.favoriteCount != null) recipe.value.favoriteCount--
    } else {
      await addFavorite(userId, recipeId)
      isFavorited.value = true
      if (recipe.value.favoriteCount != null) recipe.value.favoriteCount++
    }
  } catch { /* ignore */ }
}

async function handleDeleteRecipe() {
  if (!currentUser.value || !recipe.value) return
  if (!confirm('确定要删除这个菜谱吗？')) return
  try {
    const res = await deleteRecipe(recipe.value.recipeId, currentUser.value.userId)
    if (res.data === 'ok') {
      router.back()
    }
  } catch { /* ignore */ }
}

async function handleFollow() {
  if (!currentUser.value || !recipe.value) return
  const myId = currentUser.value.userId
  const authorId = recipe.value.authorId
  try {
    if (isFollowed.value) {
      const res = await unfollow(myId, authorId)
      if (res.data === true) isFollowed.value = false
    } else {
      const res = await follow(myId, authorId)
      if (res.data === true) isFollowed.value = true
    }
  } catch (e) { console.error('关注操作失败:', e) }
}

function goUser(userId) {
  if (userId) router.push('/user/' + userId)
}

async function handleComment() {
  if (!commentText.value.trim() || !currentUser.value || !recipe.value) return
  try {
    await addComment(recipe.value.recipeId, currentUser.value.userId, commentText.value.trim(), replyTo.value?.commentId || null)
    commentText.value = ''
    replyTo.value = null
    await loadComments()
  } catch (e) {
    console.error('comment failed:', e)
  }
}
</script>

<style scoped>
.detail-page {
  background: var(--bg);
  min-height: 100vh;
  padding-bottom: 80px;
}

.top-bar {
  position: absolute;
  top: 12px;
  left: 12px;
  right: 12px;
  z-index: 10;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.top-delete {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(0,0,0,0.3);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.top-delete:active { background: rgba(220,60,40,0.5); }

.top-back {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(0,0,0,0.3);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.cover-wrap {
  position: relative;
  overflow: hidden;
  height: 320px;
}

.carousel-track {
  display: flex;
  height: 100%;
  transition: transform 0.35s ease;
}

.carousel-slide {
  min-width: 100%;
  height: 100%;
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  background: linear-gradient(135deg, #F5F0E8, #EDE4D5);
  display: block;
}

.cover-gradient {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 120px;
  background: linear-gradient(transparent, var(--bg));
  pointer-events: none;
}

.carousel-dots {
  position: absolute;
  bottom: 12px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 6px;
  z-index: 5;
}

.carousel-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: rgba(255,255,255,0.5);
  cursor: pointer;
  transition: all 0.3s;
}

.carousel-dot.active {
  width: 18px;
  border-radius: 3px;
  background: #fff;
}

.content-area {
  margin-top: -40px;
  position: relative;
  z-index: 2;
}

.title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 0 16px;
}

.detail-title {
  font-size: 24px;
  font-weight: 900;
  color: var(--text-primary);
  flex: 1;
  line-height: 1.3;
}

.rating-badge {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 20px;
  font-weight: 800;
  color: var(--primary);
  flex-shrink: 0;
  margin-left: 12px;
  background: var(--primary-bg-light);
  padding: 6px 12px;
  border-radius: var(--radius-md);
}

.author-row {
  display: flex;
  align-items: center;
  padding: 16px 16px;
  gap: 12px;
}

.author-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--gradient-primary);
  color: #fff;
  font-size: 17px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 3px 10px rgba(255, 122, 51,0.25);
  cursor: pointer;
  overflow: hidden;
  flex-shrink: 0;
}

.author-avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.author-info { flex: 1; cursor: pointer; }
.author-name { font-size: 15px; font-weight: 700; color: var(--text-primary); }
.author-label { font-size: 11px; color: var(--text-muted); margin-top: 1px; }

.follow-btn {
  padding: 8px 20px;
  border-radius: var(--radius-full);
  background: var(--gradient-primary);
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  box-shadow: var(--shadow-primary);
  transition: all 0.25s;
}

.follow-btn.followed {
  background: var(--divider);
  color: var(--text-muted);
  box-shadow: none;
}

.follow-btn:active { transform: scale(0.94); }

.detail-desc {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.7;
  padding: 0 16px;
  margin-top: 4px;
}

.info-bar {
  display: flex;
  margin: 20px 16px;
  background: #fff;
  border-radius: var(--radius-xl);
  padding: 18px 0;
  box-shadow: var(--shadow-sm);
  border: 1px solid rgba(0,0,0,0.03);
}

.info-item {
  flex: 1;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.info-icon { font-size: 20px; }

.info-val {
  font-size: 16px;
  font-weight: 800;
  color: var(--text-primary);
}

.info-label {
  font-size: 11px;
  color: var(--text-muted);
  font-weight: 500;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 800;
  color: var(--text-primary);
  padding: 14px 16px 12px;
  letter-spacing: 0.5px;
}

.section-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--primary);
}

.ingredient-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 0 16px;
}

.ingredient-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 13px 16px;
  background: #fff;
  border-radius: var(--radius-md);
  font-size: 14px;
  box-shadow: var(--shadow-xs);
  border: 1px solid rgba(0,0,0,0.02);
}

.ing-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--primary-light);
  flex-shrink: 0;
}

.ing-name {
  flex: 1;
  color: var(--text-primary);
  font-weight: 500;
}

.ing-amount {
  color: var(--primary);
  font-weight: 700;
  font-size: 13px;
}

.steps-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 0 16px;
}

.step-item {
  display: flex;
  gap: 14px;
}

.step-num {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: var(--gradient-primary);
  color: #fff;
  font-size: 14px;
  font-weight: 800;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 3px 10px rgba(255, 122, 51,0.2);
}

.step-content {
  flex: 1;
  background: #fff;
  border-radius: var(--radius-md);
  padding: 14px 16px;
  box-shadow: var(--shadow-xs);
  border: 1px solid rgba(0,0,0,0.02);
}

.step-desc {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.7;
}

.video-section {
  padding: 0 16px;
}

.video-player-wrap {
  border-radius: var(--radius-md);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

.video-player {
  width: 100%;
  max-height: 400px;
  border-radius: var(--radius-md);
  background: #000;
  display: block;
}

.video-empty {
  text-align: center;
  padding: 30px 16px;
  color: var(--text-muted);
  font-size: 14px;
  background: #fff;
  border-radius: var(--radius-md);
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  padding-bottom: max(16px, env(safe-area-inset-bottom));
  background: rgba(255,255,255,0.95);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border-top: 1px solid rgba(0,0,0,0.06);
  z-index: 100;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  font-size: 10px;
  color: var(--text-secondary);
  cursor: pointer;
  font-weight: 600;
  transition: color 0.2s;
  flex-shrink: 0;
  min-width: 36px;
}

.action-item.liked { color: var(--primary); }

.comment-input {
  flex: 1;
  height: 46px;
  min-width: 0;
  border-radius: 23px;
  background: #F5F0E8;
  border: 1.5px solid var(--border);
  padding: 0 18px;
  font-size: 15px;
  outline: none;
  transition: all 0.2s;
}

.comment-input:focus {
  background: #fff;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(230,126,34,0.08);
}

.send-comment-btn {
  height: 46px;
  padding: 0 20px;
  border-radius: 23px;
  background: var(--gradient-primary);
  color: #fff;
  font-size: 15px;
  font-weight: 700;
  white-space: nowrap;
  flex-shrink: 0;
  box-shadow: var(--shadow-primary);
  transition: all 0.2s;
}

.send-comment-btn:active { transform: scale(0.94); }

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 0 16px;
}

.comment-card {
  display: flex;
  gap: 10px;
  background: #fff;
  border-radius: var(--radius-md);
  padding: 14px;
  box-shadow: var(--shadow-xs);
  border: 1px solid rgba(0,0,0,0.02);
}

.comment-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: var(--gradient-primary);
  color: #fff;
  font-size: 14px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.comment-avatar.small {
  width: 26px;
  height: 26px;
  font-size: 11px;
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.comment-name {
  font-size: 13px;
  font-weight: 700;
  color: var(--text-primary);
}

.comment-time {
  font-size: 11px;
  color: var(--text-muted);
}

.comment-text {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
  word-break: break-all;
}

.comment-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 6px;
}

.comment-like-btn {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 12px;
  color: var(--text-muted);
  cursor: pointer;
  transition: color 0.2s;
  font-weight: 500;
}

.comment-like-btn.liked {
  color: #FF5E2C;
}

.comment-reply-btn {
  font-size: 12px;
  color: var(--primary);
  cursor: pointer;
  display: inline-block;
}

.reply-item {
  display: flex;
  gap: 8px;
  margin-top: 10px;
  padding: 10px 12px;
  background: #FFFAF5;
  border-radius: var(--radius-sm);
}

.comment-empty {
  text-align: center;
  padding: 30px 16px;
  color: var(--text-muted);
  font-size: 14px;
}

.comment-input-wrap {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.reply-hint {
  font-size: 11px;
  color: var(--primary);
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 0 4px;
}

.reply-cancel {
  cursor: pointer;
  opacity: 0.6;
}
</style>
