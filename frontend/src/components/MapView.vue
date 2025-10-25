<template>
  <div ref="mapContainer" class="map-container"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import L from 'leaflet'
import { listLibraries, createLibrary } from '../api'

const props = defineProps({ admin: { type: Boolean, default: false } })
const emit = defineEmits(['select-library'])

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
  clickHandler = async function (e) {
    const { lat, lng } = e.latlng
    // build form element
    const form = document.createElement('div')
    const nameInput = document.createElement('input')
    nameInput.placeholder = '名称'
    const collegeInput = document.createElement('input')
    collegeInput.placeholder = '学院'
    const numInput = document.createElement('input')
    numInput.type = 'number'
    numInput.placeholder = '藏书数'
    const saveBtn = document.createElement('button')
    saveBtn.textContent = '保存'
    form.appendChild(document.createElement('div')).appendChild(nameInput)
    form.appendChild(document.createElement('div')).appendChild(collegeInput)
    form.appendChild(document.createElement('div')).appendChild(numInput)
    form.appendChild(document.createElement('div')).appendChild(saveBtn)

    const popup = L.popup().setLatLng(e.latlng).setContent(form).openOn(map)

    saveBtn.addEventListener('click', async () => {
      const name = nameInput.value || '未命名'
      const college = collegeInput.value || ''
      const numberOfBooks = parseInt(numInput.value || '0')
      try {
        const created = await createLibrary({ name, college, numberOfBooks, lon: lng, lat })
        addMarker(created)
        map.closePopup(popup)
      } catch (err) {
        console.error('创建图书馆失败', err)
        alert('创建失败，请查看控制台')
      }
    })
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
</script>

<style scoped>
.map-container { height: 100%; width: 100%; }
</style>
