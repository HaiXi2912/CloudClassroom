<template>
  <div class="cc-page">
    <div class="cc-title">资料阅读与进度</div>

    <div class="toolbar">
      <el-button @click="goBack">返回</el-button>
    </div>

    <div class="block">
      <div class="muted">当前进度：{{ progress.progressPercent }}%</div>
      <el-slider v-model="progress.progressPercent" :min="0" :max="100" style="max-width: 420px" />
    </div>

    <div class="actions">
      <el-input-number v-model="progress.lastPosition" :min="0" />
      <el-button type="primary" @click="save">保存进度</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { http } from '@/api/http'
import type { ApiResponse } from '@/types/api'

type MaterialProgressRow = {
  progressPercent: number
  lastPosition: number
}

const route = useRoute()
const router = useRouter()
const materialId = Number(route.params.materialId)
const courseId = computed(() => Number(route.query.courseId || 0))

const progress = reactive<MaterialProgressRow>({
  progressPercent: 0,
  lastPosition: 0
})

async function load() {
  const resp = await http.get<ApiResponse<MaterialProgressRow>>(`/api/student/materials/${materialId}/progress`)
  progress.progressPercent = resp.data.data.progressPercent
  progress.lastPosition = resp.data.data.lastPosition
}

async function save() {
  await http.post<ApiResponse<void>>(`/api/student/materials/${materialId}/progress`, {
    progressPercent: progress.progressPercent,
    lastPosition: progress.lastPosition
  })
}

function goBack() {
  if (courseId.value) {
    router.push(`/student/courses/${courseId.value}/materials`)
    return
  }
  router.back()
}

onMounted(load)
</script>

<style scoped>
.cc-title {
  font-weight: 700;
}

.toolbar {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 12px;
}

.block {
  margin-bottom: 12px;
}

.muted {
  color: var(--el-text-color-secondary);
  margin-bottom: 6px;
}

.actions {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}
</style>
