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
      <el-table-column prop="libraryId" label="馆ID" width="100" />
      <el-table-column prop="publicationTime" label="出版时间" width="200" />
    </el-table>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { listAllBooks, listBooks, listLibraries } from '../api'

const books = ref([])
const libraries = ref([])
const loading = ref(false)
const filterLibraryId = ref(null)

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

loadLibraries()
load()

defineExpose({ load })
</script>

<style scoped></style>
