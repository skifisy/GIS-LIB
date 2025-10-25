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
        <template #default="{ row }">{{ row.lon ?? '-' }}, {{ row.lat ?? '-' }}</template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { listLibraries } from '../api'

const libs = ref([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    libs.value = await listLibraries()
  } catch (e) { console.error(e); libs.value = [] }
  loading.value = false
}

load()

defineExpose({ load })
</script>

<style scoped></style>
