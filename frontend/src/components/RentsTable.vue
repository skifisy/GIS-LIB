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
      <el-table-column prop="bookId" label="书ID" width="100" />
      <el-table-column prop="studentId" label="学生ID" width="120" />
      <el-table-column prop="borrowTime" label="借出时间" width="200" />
      <el-table-column prop="returnTime" label="归还时间" width="200" />
    </el-table>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { listAllRents } from '../api'

const rents = ref([])
const loading = ref(false)

async function load() {
  loading.value = true
  try { rents.value = await listAllRents() } catch (e) { console.error(e); rents.value = [] }
  loading.value = false
}

load()

defineExpose({ load })
</script>

<style scoped></style>
