<template>
  <div class="profile-page">
    <!-- 头像区域 -->
    <div class="header-section">
      <div class="avatar-wrapper">
        <img v-if="userInfo.avatar" :src="userInfo.avatar" class="avatar-img" @error="userInfo.avatar = ''" />
        <span v-else class="avatar-placeholder">{{ initial }}</span>
      </div>
      <div class="header-right">
        <div class="nickname-row">
          <span class="nickname">{{ userInfo.nickName || userStore.user?.nickName || '未登录' }}</span>
          <span v-if="isMember" class="vip-tag">VIP用户</span>
        </div>
        <div class="bio">热爱生活，享受下厨的每一刻</div>
      </div>
      <button class="edit-btn" @click="handleEdit">编辑</button>
    </div>

    <!-- 数据统计卡片 -->
    <div class="stats-card">
      <div class="stat-item">
        <div class="stat-num">{{ stats.works }}</div>
        <div class="stat-label">作品</div>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <div class="stat-num">{{ stats.following }}</div>
        <div class="stat-label">关注</div>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <div class="stat-num">{{ stats.followers }}</div>
        <div class="stat-label">粉丝</div>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <div class="stat-num">{{ stats.likes }}</div>
        <div class="stat-label">获赞</div>
      </div>
    </div>

    <!-- 菜单卡片 -->
    <div class="menu-card">
      <div
        v-for="(item, index) in menuItems"
        :key="item.label"
        class="menu-item"
        :class="{ 'menu-item--last': index === menuItems.length - 1 }"
        @click="handleMenuClick(item)"
      >
        <span class="menu-label">{{ item.label }}</span>
        <svg class="menu-arrow" viewBox="0 0 24 24" width="16" height="16">
          <path d="M9 18l6-6-6-6" stroke="#B99E8E" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
    </div>

    <!-- 底部导航栏 -->
    <AppTabbar />
  </div>
</template>

<script setup>
import { computed, reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { userStore } from '../store/user'
import { getUserDetail, getWorkCount, getLikeCount, getFollowingCount, getFollowerCount } from '../api/user'
import { checkMember } from '../api/member'
import AppTabbar from '../components/AppTabbar.vue'

const router = useRouter()

const isMember = ref(false)

const userInfo = reactive({
  nickName: '',
  avatar: '',
})

const initial = computed(() => {
  const name = userInfo.nickName || userStore.user?.username || ''
  return name.charAt(0).toUpperCase()
})

const stats = reactive({
  works: 0,
  following: 0,
  followers: 0,
  likes: 0,
})

const menuItems = [
  { label: '我发布的菜谱', route: '/my-recipes' },
  { label: '我的购物车', route: '/cart' },
  { label: '我的订单', route: '/orders' },
  { label: '消息通知', route: '/notifications' },
  { label: '账号设置', route: '/settings' },
  { label: '帮助与反馈', route: '/help' },
]

onMounted(async () => {
  const user = userStore.user
  if (!user || !user.userId) {
    router.push('/')
    return
  }
  try {
    const [detailRes, worksRes, likesRes, followingRes, followerRes, memberFlag] = await Promise.all([
      getUserDetail(user.userId),
      getWorkCount(user.userId),
      getLikeCount(user.userId),
      getFollowingCount(user.userId),
      getFollowerCount(user.userId),
      checkMember(user.userId),
    ])
    isMember.value = memberFlag
    if (detailRes.data) {
      userInfo.nickName = detailRes.data.nickName || ''
      userInfo.avatar = detailRes.data.avatar || ''
    }
    stats.works = worksRes.data || 0
    stats.likes = likesRes.data || 0
    stats.following = followingRes.data || 0
    stats.followers = followerRes.data || 0
  } catch {
    // 失败保持默认值 0
  }
})

function handleEdit() {}

function handleMenuClick(item) {
  if (item.route) {
    router.push(item.route)
  }
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: var(--bg);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 15px 80px;
}

/* ===== 头像区域 ===== */
.header-section {
  display: flex;
  align-items: center;
  width: 100%;
  margin-top: 34px;
  position: relative;
}

.avatar-wrapper {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  background: #f0e4d6;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  font-size: 28px;
  font-weight: 700;
  color: var(--orange);
}

.header-right {
  margin-left: 14px;
  flex: 1;
  min-width: 0;
}

.nickname-row {
  display: flex;
  align-items: center;
  gap: 6px;
}

.nickname {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  line-height: 1.3;
}

.vip-tag {
  background: linear-gradient(135deg, #f5a623, #e8961a);
  color: #fff;
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 10px;
  white-space: nowrap;
}

.bio {
  font-size: 12px;
  color: var(--gray);
  margin-top: 4px;
}

.edit-btn {
  width: 60px;
  height: 30px;
  border-radius: 15px;
  border: 1px solid var(--orange);
  color: var(--orange);
  font-size: 13px;
  flex-shrink: 0;
  transition: all 0.2s;
}

.edit-btn:active {
  background: var(--orange);
  color: #fff;
}

/* ===== 数据统计卡片 ===== */
.stats-card {
  display: flex;
  align-items: center;
  width: 100%;
  height: 70px;
  background: var(--white);
  border-radius: var(--radius-card);
  margin-top: 20px;
  box-shadow: 0 2px 12px rgba(185, 158, 142, 0.1);
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-num {
  font-size: 18px;
  font-weight: 700;
  color: #333;
}

.stat-label {
  font-size: 12px;
  color: var(--gray);
}

.stat-divider {
  width: 1px;
  height: 30px;
  background: #e8ddd2;
}

/* ===== 菜单卡片 ===== */
.menu-card {
  width: 100%;
  background: var(--white);
  border-radius: var(--radius-card);
  margin-top: 16px;
  box-shadow: 0 2px 12px rgba(185, 158, 142, 0.1);
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 52px;
  padding: 0 18px;
  cursor: pointer;
  transition: background 0.15s;
}

.menu-item:active {
  background: #fdf7f2;
}

.menu-item:not(.menu-item--last) {
  border-bottom: 1px solid #f0e8de;
}

.menu-label {
  font-size: 15px;
  color: #333;
}

.menu-arrow {
  flex-shrink: 0;
}
</style>
