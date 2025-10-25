<template>
  <div class="panel">
    <h4>图书馆: {{ library.name }}</h4>
    <div>学院: {{ library.college }}</div>
    <div>藏书数: {{ library.numberOfBooks ?? '-' }}</div>

    <hr />
    <div>
      <button @click="refresh">刷新藏书</button>
      <button @click="close">关闭</button>
    </div>

    <ul class="book-list">
      <li v-for="b in books" :key="b.id">
        <div><strong>{{ b.name }}</strong> — {{ b.author ?? '' }}</div>
        <div>出版: {{ b.publicationTime ?? '-' }}</div>
      <div><button @click="doBorrow(b.id)">借阅</button></div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { listBooks, borrow } from '../api'
const props = defineProps({ library: { type: Object, required: true }, studentId: { type: [Number, String], required: false } })
const emit = defineEmits(['close', 'borrowed'])
const books = ref([])

async function refresh() {
  books.value = await listBooks(props.library.id)
}

watch(() => props.library, () => { refresh() }, { immediate: true })

function close() { emit('close') }

async function doBorrow(bookId) {
  const sid = props.studentId || prompt('请输入学生 ID（模拟登录）')
  if (!sid) return alert('需要学生 ID')
  try {
    const r = await borrow(bookId, Number(sid))
    alert('借阅成功，借书记录 ID: ' + r.id)
    emit('borrowed', r)
  } catch (e) {
    console.error(e)
    alert('借阅失败')
  }
}
</script>

<style scoped>
.panel { position: absolute; right: 12px; top: 64px; width: 320px; max-height: calc(100vh - 100px); overflow:auto; background:white; border:1px solid #ddd; padding:10px; box-shadow:0 2px 8px rgba(0,0,0,0.15) }
.book-list { list-style:none; padding:0 }
.book-list li { padding:6px 0; border-bottom:1px solid #eee }
</style>
