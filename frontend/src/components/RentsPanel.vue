<template>
  <div :class="['panel', { collapsed }]">
    <button class="collapse-toggle" @click="collapsed = !collapsed" :aria-expanded="!collapsed">{{ collapsed ? '展开' : '收起' }}</button>

    <template v-if="!collapsed">
      <h4>借阅记录查询</h4>
      <div>
        学号/学生 ID: <input v-model="sid" />
        <button @click="query">查询</button>
      </div>

      <ul>
        <li v-for="r in rents" :key="r.id">
          <div>借阅 ID: {{ r.id }} 图书ID: {{ r.bookId }} 图书馆: {{ r.libraryId }}</div>
          <div>借出时间: {{ r.rentTime }} 返回时间: {{ r.returnTime ?? '-' }}</div>
          <div v-if="!r.returnTime"><button @click="doReturn(r.id)">归还</button></div>
        </li>
      </ul>
      <div><button @click="$emit('close')">关闭</button></div>
    </template>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { listRents, returnBook } from '../api'
const rents = ref([])
const sid = ref('')
const emit = defineEmits(['close'])

// collapsed state for better map visibility
const collapsed = ref(false)

async function query() {
  if (!sid.value) return alert('请输入学生 ID')
  rents.value = await listRents(Number(sid.value))
}

async function doReturn(rentId) {
  if (!sid.value) return alert('请输入学生 ID')
  try {
    const res = await returnBook(rentId, Number(sid.value))
    alert('归还成功')
    await query()
  } catch (e) { console.error(e); alert('归还失败') }
}
</script>

<style scoped>
.panel { position: fixed; left: 12px; top: 64px; width: 420px; max-height: calc(100vh - 100px); overflow:auto; background:white; border:1px solid #ddd; padding:10px; box-shadow:0 2px 8px rgba(0,0,0,0.15); z-index:10000 }
.panel.collapsed { width:56px; height:56px; padding:6px; display:flex; align-items:center; justify-content:center }
.collapse-toggle { position:absolute; right:8px; top:8px; background:#f5f5f5; border:1px solid #ddd; padding:4px 6px; cursor:pointer }
</style>
