<template>
  <div class="tabbar">
    <router-link to="/home" class="tab-item" :class="{ active: $route.path === '/home' }">
      <svg viewBox="0 0 24 24" width="24" height="24">
        <path d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-4 0a1 1 0 01-1-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 01-1 1" stroke="currentColor" stroke-width="1.8" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <span>首页</span>
    </router-link>

    <router-link to="/custom" class="tab-item" :class="{ active: $route.path === '/custom' }">
      <svg viewBox="0 0 24 24" width="24" height="24">
        <path d="M5 16L3 5l5.5 5L12 4l3.5 6L21 5l-2 11H5zm14 2H5v1a1 1 0 001 1h12a1 1 0 001-1v-1z" stroke="currentColor" stroke-width="1.8" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <span>定制</span>
    </router-link>

    <div class="tab-item tab-add" @click="showMenu = true">
      <div class="add-circle">
        <svg viewBox="0 0 24 24" width="22" height="22">
          <path d="M12 5v14M5 12h14" stroke="#fff" stroke-width="2.2" stroke-linecap="round"/>
        </svg>
      </div>
    </div>

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

    <!-- 发布菜单 -->
    <teleport to="body">
      <div v-if="showMenu" class="menu-overlay" @click="showMenu = false">
        <div class="menu-sheet">
          <div class="menu-handle"></div>
          <div class="menu-title-row">
            <span class="menu-title-main">创作</span>
            <span class="menu-title-sub">分享你的美食灵感</span>
          </div>
          <div class="menu-options">
            <div class="menu-card" @click="goUploadRecipe">
              <div class="menu-card-icon">
                <svg viewBox="0 0 32 32" width="28" height="28" fill="none">
                  <rect x="4" y="6" width="24" height="20" rx="3" stroke="url(#cg1)" stroke-width="2"/>
                  <path d="M4 13h24" stroke="url(#cg1)" stroke-width="2"/>
                  <circle cx="9" cy="9.5" r="1.5" fill="url(#cg1)"/>
                  <path d="M12 23l4-5 3 3 2-3 3 5H12z" stroke="url(#cg1)" stroke-width="1.8" fill="none" stroke-linejoin="round"/>
                  <defs>
                    <linearGradient id="cg1" x1="4" y1="6" x2="28" y2="26">
                      <stop stop-color="#E8783D"/>
                      <stop offset="1" stop-color="#F09055"/>
                    </linearGradient>
                  </defs>
                </svg>
              </div>
              <div class="menu-card-info">
                <div class="menu-card-name">发布菜谱</div>
                <div class="menu-card-desc">图文并茂，手把手教大家做菜</div>
              </div>
              <svg viewBox="0 0 16 16" width="14" height="14" class="menu-card-arrow">
                <path d="M6 4l4 4-4 4" stroke="#C4B5AA" stroke-width="1.6" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
          </div>
          <button class="menu-close" @click="showMenu = false">取消</button>
        </div>
      </div>
    </teleport>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const showMenu = ref(false)

function goUploadRecipe() {
  showMenu.value = false
  router.push('/upload-recipe')
}

</script>

<style scoped>
.tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  min-height: 64px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(24px) saturate(1.4);
  -webkit-backdrop-filter: blur(24px) saturate(1.4);
  border-top: 1px solid var(--border-light);
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 0 8px;
  padding-bottom: calc(8px + env(safe-area-inset-bottom, 0px));
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

/* add button */
.tab-add {
  cursor: pointer;
}

.add-circle {
  width: 42px; height: 42px;
  border-radius: 50%;
  background: var(--gradient-primary);
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 4px 16px rgba(255,122,51,0.35);
  transition: all 0.25s var(--ease-smooth);
  margin-bottom: 2px;
}

.tab-add:active .add-circle {
  transform: scale(0.9);
}

/* menu */
.menu-overlay {
  position: fixed;
  inset: 0;
  background: rgba(20, 14, 8, 0.55);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  z-index: 9999;
  animation: menuFadeIn 0.3s ease;
}

@keyframes menuFadeIn {
  from { opacity: 0; }
  to   { opacity: 1; }
}

.menu-sheet {
  width: 100%;
  max-width: 500px;
  background: #FFFDFA;
  border-radius: 24px 24px 0 0;
  padding: 12px 20px 30px;
  animation: menuSlideUp 0.45s cubic-bezier(0.17, 0.84, 0.44, 1);
  box-shadow: 0 -8px 40px rgba(0,0,0,0.12);
}

@keyframes menuSlideUp {
  0%   { transform: translateY(100%); opacity: 0.6; }
  100% { transform: translateY(0); opacity: 1; }
}

.menu-handle {
  width: 36px; height: 4px;
  border-radius: 2px;
  background: #E0D8CC;
  margin: 0 auto 18px;
}

.menu-title-row {
  text-align: center;
  margin-bottom: 20px;
}

.menu-title-main {
  display: block;
  font-size: 20px; font-weight: 800;
  color: var(--text-primary);
  letter-spacing: 0.5px;
}

.menu-title-sub {
  display: block;
  font-size: 13px; color: var(--text-muted);
  margin-top: 4px;
}

.menu-options {
  margin-bottom: 16px;
}

.menu-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px;
  background: #fff;
  border-radius: 18px;
  cursor: pointer;
  border: 1.5px solid rgba(0,0,0,0.04);
  box-shadow: 0 2px 16px rgba(30, 21, 15, 0.05);
  transition: all 0.25s var(--ease-smooth);
}

.menu-card:active {
  background: #FFF8F2;
  border-color: rgba(232, 120, 61, 0.15);
  transform: scale(0.985);
  box-shadow: 0 4px 20px rgba(232, 120, 61, 0.08);
}

.menu-card-icon {
  width: 52px; height: 52px;
  border-radius: 16px;
  background: linear-gradient(135deg, #FFF0E5, #FFE4D2);
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}

.menu-card-info { flex: 1; min-width: 0; }

.menu-card-name {
  font-size: 16px; font-weight: 700;
  color: var(--text-primary);
}

.menu-card-desc {
  font-size: 12px; color: var(--text-muted);
  margin-top: 3px;
}

.menu-card-arrow { flex-shrink: 0; }

.menu-close {
  width: 100%;
  padding: 15px;
  border-radius: 16px;
  background: #F5F2ED;
  color: var(--text-secondary);
  font-size: 16px; font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
}

.menu-close:active {
  background: #EBE6DE;
  transform: scale(0.98);
}
</style>
