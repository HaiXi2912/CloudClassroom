<template>
  <div class="page cc-page">
    <div class="cc-page-head">
      <div class="cc-page-title">提交/批改</div>
      <div class="cc-page-actions">
        <el-button size="small" @click="router.back()">返回</el-button>
        <el-button size="small" @click="load">刷新</el-button>
      </div>
    </div>

      <div class="cc-page-meta">作业ID：{{ assignmentId }}</div>

      <el-skeleton v-if="loading" :rows="6" animated />

      <div v-else>
        <el-empty v-if="rows.length === 0" description="暂无提交" />

        <el-table v-else :data="rows" stripe>
          <el-table-column prop="id" label="提交ID" width="100" />
          <el-table-column prop="studentId" label="学生ID" width="100" />
          <el-table-column prop="status" label="状态" width="120">
            <template #default="scope">
              <el-tag v-if="scope.row.status === 1" type="info">已提交</el-tag>
              <el-tag v-else-if="scope.row.status === 2" type="success">已批改</el-tag>
              <el-tag v-else type="warning">未知</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="autoScore" label="自动分" width="90" />
          <el-table-column prop="manualScore" label="人工分" width="90" />
          <el-table-column prop="totalScore" label="总分" width="90" />
          <el-table-column prop="submitAt" label="提交时间" width="180" />
          <el-table-column prop="gradedAt" label="批改时间" width="180" />
          <el-table-column label="操作" width="160">
            <template #default="scope">
              <el-button size="small" type="primary" @click="openGrade(scope.row)">批改</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

    <el-dialog v-model="showGrade" title="批改" width="520px">
      <el-form :model="gradeForm" label-width="90px">
        <el-form-item label="提交ID">
          <el-input :model-value="gradeForm.submissionId" disabled />
        </el-form-item>
        <el-form-item label="人工分">
          <el-input-number v-model="gradeForm.manualScore" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="评语">
          <el-input v-model="gradeForm.teacherComment" type="textarea" :rows="3" placeholder="可选" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showGrade = false">取消</el-button>
        <el-button type="primary" :loading="grading" @click="submitGrade">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { http } from '@/api/http'
import type { ApiResponse } from '@/types/api'

type SubmissionRow = {
  id: number
  assignmentId: number
  studentId: number
  status: number
  autoScore: number | null
  manualScore: number | null
  totalScore: number | null
  submitAt: string
  gradedAt: string
}

const route = useRoute()
const router = useRouter()

const assignmentId = computed(() => Number(route.params.assignmentId))

const rows = ref<SubmissionRow[]>([])
const loading = ref(false)

const showGrade = ref(false)
const grading = ref(false)
const gradeForm = reactive({
  submissionId: undefined as number | undefined,
  manualScore: 0,
  teacherComment: ''
})

async function load() {
  loading.value = true
  try {
    const resp = await http.get<ApiResponse<SubmissionRow[]>>(`/api/teacher/assignments/${assignmentId.value}/submissions`)
    rows.value = resp.data.data || []
  } finally {
    loading.value = false
  }
}

function openGrade(row: SubmissionRow) {
  gradeForm.submissionId = row.id
  gradeForm.manualScore = row.manualScore ?? 0
  gradeForm.teacherComment = ''
  showGrade.value = true
}

async function submitGrade() {
  if (!gradeForm.submissionId) return
  grading.value = true
  try {
    await http.post<ApiResponse<void>>(`/api/teacher/assignments/submissions/${gradeForm.submissionId}/grade`, {
      manualScore: gradeForm.manualScore,
      teacherComment: gradeForm.teacherComment
    })
    ElMessage.success('已提交批改')
    showGrade.value = false
    await load()
  } finally {
    grading.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.page {
  max-width: none;
  margin: 0;
}
</style>
