<template>
  <div style="height:100vh; display:flex; flex-direction:column;">
    <!-- 上半部分：筛选区（使用 Element Plus 组件） -->
    <div style="height:36%; padding:12px; background:#fafafa; box-shadow:0 1px 4px rgba(0,0,0,0.04);">
      <div style="display:flex; align-items:flex-start; gap:12px">
        <h3 style="margin:0">GIS 图书馆共享系统（原型）</h3>
        <div style="margin-left:12px">学生 ID（模拟登录）: <el-input v-model="studentId" style="width:140px" size="small" /></div>
        <div style="margin-left:auto; display:flex; gap:8px; align-items:center;">
          <el-button @click="showRents = true" size="small">我的借阅</el-button>
          <el-button @click="goAdmin" size="small">管理员视图</el-button>
        </div>
      </div>

      <div style="margin-top:12px; display:flex; gap:12px; align-items:center;">
        <div>
          <div style="font-size:12px; color:#666; margin-bottom:6px">筛选类型</div>
          <el-radio-group v-model="filterType">
            <el-radio-button label="all">全部</el-radio-button>
            <el-radio-button label="college">按学院</el-radio-button>
            <el-radio-button label="name">按名称</el-radio-button>
          </el-radio-group>
        </div>

        <div v-if="filterType === 'college'">
          <div style="font-size:12px; color:#666; margin-bottom:6px">选择学院</div>
          <el-select v-model="selectedCollege" placeholder="请选择学院" style="min-width:220px" clearable>
            <el-option v-for="c in colleges" :key="c" :label="c" :value="c" />
          </el-select>
        </div>

        <div v-if="filterType === 'name'">
          <div style="font-size:12px; color:#666; margin-bottom:6px">图书馆名称</div>
          <el-input v-model="nameSearch" placeholder="输入名称关键字" style="min-width:220px" clearable />
        </div>

        <el-button type="primary" @click="applyFilter">筛选</el-button>
        <el-button @click="resetFilter">重置</el-button>
      </div>

      <div style="margin-top:12px; height:calc(100% - 110px); overflow:auto">
        <el-table :data="filteredLibraries" stripe size="small" style="width:100%" @row-click="onRowClick">
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="college" label="学院" />
          <el-table-column prop="numberOfBooks" label="藏书" width="90" />
        </el-table>
      </div>
    </div>

    <!-- 下半部分：地图 -->
    <div style="flex:1;">
      <MapView ref="mapRef" :admin="false" style="height:100%" @select-library="openLibrary" />
    </div>

    <BookPanel v-if="selectedLibrary" :library="selectedLibrary" :studentId="studentId" @close="selectedLibrary=null" @borrowed="onBorrowed" />
    <RentsPanel v-if="showRents" @close="showRents=false" />
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import MapView from '../components/MapView.vue'
import BookPanel from '../components/BookPanel.vue'
import RentsPanel from '../components/RentsPanel.vue'
import { listLibraries } from '../api'

export default {
  components: { MapView, BookPanel, RentsPanel },
  setup() {
    const router = useRouter()
    const studentId = ref('')
    const selectedLibrary = ref(null)
    const showRents = ref(false)
    const mapRef = ref(null)

  // filter state
  const filterType = ref('all')
  const selectedCollege = ref(null)
  const nameSearch = ref('')
  const libraries = ref([])
  const colleges = ref([])

    async function loadAll() {
      libraries.value = await listLibraries()
      const set = new Set(libraries.value.map(l => l.college).filter(Boolean))
      colleges.value = Array.from(set)
    }

    onMounted(() => { loadAll() })

    const filteredLibraries = computed(() => {
      if (filterType.value === 'all') return libraries.value
      if (filterType.value === 'college' && selectedCollege.value) return libraries.value.filter(l => l.college === selectedCollege.value)
      if (filterType.value === 'name' && nameSearch.value) {
        const q = nameSearch.value.trim().toLowerCase()
        return libraries.value.filter(l => (l.name || '').toLowerCase().includes(q))
      }
      return libraries.value
    })

    function applyFilter() {
      // filteredLibraries is reactive; no-op for now
    }

    function resetFilter() {
      filterType.value = 'all'
      selectedCollege.value = null
      nameSearch.value = ''
    }

    function goAdmin() { router.push('/admin') }
    function openLibrary(lib) {
      selectedLibrary.value = lib
      if (mapRef.value && mapRef.value.panToPoint) {
        mapRef.value.panToPoint(lib.lat, lib.lon)
      }
    }
    function onBorrowed(r) { /* could refresh rents or UI */ }

    function onRowClick(row) { openLibrary(row) }

    return { studentId, selectedLibrary, showRents, goAdmin, openLibrary, onBorrowed, mapRef,
      filterType, selectedCollege, nameSearch, colleges, libraries, filteredLibraries, applyFilter, resetFilter, onRowClick }
  }
}
</script>
