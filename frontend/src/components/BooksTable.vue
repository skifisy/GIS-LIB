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
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button type="primary" size="mini" @click="onEdit(row)">编辑</el-button>
          <el-button type="danger" size="mini" @click="onDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="编辑图书" :model-value="editVisible" width="520px" @close="() => (editVisible = false)">
      <el-form :model="editForm" label-position="top" size="small">
        <el-form-item label="书名">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="editForm.author" />
        </el-form-item>
        <el-form-item label="所在馆">
          <el-select v-model="editForm.libraryId" placeholder="选择馆">
            <el-option v-for="l in libraries" :key="l.id" :label="l.name" :value="l.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="出版时间">
          <el-date-picker v-model="editForm.publicationTime" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="() => (editVisible = false)">取消</el-button>
        <el-button type="primary" @click="onSaveEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { listAllBooks, listBooks, listLibraries, deleteBook, updateBook } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const books = ref([])
const libraries = ref([])
const loading = ref(false)
const filterLibraryId = ref(null)
const editVisible = ref(false)
const editForm = ref({ id: null, name: '', author: '', libraryId: null, publicationTime: '' })
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

function onEdit(row) {
  editForm.value = { id: row.id, name: row.name, author: row.author, libraryId: row.libraryId, publicationTime: (typeof row.publicationTime === 'string' ? row.publicationTime.split('T')[0] : '') }
  editVisible.value = true
}

async function onSaveEdit() {
  if (!editForm.value.name) return ElMessage({ message: '请输入书名', type: 'warning' })
  try {
    const payload = { name: editForm.value.name, author: editForm.value.author || null, libraryId: editForm.value.libraryId }
    if (editForm.value.publicationTime) payload.publicationTime = editForm.value.publicationTime + 'T00:00:00'
    const updated = await updateBook(editForm.value.id, payload)
    books.value = books.value.map(x => x.id === updated.id ? updated : x)
    ElMessage({ message: '更新成功', type: 'success' })
    editVisible.value = false
  } catch (e) {
    console.error('更新失败', e)
    ElMessage({ message: '更新失败', type: 'error' })
  }
}

loadLibraries()
load()

defineExpose({ load })
</script>

<style scoped></style>
