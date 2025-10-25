<template>
  <div style="height:100vh; display:flex; flex-direction:column;">
    <div style="padding:8px; background:#f5f5f5; display:flex; align-items:center; gap:12px;">
      <h3 style="margin:0">GIS 图书馆共享系统（原型）</h3>
      <div style="margin-left:12px">学生 ID（模拟登录）: <input v-model="studentId" style="width:120px" /></div>
      <div style="margin-left:auto; display:flex; gap:8px; align-items:center;">
        <button @click="showRents = true">我的借阅</button>
        <button @click="toggleAdmin">{{ adminMode ? '切换到用户模式' : '切换到管理员模式' }}</button>
      </div>
    </div>
    <MapView :admin="adminMode" style="flex:1" @select-library="openLibrary" />

    <BookPanel v-if="selectedLibrary" :library="selectedLibrary" :studentId="studentId" @close="selectedLibrary=null" @borrowed="onBorrowed" />
    <RentsPanel v-if="showRents" @close="showRents=false" />

    <!-- admin panel overlay -->
    <AdminPanel v-if="adminMode" @close="toggleAdmin" />
  </div>
</template>

<script>
import { ref } from 'vue'
import MapView from './components/MapView.vue'
import BookPanel from './components/BookPanel.vue'
import RentsPanel from './components/RentsPanel.vue'
import AdminPanel from './components/AdminPanel.vue'

export default {
  components: { MapView, BookPanel, RentsPanel, AdminPanel },
  setup() {
  const adminMode = ref(false)
    const studentId = ref('')
    const selectedLibrary = ref(null)
    const showRents = ref(false)
    function toggleAdmin() { adminMode.value = !adminMode.value }
    function openLibrary(lib) { selectedLibrary.value = lib }
    function onBorrowed(r) { /* could refresh rents or UI */ }
    return { adminMode, toggleAdmin, studentId, selectedLibrary, openLibrary, showRents, onBorrowed }
  }
}
</script>

