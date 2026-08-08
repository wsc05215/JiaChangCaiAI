<template>
  <div class="notify-page">
    <div class="nav-bar">
      <div class="back-btn" @click="$goBack()">
        <svg viewBox="0 0 24 24" width="22" height="22">
          <path d="M15 18l-6-6 6-6" stroke="#5a524c" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <span class="nav-title">消息通知</span>
      <button v-if="list.length > 0" class="mark-all-btn" @click="markAll">全部已读</button>
    </div>

    <div v-if="loading" class="loading-wrap"><p>加载中...</p></div>

    <div v-else-if="list.length === 0" class="empty-wrap">
      <svg viewBox="0 0 80 80" width="60" height="60" fill="none">
        <circle cx="40" cy="40" r="38" stroke="#E8DDD2" stroke-width="2" stroke-dasharray="6 4"/>
        <path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9M13.73 21a2 2 0 01-3.46 0" stroke="#E0D5C8" stroke-width="2" fill="none" stroke-linecap="round" transform="translate(22, 15) scale(1.3)"/>
      </svg>
      <p class="empty-text">暂无消息通知</p>
    </div>

    <div v-else class="notify-list">
      <div
        v-for="item in list"
        :key="item.id"
        class="notify-item"
        :class="{ unread: item.isRead === 0 }"
        @click="handleClick(item)"
      >
        <div class="notify-left">
          <div class="notify-avatar">{{ getFromUser(item.fromUserId).charAt(0) }}</div>
          <div class="notify-body">
            <div class="notify-content">
              <span class="notify-name">{{ getFromUser(item.fromUserId) }}</span>
              {{ item.content }}
            </div>
            <div class="notify-time">{{ formatTime(item.createTime) }}</div>
          </div>
        </div>
        <span v-if="item.isRead === 0" class="unread-dot"></span>
      </div>
    </div>

    <transition name="fade">
      <div v-if="toast.show" class="toast" :class="toast.type">{{ toast.msg }}</div>
    </transition>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { userStore } from '../store/user'
import { getNotifications, markNotificationRead, markAllNotificationsRead } from '../api/comment'
import { getUserDetail } from '../api/auth'

const router = useRouter()
const list = ref([])
const loading = ref(true)
const authorCache = ref({})
const toast = reactive({ show: false, msg: '', type: 'success' })

onMounted(async () => {
  const user = userStore.user
  if (!user?.userId) { loading.value = false; return }
  try {
    const res = await getNotifications(user.userId)
    list.value = res.data || []
    list.value.forEach(n => fetchUserName(n.fromUserId))
  } catch { /* ignore */ }
  finally { loading.value = false }
})

async function fetchUserName(userId) {
  if (!userId || authorCache.value[userId]) return
  try {
    const res = await getUserDetail(userId)
    authorCache.value[userId] = res.data?.nickName || ('用户' + userId)
  } catch {
    authorCache.value[userId] = '用户' + userId
  }
}

function getFromUser(userId) {
  return authorCache.value[userId] || '用户' + userId
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

async function handleClick(item) {
  if (item.isRead === 0) {
    try { await markNotificationRead(item.id); item.isRead = 1 } catch { /* ignore */ }
  }
  if (item.commentId && item.recipeId) {
    router.push('/recipe/' + item.recipeId)
  }
}

async function markAll() {
  const user = userStore.user
  if (!user?.userId) return
  try {
    await markAllNotificationsRead(user.userId)
    list.value.forEach(n => n.isRead = 1)
  } catch { /* ignore */ }
}
</script>

<style scoped>
.notify-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: #FBF8F4;
  padding-top: env(safe-area-inset-top, 0px);
  padding-bottom: 40px;
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

.mark-all-btn {
  font-size: 13px;
  font-weight: 600;
  color: var(--primary);
  padding: 4px 12px;
  border-radius: 14px;
  background: rgba(255,122,51,0.06);
  border: none;
  cursor: pointer;
}

.loading-wrap { display: flex; justify-content: center; align-items: center; height: 60vh; color: var(--text-muted); }

.empty-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 50vh;
  gap: 12px;
}

.empty-text { font-size: 15px; color: var(--text-muted); }

.notify-list { padding: 8px 16px; display: flex; flex-direction: column; gap: 6px; }

.notify-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-radius: 14px;
  padding: 14px 16px;
  cursor: pointer;
  transition: all 0.2s;
  gap: 10px;
}

.notify-item:active { background: #faf7f2; }
.notify-item.unread { background: #FFF9F5; border-left: 3px solid var(--primary); }

.notify-left { display: flex; align-items: center; gap: 10px; flex: 1; min-width: 0; }

.notify-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--gradient-primary);
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.notify-body { flex: 1; min-width: 0; }

.notify-content { font-size: 14px; color: var(--text-secondary); line-height: 1.4; }

.notify-name { font-weight: 700; color: var(--text-primary); }

.notify-time { font-size: 11px; color: var(--text-muted); margin-top: 4px; }

.unread-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--primary);
  flex-shrink: 0;
}

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
