<template>
  <router-view />
  <AiFloatingBall v-if="showBall" />
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AiFloatingBall from './components/AiFloatingBall.vue'

const route = useRoute()
const showBall = computed(() => {
  const path = route.path
  return path !== '/' && path !== '/ai-chat'
})
</script>
