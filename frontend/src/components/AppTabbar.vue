<template>
  <div class="tabbar">
    <router-link to="/home" class="tab-item" :class="{ active: $route.path === '/home' }">
      <svg viewBox="0 0 24 24" width="24" height="24">
        <path d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-4 0a1 1 0 01-1-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 01-1 1" stroke="currentColor" stroke-width="1.8" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <span>首页</span>
    </router-link>

    <router-link to="/member" class="tab-item" :class="{ active: $route.path === '/member' }">
      <svg viewBox="0 0 24 24" width="24" height="24">
        <path d="M5 16L3 5l5.5 5L12 4l3.5 6L21 5l-2 11H5zm14 2H5v1a1 1 0 001 1h12a1 1 0 001-1v-1z" stroke="currentColor" stroke-width="1.8" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <span>会员</span>
    </router-link>

    <router-link to="/favorites" class="tab-item" :class="{ active: $route.path === '/favorites' }">
      <svg viewBox="0 0 24 24" width="24" height="24">
        <path d="M20.84 4.61a5.5 5.5 0 00-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 00-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 000-7.78z" stroke="currentColor" stroke-width="1.8" fill="none"/>
      </svg>
      <span>收藏</span>
    </router-link>

    <router-link to="/profile" class="tab-item" :class="{ active: $route.path === '/profile' }">
      <svg viewBox="0 0 24 24" width="24" height="24">
        <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" stroke="currentColor" stroke-width="1.8" fill="none" stroke-linecap="round"/>
        <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="1.8" fill="none"/>
      </svg>
      <span>我的</span>
    </router-link>
  </div>
</template>

<style scoped>
.tabbar {
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 375px;
  height: 64px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(24px) saturate(1.4);
  -webkit-backdrop-filter: blur(24px) saturate(1.4);
  border-top: 1px solid var(--border-light);
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 0 8px;
  padding-bottom: env(safe-area-inset-bottom, 0);
  z-index: 100;
}

.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3px;
  font-family: var(--font-heading);
  font-size: 10px;
  font-weight: 500;
  color: var(--text-muted);
  text-decoration: none;
  transition: all 0.3s var(--ease-smooth);
  padding: 6px 10px;
  border-radius: var(--radius-md);
  position: relative;
}

.tab-item.active {
  color: var(--primary);
  font-weight: 700;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 22px;
  height: 3px;
  background: var(--gradient-primary);
  border-radius: 0 0 3px 3px;
}
</style>
