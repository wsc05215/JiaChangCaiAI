<template>
  <div class="region-picker">
    <div class="picker-body">
      <!-- 省 -->
      <div class="picker-col">
        <div class="col-label">省</div>
        <div class="col-scroll" ref="provinceRef" @scroll="onScroll('province')">
          <div class="spacer"></div>
          <div
            v-for="(p, i) in provinces" :key="i"
            class="item"
            :class="{ sel: provIdx === i }"
            @click="pickProvince(i)"
          >{{ p.name }}</div>
          <div class="spacer"></div>
        </div>
      </div>
      <!-- 市 -->
      <div class="picker-col">
        <div class="col-label">市</div>
        <div class="col-scroll" ref="cityRef" @scroll="onScroll('city')">
          <div class="spacer"></div>
          <div
            v-for="(c, i) in cities" :key="i"
            class="item"
            :class="{ sel: cityIdx === i }"
            @click="pickCity(i)"
          >{{ c.name }}</div>
          <div class="spacer"></div>
        </div>
      </div>
      <!-- 区 -->
      <div class="picker-col">
        <div class="col-label">区/县</div>
        <div class="col-scroll" ref="districtRef" @scroll="onScroll('district')">
          <div class="spacer"></div>
          <div
            v-for="(d, i) in districts" :key="i"
            class="item"
            :class="{ sel: distIdx === i }"
            @click="pickDistrict(i)"
          >{{ d }}</div>
          <div class="spacer"></div>
        </div>
      </div>
    </div>
    <!-- 中间高亮条 -->
    <div class="center-line"></div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted } from 'vue'
import regions from '../data/regions.json'

const ITEM_H = 44

const emit = defineEmits(['update:province', 'update:city', 'update:district'])

const provinces = regions
const provIdx = ref(0)
const cityIdx = ref(0)
const distIdx = ref(0)

const provinceRef = ref(null)
const cityRef = ref(null)
const districtRef = ref(null)

const programmatic = ref(false)

const cities = computed(() => provinces[provIdx.value]?.cities || [])
const districts = computed(() => cities.value[cityIdx.value]?.districts || [])

function lockScroll(fn) {
  programmatic.value = true
  fn()
  setTimeout(() => { programmatic.value = false }, 350)
}

function scrollTo(el, idx) {
  if (!el) return
  el.scrollTo({ top: idx * ITEM_H, behavior: 'smooth' })
}

function onScroll(col) {
  if (programmatic.value) return
  const refMap = { province: provinceRef, city: cityRef, district: districtRef }
  const el = refMap[col]?.value
  if (!el) return
  const idx = Math.round(el.scrollTop / ITEM_H)
  if (col === 'province' && idx !== provIdx.value) {
    provIdx.value = Math.max(0, Math.min(idx, provinces.length - 1))
    cityIdx.value = 0
    distIdx.value = 0
    lockScroll(() => {
      scrollTo(cityRef.value, 0)
      scrollTo(districtRef.value, 0)
    })
    emitChange()
  } else if (col === 'city' && idx !== cityIdx.value) {
    cityIdx.value = Math.max(0, Math.min(idx, cities.value.length - 1))
    distIdx.value = 0
    lockScroll(() => scrollTo(districtRef.value, 0))
    emitChange()
  } else if (col === 'district' && idx !== distIdx.value) {
    distIdx.value = Math.max(0, Math.min(idx, districts.value.length - 1))
    emitChange()
  }
}

function pickProvince(i) {
  provIdx.value = i; cityIdx.value = 0; distIdx.value = 0
  lockScroll(() => { scrollTo(provinceRef.value, i); scrollTo(cityRef.value, 0); scrollTo(districtRef.value, 0) })
  emitChange()
}
function pickCity(i) {
  cityIdx.value = i; distIdx.value = 0
  lockScroll(() => { scrollTo(cityRef.value, i); scrollTo(districtRef.value, 0) })
  emitChange()
}
function pickDistrict(i) {
  distIdx.value = i
  lockScroll(() => scrollTo(districtRef.value, i))
  emitChange()
}

function emitChange() {
  const p = provinces[provIdx.value]
  const c = cities.value[cityIdx.value]
  const d = districts.value[distIdx.value]
  emit('update:province', p?.name || '')
  emit('update:city', c?.name || '')
  emit('update:district', d || '')
}

onMounted(() => {
  emitChange()
})
</script>

<style scoped>
.region-picker {
  position: relative;
  height: 260px;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
}

.picker-body {
  display: flex;
  height: 100%;
  position: relative;
  z-index: 1;
}

.picker-col {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.col-label {
  text-align: center;
  font-size: 12px;
  font-weight: 600;
  color: var(--text-muted, #a3988c);
  padding: 10px 0 4px;
  flex-shrink: 0;
}

.col-scroll {
  flex: 1;
  overflow-y: auto;
  scroll-snap-type: y mandatory;
  -webkit-overflow-scrolling: touch;
  padding: 0 8px;
  scrollbar-width: none;
}

.col-scroll::-webkit-scrollbar { display: none; }

.spacer {
  height: calc(130px - 22px); /* half of picker height minus half item */
  flex-shrink: 0;
}

.item {
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: #c5bfb5;
  scroll-snap-align: center;
  cursor: pointer;
  transition: color 0.15s, font-weight 0.15s;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  padding: 0 4px;
}

.item.sel {
  color: var(--text-primary, #3d3226);
  font-weight: 700;
  font-size: 17px;
}

.center-line {
  position: absolute;
  top: 50%;
  left: 12px;
  right: 12px;
  height: 44px;
  transform: translateY(-50%);
  border-top: 1px solid #f0ebe0;
  border-bottom: 1px solid #f0ebe0;
  pointer-events: none;
  z-index: 0;
  border-radius: 4px;
  background: linear-gradient(180deg, rgba(255,122,51,0.04) 0%, rgba(255,122,51,0.02) 100%);
}
</style>
