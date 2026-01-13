<template>
  <div class="page cc-page">
    <div class="cc-page-head">
      <div class="cc-page-title">成绩</div>
      <div class="cc-page-actions"></div>
    </div>

    <div class="top">
      <div class="top-left">
        <div class="toolbar">
          <el-input v-model.number="courseId" placeholder="courseId" style="width: 200px" />
          <el-button type="primary" :disabled="!courseId" @click="loadAll">查询</el-button>
        </div>
      </div>

      <div class="top-right">
        <div class="weights">
          <div class="w-item">
            <div class="w-label">任务</div>
            <el-input-number v-model="wTask" :min="0" :max="100" />
          </div>
          <div class="w-item">
            <div class="w-label">作业</div>
            <el-input-number v-model="wAssignment" :min="0" :max="100" />
          </div>
          <div class="w-item">
            <div class="w-label">考试</div>
            <el-input-number v-model="wExam" :min="0" :max="100" />
          </div>

          <div class="w-actions">
            <el-tag :type="weightSum === 100 ? 'success' : 'warning'">
              {{ weightSum === 100 ? '可保存' : '权重需为 100' }}
            </el-tag>
            <el-button type="primary" :disabled="!courseId || weightSum !== 100" @click="save">保存</el-button>
          </div>
        </div>
      </div>
    </div>

    <el-table :data="rows" stripe>
      <el-table-column prop="studentId" label="学生ID" width="110" />
      <el-table-column prop="nickname" label="昵称" width="140" />
      <el-table-column prop="taskPercent" label="任务完成度" width="120" />
      <el-table-column prop="assignmentScorePercent" label="作业得分率" width="120" />
      <el-table-column prop="examScorePercent" label="考试得分率" width="120" />
      <el-table-column prop="finalScore" label="总评" width="100" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { http } from '@/api/http'
import type { ApiResponse } from '@/types/api'

type GradeConfigRow = {
  courseId: number
  weightTask: number
  weightAssignment: number
  weightExam: number
}

type StudentGradeRow = {
  studentId: number
  nickname: string
  taskPercent: number
  assignmentScorePercent: number
  examScorePercent: number
  finalScore: number
}

const route = useRoute()

function readCourseIdFromRoute() {
  const cid = Number((route.params.courseId as string | undefined) || route.query.courseId || 0)
  return Number.isFinite(cid) ? cid : 0
}

const courseId = ref<number>(readCourseIdFromRoute())

const wTask = ref<number>(30)
const wAssignment = ref<number>(30)
const wExam = ref<number>(40)

const rows = ref<StudentGradeRow[]>([])

const weightSum = computed(() => wTask.value + wAssignment.value + wExam.value)

async function loadAll() {
  const cfg = await http.get<ApiResponse<GradeConfigRow>>(`/api/teacher/courses/${courseId.value}/grade-config`)
  wTask.value = cfg.data.data.weightTask
  wAssignment.value = cfg.data.data.weightAssignment
  wExam.value = cfg.data.data.weightExam

  const g = await http.get<ApiResponse<StudentGradeRow[]>>(`/api/teacher/courses/${courseId.value}/grades`)
  rows.value = g.data.data
}

async function save() {
  await http.post<ApiResponse<void>>(`/api/teacher/courses/${courseId.value}/grade-config`, {
    weightTask: wTask.value,
    weightAssignment: wAssignment.value,
    weightExam: wExam.value
  })
  await loadAll()
}

onMounted(async () => {
  if (courseId.value) {
    await loadAll()
  }
})

watch(
  () => route.params.courseId,
  async () => {
    courseId.value = readCourseIdFromRoute()
    if (courseId.value) {
      await loadAll()
    }
  }
)
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.page {
  width: 100%;
  max-width: none;
}

.top {
  margin-bottom: 12px;
  display: grid;
  grid-template-columns: 1fr 1.2fr;
  gap: 12px;
}

.weights {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.w-item {
  display: grid;
  gap: 6px;
}

.w-label {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.w-actions {
  grid-column: 1 / -1;
  display: flex;
  gap: 10px;
  align-items: center;
  justify-content: flex-end;
}

@media (max-width: 860px) {
  .top {
    grid-template-columns: 1fr;
  }

  .weights {
    grid-template-columns: 1fr;
  }
}
</style>
