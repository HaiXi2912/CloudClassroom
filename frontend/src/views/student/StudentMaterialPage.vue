<template>
  <div class="cc-page">
    <div class="cc-page-head">
      <div class="cc-page-title">资料</div>
      <div class="cc-page-actions">
        <el-input v-if="!isCourseScoped" v-model.number="courseIdInput" placeholder="courseId" style="width: 200px" />
        <el-button size="small" type="primary" @click="load">查询</el-button>
      </div>
    </div>

    <div v-if="!rows.length" class="empty">
      <el-empty description="暂无资料" />
    </div>

    <div v-else class="list">
      <div v-for="m in rows" :key="m.id" class="item">
        <div class="item-left">
          <div class="item-title">{{ m.title }}</div>
          <div class="item-meta">类型：{{ m.materialType === 1 ? 'PDF' : m.materialType === 2 ? 'PPT' : m.materialType === 3 ? '视频' : m.materialType }} · ID: {{ m.id }}</div>
        </div>
        <div class="item-actions">
          <el-button size="small" @click="download(m.id)">下载</el-button>
          <el-button size="small" type="primary" @click="read(m.id)">阅读/进度</el-button>
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

type MaterialRow = {
  id: number
  title: string
  materialType: number
}

const route = useRoute()
const router = useRouter()

const isCourseScoped = computed(() => !!route.params.courseId)
const courseIdInput = ref<number>(Number(route.query.courseId || 0))
const courseId = computed(() => {
  if (isCourseScoped.value) return Number(route.params.courseId || 0)
  return courseIdInput.value
})
const rows = ref<MaterialRow[]>([])

async function load() {
  if (!courseId.value) return
  const resp = await http.get<ApiResponse<MaterialRow[]>>('/api/student/materials', {
    params: { courseId: courseId.value }
  })
  rows.value = resp.data.data
}

function download(materialId: number) {
  window.open(`/api/student/materials/${materialId}/file`)
}

function read(materialId: number) {
  router.push({ path: `/student/materials/${materialId}/read`, query: { courseId: String(courseId.value || '') } })
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
  display: flex;
  gap: 8px;
  flex: 0 0 auto;
  flex-wrap: wrap;
  justify-content: flex-end;
}
</style>
