<template>
  <div>
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px">
      <h4>图书列表</h4>
      <div>
        <el-select v-model="filterLibraryId" placeholder="按图书馆筛选" style="width:220px" clearable>
          <el-option v-for="l in libraries" :key="l.id" :label="l.name" :value="l.id" />
        </el-select>
        <el-button size="small" @click="load">刷新</el-button>
      </div>
    </div>
    <el-table :data="books" style="width:100%" v-loading="loading">
      <el-table-column prop="id" label="#" width="80" />
      <el-table-column prop="name" label="书名" />
      <el-table-column prop="author" label="作者" width="160" />
      <el-table-column label="所在馆" width="180">
        <template #default="{ row }">
          {{ findLibraryName(row.libraryId) }}
        </template>
      </el-table-column>
      <el-table-column label="出版时间" width="160">
        <template #default="{ row }">
          {{ formatDate(row.publicationTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="danger" size="mini" @click="onDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { listAllBooks, listBooks, listLibraries, deleteBook } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const books = ref([])
const libraries = ref([])
const loading = ref(false)
const filterLibraryId = ref(null)
function findLibraryName(id) {
  const l = libraries.value.find(x => x.id === id)
  return l ? l.name : (id || '-')
}
function formatDate(v) {
  if (!v) return '-'
  if (typeof v === 'string') {
    return v.split('T')[0]
  }
  return v
}

async function loadLibraries() {
  try { libraries.value = await listLibraries() } catch (e) { libraries.value = [] }
}

async function load() {
  loading.value = true
  try {
    if (filterLibraryId.value) books.value = await listBooks(Number(filterLibraryId.value))
    else books.value = await listAllBooks()
  } catch (e) { console.error(e); books.value = [] }
  loading.value = false
}

async function onDelete(id) {
  const ok = await ElMessageBox.confirm('确认删除该书吗？此操作不可恢复。', '删除确认', { type: 'warning' }).catch(() => false)
  if (!ok) return
  try {
    await deleteBook(id)
    // remove from list
    books.value = books.value.filter(x => x.id !== id)
    ElMessage({ message: '删除成功', type: 'success' })
  } catch (e) {
    console.error('删除书籍失败', e)
    ElMessage({ message: '删除失败', type: 'error' })
  }
}

loadLibraries()
load()

defineExpose({ load })
</script>

<style scoped></style>
