<template>
  <div class="cc-page">
    <div class="cc-page-head">
      <div class="cc-page-title">任务</div>
      <div class="cc-page-actions">
        <el-button size="small" type="primary" :disabled="!courseId" @click="load">刷新</el-button>
      </div>
    </div>

    <div v-if="!rows.length" class="empty">
      <el-empty description="暂无任务" />
    </div>

    <div v-else class="grid">
      <div v-for="t in rows" :key="t.id" class="task" @click="open(t.id)">
        <div class="task-top">
          <div class="task-title">{{ t.title }}</div>
          <el-tag size="small" effect="plain">{{ t.progressPercent || 0 }}%</el-tag>
        </div>

        <div class="task-progress">
          <el-progress :percentage="t.progressPercent || 0" :stroke-width="10" />
        </div>

        <div class="task-meta">
          <span>{{ t.doneSteps }} / {{ t.totalSteps }} 步</span>
          <span class="task-id">ID: {{ t.id }}</span>
        </div>

        <div class="task-actions">
          <el-button size="small" type="primary" @click.stop="open(t.id)">继续学习</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { http } from '@/api/http'
import type { ApiResponse } from '@/types/api'

type TaskRow = {
  id: number
  courseId: number
  title: string
  description?: string
  status?: number
  sortNo?: number
  totalSteps: number
  doneSteps: number
  progressPercent: number
}

const route = useRoute()
const router = useRouter()

const courseId = computed(() => {
  const pid = Number(route.params.courseId || 0)
  if (pid) return pid
  const qid = Number(route.query.courseId || 0)
  return qid || 0
})

const rows = ref<TaskRow[]>([])

async function load() {
  if (!courseId.value) {
    rows.value = []
    ElMessage.warning('缺少 courseId，请从课程进入章节列表')
    return
  }
  const resp = await http.get<ApiResponse<TaskRow[]>>('/api/student/tasks', {
    params: { courseId: courseId.value }
  })
  rows.value = resp.data.data
}

function open(taskId: number) {
  router.push(`/student/tasks/${taskId}`)
}

watch(courseId, () => {
  load()
})

onMounted(() => {
  if (courseId.value) load()
})
</script>

<style scoped>
.grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.task {
  padding: 14px;
  cursor: pointer;
  user-select: none;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: var(--el-border-radius-base);
  background: var(--el-bg-color);
}

.task-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.task-title {
  font-weight: 800;
}

.task-progress {
  margin-top: 10px;
}

.task-meta {
  margin-top: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.task-actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}

.task-id {
  opacity: 0.9;
}

.empty {
  padding: 20px 12px;
}

@media (max-width: 980px) {
  .grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
