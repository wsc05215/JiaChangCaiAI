<template>
  <div class="member-page">
    <!-- 顶部导航 -->
    <div class="nav-bar">
      <div class="back-btn" @click="$router.back()">
        <svg viewBox="0 0 24 24" width="24" height="24">
          <path d="M15 18l-6-6 6-6" stroke="#555" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <span class="nav-title">开通会员</span>
      <div class="nav-placeholder"></div>
    </div>

    <!-- 会员信息卡片（已是会员时显示） -->
    <div v-if="isMember" class="member-card">
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
      <div class="member-stats">
        已经陪您吃过 <span class="stats-num">{{ memberInfo.mealCount || 0 }}</span> 顿饭了
      </div>
    </div>

    <!-- 非会员时显示 -->
    <div v-else class="non-member-hint">
      <div class="hint-icon">👑</div>
      <div class="hint-text">开通会员，解锁全部AI功能</div>
    </div>

    <!-- 会员专属权益 -->
    <div class="section-title">会员专属权益</div>
    <div class="benefits-row">
      <div class="benefit-card">
        <div class="benefit-icon icon-recipe">
          <svg viewBox="0 0 24 24" width="24" height="24" fill="none">
            <path d="M6 2h12l2 3H4l2-3zM4 7h16v13a2 2 0 01-2 2H6a2 2 0 01-2-2V7z" stroke="#E85D26" stroke-width="1.5"/>
            <circle cx="12" cy="14" r="2" fill="#E85D26"/>
          </svg>
        </div>
        <div class="benefit-name">专属食谱</div>
        <div class="benefit-desc">根据你的需求为你定制专属食谱</div>
      </div>
      <div class="benefit-card">
        <div class="benefit-icon icon-menu">
          <svg viewBox="0 0 24 24" width="24" height="24" fill="none">
            <rect x="3" y="3" width="7" height="7" rx="1" stroke="#E85D26" stroke-width="1.5"/>
            <rect x="14" y="3" width="7" height="7" rx="1" stroke="#E85D26" stroke-width="1.5"/>
            <rect x="3" y="14" width="7" height="7" rx="1" stroke="#E85D26" stroke-width="1.5"/>
            <rect x="14" y="14" width="7" height="7" rx="1" stroke="#E85D26" stroke-width="1.5"/>
          </svg>
        </div>
        <div class="benefit-name">一键配餐</div>
        <div class="benefit-desc">用普通的食材做出不普通的美食</div>
      </div>
      <div class="benefit-card">
        <div class="benefit-icon icon-manage">
          <svg viewBox="0 0 24 24" width="24" height="24" fill="none">
            <rect x="3" y="5" width="18" height="14" rx="2" stroke="#E85D26" stroke-width="1.5"/>
            <path d="M3 10h18M8 5v14" stroke="#E85D26" stroke-width="1.5"/>
          </svg>
        </div>
        <div class="benefit-name">食材管理</div>
        <div class="benefit-desc">食材记录管理过期自动提醒</div>
      </div>
    </div>

    <!-- 开通会员（非会员时显示） -->
    <div v-if="!isMember" class="section-title">开通会员</div>
    <div v-if="!isMember" class="plans">
      <div class="plan-card">
        <div class="plan-info">
          <div class="plan-name">食谱定制会员</div>
          <div class="plan-desc">根据您的需求和口味偏好为您定制一周食谱</div>
        </div>
        <div class="plan-price"><span class="price-num">￥5.9</span>/次</div>
        <button class="plan-btn" @click="handleBuy('recipe')">立即开通</button>
      </div>
      <div class="plan-card">
        <div class="plan-info">
          <div class="plan-name">食材管理会员</div>
          <div class="plan-desc">帮您管理家中的食材根据食材推荐食谱</div>
        </div>
        <div class="plan-price"><span class="price-num">￥18</span>/月</div>
        <button class="plan-btn" @click="handleBuy('ingredient')">立即开通</button>
      </div>
      <div class="plan-card plan-premium">
        <div class="plan-info">
          <div class="plan-name">尊享会员</div>
          <div class="plan-sub">享以上两种会员待遇</div>
        </div>
        <div class="plan-price"><span class="price-num">￥128</span>/年</div>
        <button class="plan-btn plan-btn-gold" @click="handleBuy('premium')">立即开通</button>
      </div>
    </div>

    <!-- 已是会员 -->
    <div v-else class="already-member">
      <div class="already-icon">✓</div>
      <div>您已是尊享会员，尽情享受所有功能</div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { userStore } from '../store/user'
