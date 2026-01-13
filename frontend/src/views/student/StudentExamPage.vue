<template>
  <div class="cc-page">
    <div class="cc-page-head">
      <div class="cc-page-title">考试</div>
      <div class="cc-page-actions">
        <el-input v-if="!isCourseScoped" v-model.number="courseIdInput" placeholder="courseId" style="width: 200px" />
        <el-button size="small" type="primary" @click="load">查询</el-button>
      </div>
    </div>

    <div v-if="!rows.length" class="empty">
      <el-empty description="暂无考试" />
    </div>

    <div v-else class="list">
      <div v-for="e in rows" :key="e.id" class="item">
        <div class="item-left">
          <div class="item-title">{{ e.title }}</div>
          <div class="item-meta">开始：{{ e.startAt || '-' }} · 结束：{{ e.endAt || '-' }} · 时长：{{ e.durationMinutes }} 分钟 · ID: {{ e.id }}</div>
        </div>
        <div class="item-actions">
          <el-button size="small" type="primary" @click="doIt(e.id, e.durationMinutes)">进入答题</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { http } from '@/api/http'
import type { ApiResponse } from '@/types/api'

type ExamRow = {
  id: number
  title: string
  startAt: string
  endAt: string
  durationMinutes: number
}

const route = useRoute()
const router = useRouter()

const isCourseScoped = computed(() => !!route.params.courseId)
const courseIdInput = ref<number>(Number(route.query.courseId || 0))
const courseId = computed(() => {
  if (isCourseScoped.value) return Number(route.params.courseId || 0)
  return courseIdInput.value
})
const rows = ref<ExamRow[]>([])

async function load() {
  if (!courseId.value) return
  const resp = await http.get<ApiResponse<ExamRow[]>>('/api/student/exams', {
    params: { courseId: courseId.value }
  })
  rows.value = resp.data.data
}

function doIt(examId: number, durationMinutes: number) {
  router.push({
    path: `/student/exams/${examId}`,
    query: { durationMinutes: String(durationMinutes), courseId: String(courseId.value || '') }
  })
}

onMounted(() => {
  if (courseId.value) load()
})

watch(
  () => route.params.courseId,
  () => {
    if (courseId.value) load()
  }
)
</script>

<style scoped>
.empty {
  padding: 20px 12px;
}

.list {
  display: grid;
  gap: 10px;
}

.item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: var(--el-border-radius-base);
}

.item-left {
  min-width: 0;
}

.item-title {
  font-weight: 800;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-meta {
  margin-top: 6px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.item-actions {
  flex: 0 0 auto;
}
</style>
