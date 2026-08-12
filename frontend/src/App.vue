<template>
  <router-view v-slot="{ Component }">
    <transition name="page" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>

  <AppTabbar v-if="showTabbar" />
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import AppTabbar from './components/AppTabbar.vue'

// 底部导航只在这四个 tab 路由显示，切 tab 时 tabbar 常驻不跳变
const TAB_ROUTES = ['/home', '/custom', '/favorites', '/profile']
const route = useRoute()
const showTabbar = computed(() => TAB_ROUTES.includes(route.path))

onMounted(() => {
  const splash = document.getElementById('app-splash')
  if (!splash) return

  const MIN_DISPLAY = 900

  const elapsed = performance.now()
  const remaining = Math.max(0, MIN_DISPLAY - elapsed)

  setTimeout(() => {
    splash.classList.add('reveal')
    setTimeout(() => {
      splash.classList.add('fade-out')
      splash.addEventListener('transitionend', () => splash.remove())
      setTimeout(() => { if (splash.parentNode) splash.remove() }, 500)
    }, 150)
  }, remaining)
})
</script>

<style scoped>
/* 路由切换过渡：进入淡入+轻微上移，离开快速淡出，页面切换不生硬 */
.page-enter-active {
  transition: opacity 0.24s ease, transform 0.24s var(--ease-smooth);
}
.page-leave-active {
  transition: opacity 0.15s ease;
}
.page-enter-from {
  opacity: 0;
  transform: translateY(12px);
}
.page-leave-to {
  opacity: 0;
}
</style>
