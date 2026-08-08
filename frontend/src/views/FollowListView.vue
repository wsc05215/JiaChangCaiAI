<template>
  <div class="follow-list-page">
    <div class="nav-bar">
      <div class="back-btn" @click="$goBack()">
        <svg viewBox="0 0 24 24" width="24" height="24">
          <path d="M15 18l-6-6 6-6" stroke="#6B5E52" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <span class="nav-title">{{ isFollowers ? '粉丝' : '关注' }}</span>
    </div>

    <div class="list-wrap" v-if="users.length">
      <div class="user-item" v-for="u in users" :key="u.userId" @click="goUser(u.userId)">
        <div class="user-avatar">
          <img v-if="u.avatar" :src="u.avatar" @error="e => e.target.style.display='none'" />
          <span v-else>{{ (u.nickName || 'U').charAt(0) }}</span>
        </div>
        <span class="user-name">{{ u.nickName || ('用户' + u.userId) }}</span>
      </div>
    </div>

    <div class="empty" v-else-if="!loading">
      <p>{{ isFollowers ? '暂无粉丝' : '暂未关注任何人' }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getFollowingList, getFollowersList } from '../api/follow'

const route = useRoute()
const router = useRouter()
const users = ref([])
const loading = ref(true)

const isFollowers = computed(() => route.name === 'Followers')

onMounted(async () => {
  const userId = Number(route.params.id)
  if (!userId) return
  try {
    const fn = isFollowers.value ? getFollowersList : getFollowingList
    const res = await fn(userId)
    users.value = res.data || []
  } catch { /* ignore */ }
  finally { loading.value = false }
})

function goUser(id) {
  router.push('/user/' + id)
}
</script>

<style scoped>
.follow-list-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: var(--bg);
  padding-top: env(safe-area-inset-top, 0px);
}

.nav-bar {
  display: flex;
  align-items: center;
  padding: 12px 12px;
  gap: 8px;
}

.back-btn {
  width: 36px; height: 36px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  background: #fff;
  cursor: pointer;
}

.nav-title {
  font-size: 18px; font-weight: 800; color: var(--text-primary);
}

.list-wrap {
  padding: 0 16px;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  background: #fff;
  border-radius: var(--radius-md);
  margin-bottom: 8px;
  cursor: pointer;
  box-shadow: var(--shadow-xs);
}

.user-avatar {
  width: 44px; height: 44px;
  border-radius: 50%;
  background: var(--gradient-primary);
  display: flex; align-items: center; justify-content: center;
  color: #fff;
  font-size: 18px; font-weight: 700;
  overflow: hidden;
  flex-shrink: 0;
}

.user-avatar img {
  width: 100%; height: 100%; object-fit: cover; border-radius: 50%;
}

.user-name {
  font-size: 15px; font-weight: 600; color: var(--text-primary);
}

.empty {
  text-align: center;
  padding: 80px 16px;
  color: var(--text-muted);
  font-size: 14px;
}
</style>
