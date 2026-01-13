<template>
  <div class="page" />
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

function goBoard() {
  if (auth.role === 'TEACHER') router.replace('/teacher/courses')
  else if (auth.role === 'STUDENT') router.replace('/student/courses')
  else if (auth.role === 'ADMIN') router.replace('/admin/users')
  else router.replace('/login')
}

onMounted(goBoard)
</script>

<style scoped>
.page {
  max-width: 1200px;
  margin: 0 auto;
}

</style>
