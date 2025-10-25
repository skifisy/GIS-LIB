<template>
  <div>
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px">
      <h4>借阅记录</h4>
      <div>
        <el-button size="small" @click="load">刷新</el-button>
      </div>
    </div>

    <el-table :data="rents" style="width:100%" v-loading="loading">
      <el-table-column prop="id" label="#" width="80" />
      <el-table-column label="书名" width="220">
        <template #default="{ row }">
          {{ findBookName(row.bookId) }}
        </template>
      </el-table-column>
      <el-table-column prop="studentId" label="学生ID" width="120" />
      <el-table-column label="借出时间" width="140">
        <template #default="{ row }">
          {{ formatDate(row.rentTime) }}
        </template>
      </el-table-column>
      <el-table-column label="归还时间" width="140">
        <template #default="{ row }">
          {{ formatDate(row.returnTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button type="primary" size="mini" @click="onEdit(row)">编辑</el-button>
          <el-button type="danger" size="mini" @click="onDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

      <el-dialog title="编辑借阅记录" :model-value="editVisible" width="600px" @close="() => (editVisible = false)">
        <el-form :model="editForm" label-position="top" size="small">
          <el-form-item label="书">
            <el-select v-model="editForm.bookId" placeholder="选择书">
              <el-option v-for="b in books" :key="b.id" :label="b.name" :value="b.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="学生ID">
            <el-input-number v-model="editForm.studentId" :min="0" />
          </el-form-item>
          <el-form-item label="借出时间">
            <el-date-picker v-model="editForm.rentTime" type="date" value-format="YYYY-MM-DD" style="width:100%" />
          </el-form-item>
          <el-form-item label="归还时间">
            <el-date-picker v-model="editForm.returnTime" type="date" value-format="YYYY-MM-DD" style="width:100%" />
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
import { listAllRents, listAllBooks, deleteRent, updateRent } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const rents = ref([])
const books = ref([])
const loading = ref(false)
const editVisible = ref(false)
const editForm = ref({ id: null, bookId: null, studentId: null, rentTime: '', returnTime: '' })

function findBookName(id) {
  const b = books.value.find(x => x.id === id)
  return b ? b.name : (id || '-')
}

function formatDate(v) {
  if (!v) return '-'
  if (typeof v === 'string') return v.split('T')[0]
  return v
}

async function load() {
  loading.value = true
  try {
    rents.value = await listAllRents()
    books.value = await listAllBooks()
  } catch (e) { console.error(e); rents.value = []; books.value = [] }
  loading.value = false
}

async function onDelete(id) {
  const ok = await ElMessageBox.confirm('确认删除该借阅记录吗？此操作不可恢复。', '删除确认', { type: 'warning' }).catch(() => false)
  if (!ok) return
  try {
    await deleteRent(id)
    rents.value = rents.value.filter(x => x.id !== id)
    ElMessage({ message: '删除成功', type: 'success' })
  } catch (e) {
    console.error('删除借阅记录失败', e)
    ElMessage({ message: '删除失败', type: 'error' })
  }
}

function onEdit(row) {
  editForm.value = {
    id: row.id,
    bookId: row.bookId,
    studentId: row.studentId,
    rentTime: (typeof row.rentTime === 'string' ? row.rentTime.split('T')[0] : ''),
    returnTime: (typeof row.returnTime === 'string' ? row.returnTime.split('T')[0] : '')
  }
  editVisible.value = true
}

async function onSaveEdit() {
  if (!editForm.value.bookId) return ElMessage({ message: '请选择书籍', type: 'warning' })
  try {
    const payload = { bookId: editForm.value.bookId, studentId: editForm.value.studentId }
    if (editForm.value.rentTime) payload.rentTime = editForm.value.rentTime + 'T00:00:00'
    if (editForm.value.returnTime) payload.returnTime = editForm.value.returnTime + 'T00:00:00'
    const updated = await updateRent(editForm.value.id, payload)
    rents.value = rents.value.map(x => x.id === updated.id ? updated : x)
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
