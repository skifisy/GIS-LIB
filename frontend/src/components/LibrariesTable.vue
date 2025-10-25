<template>
  <div>
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px">
      <h4>图书馆列表</h4>
      <div>
        <el-button size="small" @click="load">刷新</el-button>
      </div>
    </div>
    <el-table :data="libs" style="width:100%" v-loading="loading">
      <el-table-column prop="id" label="#" width="80" />
      <el-table-column prop="name" label="名称" />
  <el-table-column prop="college" label="大学" />
      <el-table-column prop="numberOfBooks" label="藏书数" width="120" />
      <el-table-column label="坐标" >
        <template #default="{ row }">{{ formatCoord(row.lon) }}, {{ formatCoord(row.lat) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="270">
            <template #default="{ row }">
              <el-button type="primary" size="mini" @click="onEdit(row)">编辑</el-button>
              <el-button type="success" size="mini" @click="onOpenAddBook(row)">添加图书</el-button>
              <el-button type="danger" size="mini" @click="onDelete(row.id)">删除</el-button>
            </template>
          </el-table-column>
    </el-table>

      <el-dialog title="编辑图书馆" :model-value="editVisible" width="520px" @close="() => (editVisible = false)">
        <el-form :model="editForm" label-position="top" size="small">
          <el-form-item label="名称">
            <el-input v-model="editForm.name" />
          </el-form-item>
          <el-form-item label="大学">
            <el-input v-model="editForm.college" />
          </el-form-item>
          <el-form-item label="藏书数">
            <el-input-number v-model="editForm.numberOfBooks" :min="0" />
          </el-form-item>
          <el-form-item label="经度">
            <el-input v-model.number="editForm.lon" />
          </el-form-item>
          <el-form-item label="纬度">
            <el-input v-model.number="editForm.lat" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="() => (editVisible = false)">取消</el-button>
          <el-button type="primary" @click="onSaveEdit">保存</el-button>
        </template>
      </el-dialog>

      <el-dialog title="为图书馆添加图书" :model-value="addVisible" width="520px" @close="() => (addVisible = false)">
        <el-form :model="addForm" label-position="top" size="small">
          <el-form-item label="书名">
            <el-input v-model="addForm.name" />
          </el-form-item>
          <el-form-item label="作者">
            <el-input v-model="addForm.author" />
          </el-form-item>
          <el-form-item label="出版日期">
            <el-date-picker v-model="addForm.publicationTime" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width:100%" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="() => (addVisible = false)">取消</el-button>
          <el-button type="primary" @click="onSaveAddBook">添加</el-button>
        </template>
      </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { listLibraries, deleteLibrary, updateLibrary, createBook } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatCoord } from '../utils/format'

const libs = ref([])
const loading = ref(false)
const editVisible = ref(false)
const editForm = ref({ id: null, name: '', college: '', numberOfBooks: 0, lon: null, lat: null })
// add book dialog state
const addVisible = ref(false)
const addForm = ref({ libraryId: null, name: '', author: '', publicationTime: '' })

function onOpenAddBook(row) {
  addForm.value = { libraryId: row.id, name: '', author: '', publicationTime: '' }
  addVisible.value = true
}

async function onSaveAddBook() {
  if (!addForm.value.name) return ElMessage({ message: '请输入书名', type: 'warning' })
  const payload = { libraryId: addForm.value.libraryId, name: addForm.value.name, author: addForm.value.author || null }
  if (addForm.value.publicationTime) payload.publicationTime = addForm.value.publicationTime + 'T00:00:00'
  try {
    const created = await createBook(payload)
    ElMessage({ message: '添加成功，ID: ' + created.id, type: 'success' })
    addVisible.value = false
    // optionally update library's numberOfBooks in list
    libs.value = libs.value.map(x => x.id === created.libraryId ? { ...x, numberOfBooks: (x.numberOfBooks||0) + 1 } : x)
  } catch (e) {
    console.error('添加图书失败', e)
    ElMessage({ message: '添加图书失败', type: 'error' })
  }
}

async function load() {
  loading.value = true
  try {
    libs.value = await listLibraries()
  } catch (e) { console.error(e); libs.value = [] }
  loading.value = false
}

async function onDelete(id) {
  const ok = await ElMessageBox.confirm('确认删除该图书馆吗？此操作不可恢复。', '删除确认', { type: 'warning' }).catch(() => false)
  if (!ok) return
  try {
    await deleteLibrary(id)
    libs.value = libs.value.filter(x => x.id !== id)
    ElMessage({ message: '删除成功', type: 'success' })
  } catch (e) {
    console.error('删除失败', e)
    ElMessage({ message: '删除失败', type: 'error' })
  }
}

function onEdit(row) {
  editForm.value = { id: row.id, name: row.name, college: row.college, numberOfBooks: row.numberOfBooks, lon: row.lon, lat: row.lat }
  editVisible.value = true
}

async function onSaveEdit() {
  if (!editForm.value.name) return ElMessage({ message: '请输入名称', type: 'warning' })
  try {
    const payload = { name: editForm.value.name, college: editForm.value.college, numberOfBooks: editForm.value.numberOfBooks, lon: Number(editForm.value.lon), lat: Number(editForm.value.lat) }
    const updated = await updateLibrary(editForm.value.id, payload)
    // replace in list
    libs.value = libs.value.map(x => x.id === updated.id ? updated : x)
    ElMessage({ message: '更新成功', type: 'success' })
    editVisible.value = false
  } catch (e) {
    console.error('更新失败', e)
    ElMessage({ message: '更新失败', type: 'error' })
  }
}

load()

defineExpose({ load })
</script>

<style scoped></style>
