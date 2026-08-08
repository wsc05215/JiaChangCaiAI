<template>
  <div class="member-page">
    <div class="nav-bar">
      <div class="back-btn" @click="goBack">
        <svg viewBox="0 0 24 24" width="24" height="24">
          <path d="M15 18l-6-6 6-6" stroke="#6B5E52" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <span class="nav-title">会员中心</span>
      <div class="nav-placeholder"></div>
    </div>

    <div v-if="!memberChecked" class="loading-state">
      <div class="loading-spinner"></div>
    </div>

    <template v-else-if="isMember">
    <div class="member-card">
      <div class="member-card-shine"></div>
      <div class="member-card-inner">
        <div class="member-avatar">
          <img v-if="memberInfo.avatar" :src="memberInfo.avatar" class="avatar-img" />
          <span v-else class="avatar-text">{{ initial }}</span>
        </div>
        <div class="member-info">
          <div class="member-level">{{ memberInfo.level || '超级大厨神' }}</div>
          <div class="member-expire">{{ memberInfo.expireTime || '' }}到期</div>
        </div>
        <div class="member-badge">尊享会员</div>
      </div>
    </div>
    </template>

    <template v-else>
    <div class="non-member-hint">
      <div class="hint-crown">&#x1F451;</div>
      <div class="hint-text">开通会员，解锁全部AI功能</div>
    </div>

    <div class="section-title">会员专属权益</div>
    <div class="benefits-row">
      <div class="benefit-card">
        <div class="benefit-icon">&#x1F4D6;</div>
        <div class="benefit-name">专属食谱</div>
        <div class="benefit-desc">根据你的需求为你定制专属食谱</div>
      </div>
      <div class="benefit-card">
        <div class="benefit-icon">&#x1F4CB;</div>
        <div class="benefit-name">一键配餐</div>
        <div class="benefit-desc">用普通的食材做出不普通的美食</div>
      </div>
      <div class="benefit-card">
        <div class="benefit-icon">&#x1F96C;</div>
        <div class="benefit-name">食材管理</div>
        <div class="benefit-desc">食材记录管理过期自动提醒</div>
      </div>
    </div>

    <div v-if="!isMember" class="section-title">选择套餐</div>
    <div v-if="!isMember" class="plans">
      <div class="plan-card">
        <div class="plan-badge">次卡</div>
        <div class="plan-info">
          <div class="plan-name">食谱定制会员</div>
          <div class="plan-desc">根据您的需求和口味偏好定制一周食谱</div>
        </div>
        <div class="plan-price"><span class="price-num">¥5.9</span>/次</div>
        <button class="plan-btn" @click="handleBuy('recipe')">开通</button>
      </div>
      <div class="plan-card">
        <div class="plan-badge">月卡</div>
        <div class="plan-info">
          <div class="plan-name">食材管理会员</div>
          <div class="plan-desc">帮您管理家中食材 根据食材推荐食谱</div>
        </div>
        <div class="plan-price"><span class="price-num">¥18</span>/月</div>
        <button class="plan-btn" @click="handleBuy('ingredient')">开通</button>
      </div>
      <div class="plan-card plan-premium">
        <div class="plan-badge plan-badge-gold">推荐</div>
        <div class="plan-info">
          <div class="plan-name">尊享会员</div>
          <div class="plan-sub">享以上两种会员待遇 性价比之选</div>
        </div>
        <div class="plan-price"><span class="price-num price-num-gold">¥128</span>/年</div>
        <button class="plan-btn plan-btn-gold" @click="handleBuy('premium')">立即开通</button>
      </div>
    </div>

    </template>

    <div v-if="isMember" class="already-member">
      <div class="already-icon">&#x2714;</div>
      <div>您已是尊享会员，尽情享受所有功能</div>
    </div>

    <MemberPayModal
      v-if="showPayModal"
      @close="showPayModal = false"
      @success="onPaySuccess"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { userStore } from '../store/user'
