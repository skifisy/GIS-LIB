<template>
  <div style="display:flex; flex-direction:column; gap:8px;">
    <div style="padding:8px; border:1px solid #eee; border-radius:6px; background:#fff">
      <h4 style="margin:0 0 8px 0">添加新图书馆</h4>
      <div style="display:flex; gap:8px; margin-bottom:8px">
        <el-select v-model="provinceIndex" placeholder="选择省" style="flex:1" @change="onProvinceChange">
          <el-option v-for="(p, idx) in regionsData" :key="p.code" :label="p.name" :value="idx" />
        </el-select>
        <el-select v-model="cityIndex" placeholder="选择市" style="flex:1" @change="onCityChange">
          <el-option v-for="(c, idx) in cities" :key="c.code" :label="c.name" :value="idx" />
        </el-select>
      </div>

      <div style="display:flex; gap:8px; margin-bottom:8px">
        <el-select v-model="districtIndex" placeholder="选择区" style="flex:1" @change="onDistrictChange">
          <el-option v-for="(d, idx) in districts" :key="d.code" :label="d.name" :value="idx" />
        </el-select>
        <el-select v-model="streetIndex" placeholder="选择街道" style="flex:1" @change="onStreetChange">
          <el-option v-for="(s, idx) in streets" :key="s.code" :label="s.name" :value="idx" />
        </el-select>
      </div>

      <el-input v-model="form.name" placeholder="图书馆名称" style="margin-bottom:8px" />
      <el-input v-model="form.college" placeholder="学院" style="margin-bottom:8px" />
      <el-input-number v-model="form.numberOfBooks" :min="0" label="藏书数" style="margin-bottom:8px" />

      <div style="display:flex; gap:12px; align-items:center;">
        <div>经度: <strong>{{ displayLon }}</strong></div>
        <div>纬度: <strong>{{ displayLat }}</strong></div>
        <el-button type="primary" size="small" @click="onSave">保存并添加</el-button>
        <el-button size="small" @click="onClear">清除</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import regions from '../data/regions'
import { createLibrary } from '../api'
import { formatCoord } from '../utils/format'

const props = defineProps({ initialPoint: { type: Object, default: null } })
const emit = defineEmits(['region-change', 'created'])

const regionsData = regions
const provinceIndex = ref(null)
const cityIndex = ref(null)
const districtIndex = ref(null)
const streetIndex = ref(null)

const form = ref({ name: '', college: '', numberOfBooks: 0, lon: null, lat: null })

const cities = computed(() => provinceIndex.value != null ? regionsData[provinceIndex.value].cities : [])
const districts = computed(() => (cities.value && cityIndex.value != null) ? cities.value[cityIndex.value].districts : [])
const streets = computed(() => (districts.value && districtIndex.value != null) ? districts.value[districtIndex.value].streets : [])

const displayLon = computed(() => {
  const raw = form.value.lon ?? (props.initialPoint && (props.initialPoint.lng || props.initialPoint.lon))
  return formatCoord(raw)
})
const displayLat = computed(() => {
  const raw = form.value.lat ?? (props.initialPoint && (props.initialPoint.lat || props.initialPoint.lat))
  return formatCoord(raw)
})

watch(() => props.initialPoint, (p) => {
  if (p) {
    form.value.lon = p.lng ?? p.lon
    form.value.lat = p.lat
  }
})

function onProvinceChange() { cityIndex.value = null; districtIndex.value = null; streetIndex.value = null }
function onCityChange() { districtIndex.value = null; streetIndex.value = null }
function onDistrictChange() { streetIndex.value = null }

function onStreetChange() {
  const s = streets.value[streetIndex.value]
  if (s) {
    form.value.lon = s.lon
    form.value.lat = s.lat
    emit('region-change', { lon: s.lon, lat: s.lat })
  }
}

async function onSave() {
  if (!form.value.name) return alert('请输入名称')
  if (form.value.lon == null || form.value.lat == null) return alert('请选择位置或在地图上标点')
  try {
    const payload = { name: form.value.name, college: form.value.college, numberOfBooks: form.value.numberOfBooks, lon: form.value.lon, lat: form.value.lat }
    const created = await createLibrary(payload)
    emit('created', created)
    // keep form values for convenience
  } catch (e) { console.error(e); alert('创建失败') }
}

function onClear() { form.value = { name: '', college: '', numberOfBooks: 0, lon: null, lat: null }; provinceIndex.value = cityIndex.value = districtIndex.value = streetIndex.value = null }
</script>

<style scoped>
/* minor inline form styles */
</style>
