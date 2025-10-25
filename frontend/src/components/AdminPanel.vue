<template>
  <div :class="['admin-panel', { collapsed }]">
    <button class="collapse-toggle" @click="collapsed = !collapsed">{{ collapsed ? '展开' : '收起' }}</button>

    <!-- 管理数据总览已移除：此处保留最小占位，若需恢复可将展示逻辑放回此处 -->
  </div>
</template>

<script setup>
import { ref } from 'vue'
import api, { listLibraries, listBooks, listRents, listAllBooks, listAllRents } from '../api'

const emit = defineEmits(['close'])
const libraries = ref([])
const books = ref([])
const rents = ref([])

const filterLibraryId = ref(null)
const filterStudentId = ref('')
const collapsed = ref(false)

async function loadLibraries() {
  try {
    libraries.value = await listLibraries()
  } catch (e) { console.error('加载图书馆失败', e); alert('加载图书馆失败') }
}

async function loadBooks() {
  try {
    // if filterLibraryId is null/undefined, pass nothing to get all
    const libId = filterLibraryId.value
    if (libId === null || libId === '') {
      books.value = await listAllBooks()
    } else {
      books.value = await listBooks(Number(libId))
    }
  } catch (e) { console.error('加载图书失败', e); alert('加载图书失败') }
}

async function loadRents() {
  try {
    const sid = filterStudentId.value
    if (!sid) {
      rents.value = await listAllRents()
    } else rents.value = await listRents(Number(sid))
  } catch (e) { console.error('加载借阅记录失败', e); alert('加载借阅记录失败') }
}

// load initial data
loadLibraries()
loadBooks()
loadRents()

// expose loader so parent pages can request refresh
import { defineExpose } from 'vue'
defineExpose({ loadLibraries })
</script>

<style scoped>
.admin-panel { position: fixed; left: 50%; top: 80px; transform: translateX(-50%); width: 92%; max-width: 1100px; max-height: calc(100vh - 120px); overflow:auto; background:white; border:1px solid #ddd; padding:12px; box-shadow:0 6px 18px rgba(0,0,0,0.12); z-index:10001 }
.admin-panel.collapsed { width:56px; height:56px; padding:6px; display:flex; align-items:center; justify-content:center }
.collapse-toggle { position:absolute; right:8px; top:8px; background:#f5f5f5; border:1px solid #ddd; padding:4px 6px; cursor:pointer }
.card { border:1px solid #f0f0f0; background:#fff; padding:8px; width:32%; box-sizing:border-box }
.card-header { font-weight:600; margin-bottom:6px }
.card-body { max-height:480px; overflow:auto }
.mini-list { list-style:none; padding:0; margin:0 }
.mini-list li { padding:6px 4px; border-bottom:1px solid #f3f3f3; font-size:13px }
div[style*="display:flex; gap:12px"] { display:flex }

@media(max-width:900px) {
  .card { width:100% }
  div[style*="display:flex; gap:12px"] { flex-direction:column }
}
</style>
