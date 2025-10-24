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
<template>
  <div ref="mapEl" style="height:100%; width:100%; position:relative"></div>
</template>

<script>
import L from 'leaflet'
import { onMounted, ref, watch } from 'vue'
import { listLibraries, createLibrary } from '../api'

export default {
  props: { admin: { type: Boolean, default: false } },
  setup(props) {
    const mapEl = ref(null)
    let map = null
    let markersLayer = null

    async function loadLibraries() {
      const libs = await listLibraries()
      markersLayer.clearLayers()
      libs.forEach(l => {
        const marker = L.marker([l.lat, l.lon]).bindPopup(`<b>${l.name}</b><br/>${l.college}<br/>藏书: ${l.numberOfBooks}`)
        markersLayer.addLayer(marker)
      })
    }

    onMounted(() => {
      map = L.map(mapEl.value).setView([30.67, 104.06], 13) // 成都中心
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors'
      }).addTo(map)
      markersLayer = L.layerGroup().addTo(map)
      loadLibraries()

      map.on('click', async (e) => {
        if (!props.admin) return
        const lon = e.latlng.lng
        const lat = e.latlng.lat
        const name = prompt('图书馆名称')
        if (!name) return
        const college = prompt('所属学院/学校', '') || ''
        const number_of_books = parseInt(prompt('藏书数量', '0') || '0')
        try {
          await createLibrary({ name, college, numberOfBooks: number_of_books, lon, lat })
          alert('已添加')
          loadLibraries()
        } catch (err) {
          console.error(err)
          alert('添加失败')
        }
      })
    })

    watch(() => props.admin, (nv) => {
      // 简单反馈
      if (nv) map.getContainer().style.cursor = 'crosshair'
      else map.getContainer().style.cursor = ''
    })

    return { mapEl }
  }
}
</script>
