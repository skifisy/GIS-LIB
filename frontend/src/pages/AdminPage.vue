<template>
  <div style="height:100vh; display:flex; flex-direction:column;">
    <div style="padding:8px; background:#f5f5f5; display:flex; align-items:center; gap:12px;">
      <button @click="goBack">返回</button>
      <h3 style="margin:0">管理员视图</h3>
    </div>

    <div style="flex:1; display:flex; gap:12px; padding:12px; box-sizing:border-box">
      <el-aside width="200px" style="border-right:1px solid #eee; padding:12px; box-sizing:border-box;">
        <el-menu :default-active="currentView" @select="onMenuSelect" :router="false">
          <el-menu-item index="libraries">馆藏列表</el-menu-item>
          <el-menu-item index="books">图书列表</el-menu-item>
          <el-menu-item index="rents">借阅记录</el-menu-item>
          <el-menu-item index="add">添加馆点</el-menu-item>
        </el-menu>
      </el-aside>

        <!-- 中央区域或右侧添加视图二选一展示 -->
        <div v-if="currentView !== 'add'" style="flex:1; min-width:420px; height:calc(100vh - 80px);">
          <component :is="currentComponent" ref="centerRef" style="height:100%; width:100%" />
        </div>

        <div v-if="currentView === 'add'" style="width:600px; max-width:60%; padding:12px; box-sizing:border-box; overflow:auto; display:flex; flex-direction:column; gap:8px;">
          <!-- 如果是添加视图：右侧上半部为表单，下半部为地图 -->
          <div style="flex:0 0 auto">
            <AddLibraryInline :initialPoint="selectedPoint" @region-change="onRegionChange" @created="onCreated" />
          </div>
          <div style="flex:1; min-height:260px; border:1px solid #eee; border-radius:6px; overflow:hidden">
            <MapView ref="mapRef" :admin="true" style="height:100%" @admin-point="onAdminPoint" @select-library="onSelectLibrary" />
          </div>
        </div>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'
import MapView from '../components/MapView.vue'
import AddLibraryInline from '../components/AddLibraryInline.vue'
import LibrariesTable from '../components/LibrariesTable.vue'
import BooksTable from '../components/BooksTable.vue'
import RentsTable from '../components/RentsTable.vue'
import { ref, computed, onMounted } from 'vue'

export default {
  components: { MapView, AddLibraryInline, LibrariesTable, BooksTable, RentsTable },
  setup() {
    const router = useRouter()
    const mapRef = ref(null)
    const centerRef = ref(null)
    const showAdd = ref(false)
    const selectedPoint = ref(null)
    const currentView = ref('libraries')

    const componentsMap = {
      libraries: LibrariesTable,
      books: BooksTable,
      rents: RentsTable,
    }

    const currentComponent = computed(() => componentsMap[currentView.value] || LibrariesTable)

    function goBack() { router.push('/') }

    function onMenuSelect(key) {
      currentView.value = key
      // give child a moment to mount then call load if available
      setTimeout(() => {
        if (currentView.value !== 'add') {
          if (centerRef.value && centerRef.value.load) centerRef.value.load()
        }
      }, 80)
    }

    function onAdminPoint(p) {
      selectedPoint.value = { lat: p.lat, lng: p.lng }
      showAdd.value = true
    }

    function onRegionChange(coords) {
      if (mapRef.value && mapRef.value.panToPoint) mapRef.value.panToPoint(coords.lat || coords.lon, coords.lon || coords.lng)
    }

    function onCreated(lib) {
      if (mapRef.value && mapRef.value.addMarker) mapRef.value.addMarker(lib)
  // refresh center list if present
  if (centerRef.value && centerRef.value.load) centerRef.value.load()
    }

    function onSelectLibrary(lib) { /* could open details */ }

    onMounted(() => { setTimeout(() => { if (centerRef.value && centerRef.value.load) centerRef.value.load() }, 120) })

    return { goBack, mapRef, centerRef, showAdd, selectedPoint, onAdminPoint, onRegionChange, onCreated, onSelectLibrary, currentView, currentComponent, onMenuSelect }
  }
}
</script>
