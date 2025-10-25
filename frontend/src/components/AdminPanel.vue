<template>
  <div :class="['admin-panel', { collapsed }]">
    <button class="collapse-toggle" @click="collapsed = !collapsed">{{ collapsed ? '展开' : '收起' }}</button>

    <template v-if="!collapsed">
      <h4>管理员视图 — 数据总览</h4>
      <div style="display:flex; gap:12px; align-items:flex-start">
        <div class="card">
          <div class="card-header">图书馆 ({{ libraries.length }})</div>
          <div class="card-body">
            <button @click="loadLibraries">刷新</button>
            <ul class="mini-list">
              <li v-for="l in libraries" :key="l.id">#{{ l.id }} {{ l.name }} — {{ l.college }} (藏书: {{ l.numberOfBooks ?? '-' }})</li>
            </ul>
          </div>
        </div>

        <div class="card">
          <div class="card-header">图书 ({{ books.length }})</div>
          <div class="card-body">
            <div style="display:flex; gap:8px; align-items:center; margin-bottom:6px">
              <label style="font-size:12px">按图书馆筛选：</label>
              <select v-model="filterLibraryId">
                <option :value="null">全部</option>
                <option v-for="l in libraries" :key="l.id" :value="l.id">{{ l.name }}</option>
              </select>
              <button @click="loadBooks">刷新</button>
            </div>
            <ul class="mini-list">
              <li v-for="b in books" :key="b.id">#{{ b.id }} <strong>{{ b.name }}</strong> — {{ b.author ?? '-' }} (馆: {{ b.libraryId }})</li>
            </ul>
          </div>
        </div>

        <div class="card">
          <div class="card-header">借阅记录 ({{ rents.length }})</div>
          <div class="card-body">
            <div style="display:flex; gap:8px; align-items:center; margin-bottom:6px">
              <input v-model="filterStudentId" placeholder="按学生ID筛选" style="width:120px" />
              <button @click="loadRents">刷新</button>
            </div>
            <ul class="mini-list">
              <li v-for="r in rents" :key="r.id">#{{ r.id }} 书:{{ r.bookId }} 馆:{{ r.libraryId }} 学生:{{ r.studentId }} 返回:{{ r.returnTime ?? '-' }}</li>
            </ul>
          </div>
        </div>
      </div>
      <div style="margin-top:8px; text-align:right"><button @click="$emit('close')">关闭管理员视图</button></div>
    </template>
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
