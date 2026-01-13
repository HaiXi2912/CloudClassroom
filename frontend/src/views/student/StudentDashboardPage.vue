<template>
  <div class="cc-page">
    <div class="cc-title">学习看板</div>

    <div class="toolbar">
      <el-input v-model.number="courseId" placeholder="courseId" style="width: 200px" />
      <div class="toolbar-actions">
        <el-button type="primary" @click="load">查询</el-button>
      </div>
    </div>

    <div v-if="data" class="grid">
      <div class="metric">
        <div class="metric-title">任务完成度</div>
        <el-progress :percentage="data.taskPercent || 0" />
        <div class="metric-sub">{{ data.taskDone }} / {{ data.taskTotal }} 步</div>
      </div>

      <div class="metric">
        <div class="metric-title">作业完成度</div>
        <el-progress :percentage="data.assignmentPercent || 0" />
        <div class="metric-sub">{{ data.assignmentDone }} / {{ data.assignmentTotal }} 个</div>
      </div>

      <div class="metric">
        <div class="metric-title">考试完成度</div>
        <el-progress :percentage="data.examPercent || 0" />
        <div class="metric-sub">{{ data.examDone }} / {{ data.examTotal }} 场</div>
      </div>

      <div class="metric">
        <div class="metric-title">总评（得分率）</div>
        <div class="score">{{ data.finalScore ?? 0 }}</div>
        <div class="metric-sub">任务/作业/考试按课程权重计算</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { http } from '@/api/http'
import type { ApiResponse } from '@/types/api'

type DashboardRow = {
  courseId: number
  taskPercent: number
  assignmentPercent: number
  examPercent: number
  taskDone: number
  taskTotal: number
  assignmentDone: number
  assignmentTotal: number
  examDone: number
  examTotal: number
  finalScore: number
}

const route = useRoute()

const courseId = ref<number>(Number(route.query.courseId || 0))
const data = ref<DashboardRow | null>(null)

async function load() {
  const resp = await http.get<ApiResponse<DashboardRow>>(`/api/student/courses/${courseId.value}/dashboard`)
  data.value = resp.data.data
}
</script>

<style scoped>
.cc-title {
  font-weight: 700;
}

.toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.toolbar-actions {
  display: flex;
  gap: 8px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.metric {
  padding: 14px 14px;
}

@media (max-width: 720px) {
  .grid {
    grid-template-columns: 1fr;
  }
}

.metric-title {
  font-weight: 700;
  margin-bottom: 8px;
}

.metric-sub {
  margin-top: 8px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.score {
  font-size: 28px;
  font-weight: 800;
  line-height: 1;
}
</style>
