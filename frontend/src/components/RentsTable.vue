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
import { listAllRents, listAllBooks, deleteRent } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const rents = ref([])
const books = ref([])
const loading = ref(false)

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

load()

defineExpose({ load })
</script>

<style scoped></style>
