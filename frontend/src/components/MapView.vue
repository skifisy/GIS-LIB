<template>
  <div ref="mapContainer" class="map-container"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, defineExpose } from 'vue'
import L from 'leaflet'
import { listLibraries } from '../api'

const props = defineProps({ admin: { type: Boolean, default: false } })
const emit = defineEmits(['select-library', 'admin-point'])

const mapContainer = ref(null)
const mapRef = ref(null)
const markersLayerRef = ref(null)
let clickHandler = null

function createMarkerElement(lib) {
  const container = document.createElement('div')
  container.innerHTML = `
    <div>
      <strong>${lib.name}</strong><br/>
      学院: ${lib.college}<br/>
      藏书: ${lib.numberOfBooks ?? '-'}<br/>
      <button class="view-books-btn">查看藏书</button>
    </div>`
  const btn = container.querySelector('.view-books-btn')
  if (btn) btn.addEventListener('click', (e) => {
    e.stopPropagation()
    emit('select-library', lib)
  })
  return container
}

function addMarker(lib) {
  if (!markersLayerRef.value) return
  // Leaflet expects [lat, lon]
  const marker = L.marker([lib.lat, lib.lon])
  const el = createMarkerElement(lib)
  marker.bindPopup(el)
  marker.on('click', () => emit('select-library', lib))
  markersLayerRef.value.addLayer(marker)
}

async function loadLibraries() {
  try {
    const libs = await listLibraries()
    if (!markersLayerRef.value) return
    markersLayerRef.value.clearLayers()
    libs.forEach(addMarker)
  } catch (e) {
    console.error('加载图书馆失败', e)
  }
}

function enableAdminAdd() {
  const map = mapRef.value
  if (!map || clickHandler) return
  clickHandler = function (e) {
    const { lat, lng } = e.latlng
    // emit admin-point so parent (AdminPage) will show a Vue form/modal
    // parent can then call back to MapView to pan/add marker
    emit('admin-point', { lat, lng })
  }
  map.on('click', clickHandler)
}

function disableAdminAdd() {
  const map = mapRef.value
  if (!map || !clickHandler) return
  map.off('click', clickHandler)
  clickHandler = null
}

onMounted(() => {
  const center = [30.67, 104.07]
  const map = L.map(mapContainer.value).setView(center, 12)
  mapRef.value = map
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; OpenStreetMap contributors'
  }).addTo(map)

  markersLayerRef.value = L.layerGroup().addTo(map)
  loadLibraries()
  if (props.admin) enableAdminAdd()
  // ensure map renders correctly when container size changes
  setTimeout(() => map.invalidateSize(), 200)
})

onBeforeUnmount(() => {
  disableAdminAdd()
  if (mapRef.value) {
    mapRef.value.remove()
    mapRef.value = null
  }
})

watch(() => props.admin, (v) => {
  if (v) enableAdminAdd()
  else disableAdminAdd()
})

// expose methods to parent
function panToPoint(lat, lng, zoom = 15) {
  if (!mapRef.value) return
  mapRef.value.setView([lat, lng], zoom)
}

defineExpose({ panToPoint, addMarker })
</script>

<style scoped>
.map-container { height: 100%; width: 100%; }
</style>
