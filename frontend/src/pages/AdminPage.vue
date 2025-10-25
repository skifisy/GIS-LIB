<template>
  <div style="height:100vh; display:flex; flex-direction:column;">
    <div style="padding:8px; background:#f5f5f5; display:flex; align-items:center; gap:12px;">
      <button @click="goBack">返回</button>
      <h3 style="margin:0">管理员视图</h3>
    </div>
    <div style="flex:1; padding:12px; overflow:auto">
      <AdminPanel />
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'
import AdminPanel from '../components/AdminPanel.vue'

export default {
  components: { AdminPanel },
  setup() {
    const router = useRouter()
    function goBack() { router.push('/') }
    return { goBack }
  }
}
</script>
