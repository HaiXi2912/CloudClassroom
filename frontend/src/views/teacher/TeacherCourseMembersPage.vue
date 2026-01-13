<template>
  <div class="page cc-page">
    <div class="cc-page-head">
      <div class="cc-page-title">课程成员</div>
      <div class="cc-page-actions">
        <el-button size="small" @click="load">刷新</el-button>
      </div>
    </div>

    <div class="cc-page-meta">课程ID：{{ courseId }}</div>

    <el-table :data="rows" stripe>
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="username" label="用户名" width="160" />
        <el-table-column prop="nickname" label="昵称" width="160" />
        <el-table-column prop="memberRole" label="角色" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.memberRole === 1" type="success">学生</el-tag>
            <el-tag v-else type="info">未知</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="joinedAt" label="加入时间" />
        <el-table-column label="操作" width="140">
          <template #default="scope">
            <el-button size="small" type="danger" @click="remove(scope.row.userId)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>

    <el-empty v-if="!loading && rows.length === 0" description="暂无成员" style="margin-top: 12px" />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { http } from '@/api/http'
import type { ApiResponse } from '@/types/api'

type CourseMemberRow = {
  userId: number
  username: string
  nickname: string
  memberRole: number
  joinedAt: string
}

const route = useRoute()
const router = useRouter()

const courseId = computed(() => Number(route.params.courseId))
const rows = ref<CourseMemberRow[]>([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const resp = await http.get<ApiResponse<CourseMemberRow[]>>(`/api/teacher/courses/${courseId.value}/members`)
    rows.value = resp.data.data || []
  } finally {
    loading.value = false
  }
}

async function remove(userId: number) {
  await ElMessageBox.confirm(`确定移除成员 ${userId} 吗？`, '确认', { type: 'warning' })
  await http.post<ApiResponse<void>>(`/api/teacher/courses/${courseId.value}/members/remove`, { userId })
  ElMessage.success('已移除')
  await load()
}

onMounted(load)
</script>

<style scoped>
.page {
  max-width: none;
  margin: 0;
}
</style>
