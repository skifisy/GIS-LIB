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
      <el-table-column prop="college" label="学院" />
      <el-table-column prop="numberOfBooks" label="藏书数" width="120" />
      <el-table-column label="坐标" >
        <template #default="{ row }">{{ formatCoord(row.lon) }}, {{ formatCoord(row.lat) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button type="primary" size="mini" @click="onEdit(row)">编辑</el-button>
            <el-button type="danger" size="mini" @click="onDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
    </el-table>

      <el-dialog title="编辑图书馆" :model-value="editVisible" width="520px" @close="() => (editVisible = false)">
        <el-form :model="editForm" label-position="top" size="small">
          <el-form-item label="名称">
            <el-input v-model="editForm.name" />
          </el-form-item>
          <el-form-item label="学院">
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
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { listLibraries, deleteLibrary, updateLibrary } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatCoord } from '../utils/format'

const libs = ref([])
const loading = ref(false)
const editVisible = ref(false)
const editForm = ref({ id: null, name: '', college: '', numberOfBooks: 0, lon: null, lat: null })

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
