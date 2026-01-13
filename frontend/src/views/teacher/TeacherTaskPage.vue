<template>
  <div class="cc-page">
    <div class="cc-page-head">
      <div class="cc-page-title">学习路径（章节）</div>
      <div class="cc-page-actions">
        <el-input v-model.number="courseId" placeholder="courseId" style="width: 200px" />
        <el-button type="primary" :disabled="!courseId" @click="load">刷新</el-button>
      </div>
    </div>

    <div class="hint">提示：点击任务卡片或“管理章节”，进入右侧大纲视图。</div>

    <div class="create">
        <div class="create-title">新建任务</div>
        <div class="create-form">
          <el-input v-model="newTitle" placeholder="任务标题" />
          <el-input v-model="newDesc" placeholder="任务描述（可选）" />
          <el-button type="primary" :disabled="!courseId || !newTitle" @click="create">创建</el-button>
        </div>
      </div>

      <div v-if="!rows.length" class="empty">
        <el-empty description="暂无任务，先创建一个学习路径吧" />
      </div>

      <div v-else class="grid">
        <div v-for="t in rows" :key="t.id" class="task cc-glass" @click="goDetail(t.id)">
          <div class="task-top">
            <div class="task-title">{{ t.title }}</div>
            <el-tag size="small" effect="plain">ID: {{ t.id }}</el-tag>
          </div>
          <div class="task-desc">{{ t.description || '（无描述）' }}</div>
          <div class="task-meta">
            <span>排序：{{ t.sortNo }}</span>
          </div>
          <div class="task-actions">
            <el-button size="small" type="primary" @click.stop="goDetail(t.id)">管理章节</el-button>
          </div>
        </div>
      </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { http } from '@/api/http'
import type { ApiResponse } from '@/types/api'

type TaskRow = {
  id: number
  courseId: number
  title: string
  description: string
  sortNo: number
}

const route = useRoute()
const router = useRouter()

function readCourseIdFromRoute() {
  const cid = Number((route.params.courseId as string | undefined) || route.query.courseId || 0)
  return Number.isFinite(cid) ? cid : 0
}

const courseId = ref<number>(readCourseIdFromRoute())
const rows = ref<TaskRow[]>([])

const newTitle = ref('')
const newDesc = ref('')

async function load() {
  const resp = await http.get<ApiResponse<TaskRow[]>>('/api/teacher/tasks', { params: { courseId: courseId.value } })
  rows.value = resp.data.data
}

async function create() {
  await http.post<ApiResponse<number>>('/api/teacher/tasks', {
    courseId: courseId.value,
    title: newTitle.value,
    description: newDesc.value
  })
  newTitle.value = ''
  newDesc.value = ''
  await load()
}

function goDetail(taskId: number) {
  router.push(`/teacher/tasks/${taskId}`)
}

onMounted(() => {
  if (courseId.value) load()
})

watch(
  () => route.params.courseId,
  async () => {
    courseId.value = readCourseIdFromRoute()
    if (courseId.value) await load()
  }
)
</script>

<style scoped>
.create {
  padding: 14px;
  margin-bottom: 12px;
}

.hint {
  margin: 0 0 12px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.create-title {
  font-weight: 700;
  margin-bottom: 10px;
}

.create-form {
  display: grid;
  grid-template-columns: 1.2fr 1.6fr auto;
  gap: 10px;
  align-items: center;
}

.grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.task {
  padding: 14px;
  cursor: pointer;
  user-select: none;
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

.task-desc {
  margin-top: 8px;
  color: var(--el-text-color-secondary);
  line-height: 1.5;
  min-height: 42px;
}

.task-meta {
  margin-top: 10px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.task-actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}

.empty {
  padding: 20px 12px;
}

@media (max-width: 980px) {
  .grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .create-form {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
