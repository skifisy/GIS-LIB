<template>
  <div :class="['panel', { collapsed }]">
    <button class="collapse-toggle" @click="collapsed = !collapsed" :aria-expanded="!collapsed">
      {{ collapsed ? '展开' : '收起' }}
    </button>

    <template v-if="!collapsed">
      <h4>图书馆: {{ library.name }}</h4>
      <div>学院: {{ library.college }}</div>
      <div>藏书数: {{ library.numberOfBooks ?? '-' }}</div>

      <hr />
      <div>
        <button @click="refresh">刷新藏书</button>
        <button @click="close">关闭</button>
      </div>

      <!-- add book form -->
      <div style="margin-top:8px; padding:8px; border:1px dashed #eee; background:#fafafa">
        <h5 style="margin:0 0 6px 0">添加新图书</h5>
        <div style="display:flex; gap:8px; align-items:center; flex-wrap:wrap">
          <input v-model="newBook.name" placeholder="书名" style="flex:1; min-width:160px" />
          <input v-model="newBook.author" placeholder="作者" style="width:160px" />
          <input v-model="newBook.publicationTime" type="date" style="width:160px" />
          <button @click="addBook">添加图书</button>
        </div>
      </div>

      <ul class="book-list">
        <li v-for="b in books" :key="b.id">
          <div><strong>{{ b.name }}</strong> — {{ b.author ?? '' }}</div>
          <div>出版: {{ b.publicationTime ?? '-' }}</div>
          <div><button @click="borrowBook(b.id)">借阅</button></div>
        </li>
      </ul>
    </template>

  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { listBooks, borrow, createBook } from '../api'
const props = defineProps({ library: { type: Object, required: true }, studentId: { type: [Number, String], required: false } })
const emit = defineEmits(['close', 'borrowed', 'book-added'])
const books = ref([])

// new book form state
const newBook = ref({ name: '', author: '', publicationTime: '' })

// collapse state to allow map to be more visible
const collapsed = ref(false)

async function refresh() {
  books.value = await listBooks(props.library.id)
}

watch(() => props.library, () => { refresh() }, { immediate: true })

function close() { emit('close') }

async function borrowBook(bookId) {
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

async function addBook() {
  if (!newBook.value.name) return alert('请输入书名')
  const payload = {
    libraryId: props.library.id,
    name: newBook.value.name,
    author: newBook.value.author || null,
  }
  if (newBook.value.publicationTime) {
    // convert date input (YYYY-MM-DD) to ISO datetime
    payload.publicationTime = newBook.value.publicationTime + 'T00:00:00'
  }
  try {
    const created = await createBook(payload)
    alert('添加成功，书籍 ID: ' + created.id)
    // reset form
    newBook.value.name = ''
    newBook.value.author = ''
    newBook.value.publicationTime = ''
    // refresh list
    await refresh()
    emit('book-added', created)
  } catch (err) {
    console.error('添加图书失败', err)
    alert('添加图书失败')
  }
}

</script>

<style scoped>
.panel { position: fixed; right: 12px; top: 64px; width: 320px; max-height: calc(100vh - 100px); overflow:auto; background:white; border:1px solid #ddd; padding:10px; box-shadow:0 2px 8px rgba(0,0,0,0.15); z-index: 10000; }
.panel.collapsed { width:56px; height:56px; padding:6px; display:flex; align-items:center; justify-content:center }
.collapse-toggle { position:absolute; right:8px; top:8px; background:#f5f5f5; border:1px solid #ddd; padding:4px 6px; cursor:pointer }
.book-list { list-style:none; padding:0 }
.book-list li { padding:6px 0; border-bottom:1px solid #eee }
</style>

