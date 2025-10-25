<template>
  <div ref="mapContainer" style="height:100%; width:100%"></div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import L from 'leaflet'
import { listLibraries, createLibrary } from '../api'

const props = defineProps({ admin: { type: Boolean, default: false } })
const mapContainer = ref(null)
let map = null
let markersLayer = null
let clickHandler = null
const emit = defineEmits(['select-library'])

function addMarker(lib) {
  const marker = L.marker([lib.lat, lib.lon])
  const popupContent = `
    <div>
      <strong>${lib.name}</strong><br/>
      学院: ${lib.college}<br/>
      藏书: ${lib.numberOfBooks ?? '-'}<br/>
      <button id="view-books-${lib.id}">查看藏书</button>
    </div>`
  marker.bindPopup(popupContent)
  marker.on('popupopen', () => {
    const btn = document.getElementById(`view-books-${lib.id}`)
    if (btn) btn.onclick = () => emit('select-library', lib)
  })
  marker.on('click', () => emit('select-library', lib))
  markersLayer.addLayer(marker)
}

async function loadLibraries() {
  try {
    const libs = await listLibraries()
    markersLayer.clearLayers()
    libs.forEach(addMarker)
  } catch (e) {
    console.error('加载图书馆失败', e)
  }
}

function enableAdminAdd() {
  if (!map) return
  clickHandler = async function (e) {
    const { lat, lng } = e.latlng
    // create a small popup with a form
    const formId = 'add-lib-form-' + Date.now()
    const html = `
      <div>
        <div><label>名称: <input id="${formId}-name"/></label></div>
        <div><label>学院: <input id="${formId}-college"/></label></div>
        <div><label>藏书数: <input id="${formId}-num" type="number"/></label></div>
        <div style="margin-top:6px"><button id="${formId}-ok">保存</button></div>
      </div>`
    const popup = L.popup()
      .setLatLng(e.latlng)
      .setContent(html)
      .openOn(map)

    // wait a tick then attach handler
    setTimeout(() => {
      const btn = document.getElementById(`${formId}-ok`)
      if (!btn) return
      btn.addEventListener('click', async () => {
        const name = document.getElementById(`${formId}-name`).value || '未命名'
        const college = document.getElementById(`${formId}-college`).value || ''
        const numberOfBooks = parseInt(document.getElementById(`${formId}-num`).value || '0')
        try {
          const created = await createLibrary({ name, college, numberOfBooks, lon: lng, lat })
          // add marker
          addMarker(created)
          map.closePopup(popup)
        } catch (err) {
          console.error('创建图书馆失败', err)
          alert('创建失败，请查看控制台')
        }
      })
    }, 50)
  }
  map.on('click', clickHandler)
}

function disableAdminAdd() {
  if (map && clickHandler) {
    map.off('click', clickHandler)
    clickHandler = null
  }
}

onMounted(() => {
  // Chengdu center as default
  const center = [30.67, 104.07]
  map = L.map(mapContainer.value).setView(center, 12)
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; OpenStreetMap contributors'
  }).addTo(map)

  markersLayer = L.layerGroup().addTo(map)
  loadLibraries()

  if (props.admin) enableAdminAdd()
})

watch(() => props.admin, (v) => {
  if (v) enableAdminAdd()
  else disableAdminAdd()
})
</script>

<style scoped>
/* ensure container stretches */
:host, div[ref="mapContainer"] {
  height: 100%;
}
</style>