import { checkMember, getExpireTime, getMemberInfo } from '../api/member'
import { goBack } from '../router'
import MemberPayModal from '../components/MemberPayModal.vue'

const isMember = ref(false)
const memberChecked = ref(false)
const showPayModal = ref(false)
const memberInfo = ref({
  avatar: '',
  level: '',
  expireTime: '',
  mealCount: 0,
})

const initial = computed(() => {
  const name = userStore.user?.nickName || userStore.user?.username || ''
  return name.charAt(0).toUpperCase()
})

onMounted(async () => {
  const uid = userStore.user?.userId
  if (!uid) { memberChecked.value = true; return }
  isMember.value = await checkMember(uid)
  memberChecked.value = true
  if (isMember.value) {
    memberInfo.value.avatar = userStore.user?.avatar || ''
    memberInfo.value.expireTime = formatExpire(await getExpireTime(uid))
    try {
      const info = await getMemberInfo(uid)
      memberInfo.value.level = info.level || ''
    } catch { /* ignore */ }
  }
})

function handleBuy() {
  showPayModal.value = true
}

async function onPaySuccess() {
  showPayModal.value = false
  const uid = userStore.user?.userId
  if (uid) {
    memberChecked.value = false
    isMember.value = await checkMember(uid)
    memberChecked.value = true
    if (isMember.value) {
      memberInfo.value.expireTime = formatExpire(await getExpireTime(uid))
      try {
        const info = await getMemberInfo(uid)
        memberInfo.value.level = info.level || ''
      } catch { /* ignore */ }
    }
  }
}

function formatExpire(time) {
  if (!time) return ''
  let d
  if (Array.isArray(time)) {
    d = new Date(time[0], time[1] - 1, time[2], time[3] || 0, time[4] || 0, time[5] || 0)
  } else {
    d = new Date(time)
  }
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}
</script>

<style scoped>
.member-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: var(--gradient-page);
  padding-bottom: 40px;
}

.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 8px;
}

.back-btn {
  width: 36px; height: 36px;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; border-radius: 50%;
}

.nav-title { font-size: 18px; font-weight: 800; color: var(--text-primary); letter-spacing: 0.5px; }
.nav-placeholder { width: 36px; }

/* loading */
.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
}

