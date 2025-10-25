<template>
  <div style="height:100vh; display:flex; flex-direction:column;">
    <div style="padding:8px; background:#f5f5f5; display:flex; align-items:center; gap:12px;">
      <h3 style="margin:0">GIS 图书馆共享系统（原型）</h3>
      <div style="margin-left:12px">学生 ID（模拟登录）: <input v-model="studentId" style="width:120px" /></div>
      <div style="margin-left:auto; display:flex; gap:8px; align-items:center;">
        <button @click="showRents = true">我的借阅</button>
        <button @click="goAdmin">管理员视图</button>
      </div>
    </div>
    <MapView :admin="false" style="flex:1" @select-library="openLibrary" />

    <BookPanel v-if="selectedLibrary" :library="selectedLibrary" :studentId="studentId" @close="selectedLibrary=null" @borrowed="onBorrowed" />
    <RentsPanel v-if="showRents" @close="showRents=false" />
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import MapView from '../components/MapView.vue'
import BookPanel from '../components/BookPanel.vue'
import RentsPanel from '../components/RentsPanel.vue'

export default {
  components: { MapView, BookPanel, RentsPanel },
  setup() {
    const router = useRouter()
    const studentId = ref('')
    const selectedLibrary = ref(null)
    const showRents = ref(false)
    function goAdmin() { router.push('/admin') }
    function openLibrary(lib) { selectedLibrary.value = lib }
    function onBorrowed(r) { /* could refresh rents or UI */ }
    return { studentId, selectedLibrary, showRents, goAdmin, openLibrary, onBorrowed }
  }
}
</script>