import { checkMember } from '../api/member'

const isMember = ref(false)
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
  if (!uid) return
  isMember.value = await checkMember(uid)
  if (isMember.value) {
    memberInfo.value.avatar = userStore.user?.avatar || ''
  }
})

function handleBuy(type) {
  // TODO: 接入支付
  alert('支付功能开发中')
}
</script>

<style scoped>
.member-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: linear-gradient(180deg, #fef9f4 0%, var(--bg) 30%);
  padding-bottom: 40px;
}

/* 导航 */
.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 8px;
}

.back-btn {
  width: 36px; height: 36px;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; border-radius: 50%;
}

.nav-title { font-size: 16px; font-weight: 600; color: #333; }
.nav-placeholder { width: 36px; }

/* 会员卡片 */
.member-card {
  margin: 16px 16px 0;
  background: linear-gradient(135deg, #3a3a3a, #1a1a1a);
  border-radius: 16px;
  padding: 20px;
  color: #fff;
}

.member-card-inner {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.member-avatar {
  width: 48px; height: 48px;
  border-radius: 50%;
  background: #555;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}

.avatar-img { width: 100%; height: 100%; border-radius: 50%; object-fit: cover; }
.avatar-text { font-size: 20px; font-weight: 700; color: #ddd; }

.member-info { flex: 1; }
.member-level { font-size: 17px; font-weight: 600; }
.member-expire { font-size: 12px; color: #aaa; margin-top: 2px; }

.member-badge {
  background: linear-gradient(135deg, #f5a623, #e8961a);
  color: #fff;
  font-size: 12px; font-weight: 600;
  padding: 4px 12px; border-radius: 12px;
}

.member-stats { text-align: center; font-size: 13px; color: #ccc; }
.stats-num { font-size: 18px; font-weight: 700; color: #f5a623; }

/* 非会员提示 */
.non-member-hint {
  text-align: center;
  padding: 30px 0 10px;
}
.hint-icon { font-size: 40px; margin-bottom: 8px; }
.hint-text { font-size: 14px; color: #999; }

/* 章节标题 */
.section-title {
  font-size: 16px; font-weight: 600; color: #333;
  padding: 20px 16px 12px;
}

/* 权益 */
.benefits-row {
  display: flex;
  gap: 10px;
  padding: 0 16px;
}

.benefit-card {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  padding: 14px 10px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.benefit-icon {
  width: 40px; height: 40px;
  border-radius: 50%;
  background: #fef5ee;
  display: flex; align-items: center; justify-content: center;
  margin: 0 auto 8px;
}

.benefit-name { font-size: 13px; font-weight: 600; color: #333; margin-bottom: 4px; }
.benefit-desc { font-size: 11px; color: #999; line-height: 1.4; }

/* 套餐 */
.plans { padding: 0 16px; display: flex; flex-direction: column; gap: 10px; }

.plan-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  border: 1px solid #eee;
}

.plan-premium { border-color: #f5a623; }

.plan-info { flex: 1; min-width: 0; }
.plan-name { font-size: 15px; font-weight: 600; color: #333; }
.plan-desc { font-size: 12px; color: #999; margin-top: 2px; }
.plan-sub { font-size: 12px; color: #999; margin-top: 2px; }

.plan-price { flex-shrink: 0; font-size: 12px; color: #999; }
.price-num { font-size: 18px; font-weight: 700; color: #F08B4F; }

.plan-btn {
  flex-shrink: 0;
  padding: 8px 16px;
  border-radius: 20px;
  border: 2px solid #E85D26;
  color: #E85D26;
  font-size: 13px; font-weight: 600;
  background: #fff;
  cursor: pointer;
}

.plan-btn-gold {
  border-color: #f5a623;
  background: linear-gradient(135deg, #f5a623, #e8961a);
  color: #fff;
}

.plan-btn:active { transform: scale(0.96); }

/* 已是会员 */
.already-member {
  text-align: center;
  padding: 30px;
  color: #E85D26;
  font-size: 15px; font-weight: 600;
}
.already-icon {
  width: 48px; height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f5a623, #e8961a);
  color: #fff;
  font-size: 24px;
  display: flex; align-items: center; justify-content: center;
  margin: 0 auto 12px;
}
</style>