.loading-spinner {
  width: 36px;
  height: 36px;
  border: 3px solid #EDE8DF;
  border-top-color: var(--primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

/* member card */
.member-card {
  margin: 20px 16px 0;
  background: linear-gradient(135deg, #FFF7ED 0%, #FFF1E0 40%, #FFE8D0 100%);
  border-radius: var(--radius-2xl);
  padding: 28px 22px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(255, 122, 51, 0.10);
  border: 1px solid #FDD8B8;
}

.member-card-shine {
  position: absolute;
  top: -50%;
  right: -30%;
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(255, 122, 51, 0.08) 0%, transparent 70%);
  border-radius: 50%;
}

.member-card-inner {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 20px;
  position: relative;
  z-index: 1;
}

.member-avatar {
  width: 54px; height: 54px;
  border-radius: 50%;
  background: #555;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
  border: 2.5px solid #FDD8B8;
  box-shadow: 0 4px 14px rgba(255, 122, 51, 0.10);
  overflow: hidden;
}

.avatar-img { width: 100%; height: 100%; border-radius: 50%; object-fit: cover; }
.avatar-text { font-size: 24px; font-weight: 800; color: #C89460; }

.member-info { flex: 1; position: relative; z-index: 1; }
.member-level { font-size: 20px; font-weight: 800; letter-spacing: 1px; color: #5C3D1A; }
.member-expire { font-size: 12px; color: #B08860; margin-top: 6px; }

.member-badge {
  background: var(--gradient-primary);
  color: #fff;
  font-size: 12px; font-weight: 800;
  padding: 7px 16px;
  border-radius: var(--radius-full);
  letter-spacing: 1.5px;
  box-shadow: var(--shadow-primary);
  position: relative; z-index: 1;
}

/* non-member */
.non-member-hint { text-align: center; padding: 36px 0 10px; }
.hint-crown { font-size: 54px; margin-bottom: 12px; }
.hint-text { font-size: 15px; color: var(--text-secondary); font-weight: 600; }

.section-title {
  font-size: 18px; font-weight: 800; color: var(--text-primary);
  padding: 26px 16px 14px;
  letter-spacing: 0.5px;
}

/* benefits */
.benefits-row {
  display: flex;
  gap: 10px;
  padding: 0 16px;
}

.benefit-card {
  flex: 1;
  background: #fff;
  border-radius: var(--radius-xl);
  padding: 18px 10px;
  text-align: center;
  box-shadow: var(--shadow-xs);
  border: 1px solid rgba(0,0,0,0.03);
  transition: all 0.2s;
}

.benefit-card:active { transform: scale(0.96); }
.benefit-icon { font-size: 28px; margin-bottom: 10px; }
.benefit-name { font-size: 14px; font-weight: 800; color: var(--text-primary); margin-bottom: 4px; }
.benefit-desc { font-size: 11px; color: var(--text-muted); line-height: 1.5; }

/* plans */
.plans { padding: 0 16px; display: flex; flex-direction: column; gap: 12px; }

.plan-card {
  background: #fff;
  border-radius: var(--radius-xl);
  padding: 18px 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: var(--shadow-xs);
  border: 2px solid var(--border);
  transition: all 0.25s;
  position: relative;
}

.plan-card:active { transform: scale(0.98); }

.plan-premium {
  border-color: #F5C34B;
  box-shadow: 0 4px 24px rgba(240,165,0,0.1);
  background: #FFFDF6;
}

.plan-badge {
  position: absolute;
  top: -10px; left: 16px;
  background: var(--text-secondary);
  color: #fff;
  font-size: 11px; font-weight: 800;
  padding: 3px 12px;
  border-radius: var(--radius-full);
  letter-spacing: 1px;
}

.plan-badge-gold {
  background: var(--gradient-gold);
  box-shadow: var(--shadow-gold);
}

.plan-info { flex: 1; min-width: 0; }
.plan-name { font-size: 15px; font-weight: 800; color: var(--text-primary); }
.plan-desc { font-size: 12px; color: var(--text-muted); margin-top: 2px; }
.plan-sub { font-size: 12px; color: var(--text-muted); margin-top: 2px; }

.plan-price { flex-shrink: 0; font-size: 12px; color: var(--text-muted); text-align: right; }
.price-num { font-size: 22px; font-weight: 900; color: var(--primary); }
.price-num-gold { font-size: 22px; font-weight: 900; color: var(--gold); }

.plan-btn {
  flex-shrink: 0;
  padding: 9px 18px;
  border-radius: var(--radius-full);
  border: 2px solid var(--primary);
  color: var(--primary);
  font-size: 13px; font-weight: 800;
  background: #fff;
  cursor: pointer;
  transition: all 0.25s;
}

.plan-btn:active {
  background: var(--primary);
  color: #fff;
}

.plan-btn-gold {
  border-color: var(--gold);
  background: var(--gradient-gold);
  color: #fff;
  box-shadow: var(--shadow-gold);
}

.plan-btn-gold:active { opacity: 0.9; transform: scale(0.95); }

/* already member */
.already-member {
  text-align: center;
  padding: 30px;
  color: var(--primary);
  font-size: 16px; font-weight: 700;
}

.already-icon {
  width: 56px; height: 56px;
  border-radius: 50%;
  background: var(--gradient-gold);
  color: #fff;
  font-size: 28px;
  display: flex; align-items: center; justify-content: center;
  margin: 0 auto 14px;
  box-shadow: var(--shadow-gold);
}
</style>
