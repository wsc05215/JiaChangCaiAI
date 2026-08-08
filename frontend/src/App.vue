<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'

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
