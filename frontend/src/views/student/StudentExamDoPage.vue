<template>
  <div class="cc-page">
    <div class="cc-title">考试答题（含倒计时）</div>

    <div class="toolbar">
      <el-button @click="goBack">返回</el-button>
      <el-tag type="warning">剩余时间：{{ remainText }}</el-tag>
    </div>

    <el-form>
      <div v-for="q in questions" :key="q.id" class="q">
        <div class="q-title">{{ q.questionText }}</div>
        <el-input v-model="answers[q.id]" type="textarea" :rows="3" placeholder="请输入答案" />
      </div>

      <el-button type="primary" :disabled="remainSeconds <= 0" @click="submit">提交考试</el-button>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { http } from '@/api/http'
import type { ApiResponse } from '@/types/api'

type ExamQuestionRow = {
  id: number
  questionText: string
}

type AttemptRow = {
  id: number
  score: number
  submittedAt: string
}

const route = useRoute()
const router = useRouter()
const examId = Number(route.params.examId)
const courseId = computed(() => Number(route.query.courseId || 0))

const questions = ref<ExamQuestionRow[]>([])
const answers = reactive<Record<number, string>>({})

const remainSeconds = ref(0)
let timer: number | undefined

const remainText = computed(() => {
  const s = Math.max(0, remainSeconds.value)
  const mm = String(Math.floor(s / 60)).padStart(2, '0')
  const ss = String(s % 60).padStart(2, '0')
  return `${mm}:${ss}`
})

async function load() {
  const resp = await http.get<ApiResponse<ExamQuestionRow[]>>(`/api/student/exams/${examId}/questions`)
  questions.value = resp.data.data
}

async function loadAttempt() {
  const resp = await http.get<ApiResponse<AttemptRow>>(`/api/student/exams/${examId}/my-attempt`)
  if (resp.data.data && resp.data.data.submittedAt) {
    remainSeconds.value = 0
  }
}

function startTimer() {
  timer = window.setInterval(() => {
    remainSeconds.value -= 1
    if (remainSeconds.value <= 0 && timer) {
      window.clearInterval(timer)
    }
  }, 1000)
}

async function submit() {
  const payload = questions.value.map((q) => ({
    questionId: q.id,
    answerText: answers[q.id] || ''
  }))
  await http.post<ApiResponse<void>>(`/api/student/exams/${examId}/submit`, { answers: payload })
}

function goBack() {
  if (courseId.value) {
    router.push(`/student/courses/${courseId.value}/exams`)
    return
  }
  router.back()
}

onMounted(async () => {
  await load()
  await loadAttempt()
  if (remainSeconds.value <= 0) {
    const dm = Number(route.query.durationMinutes || 0)
    remainSeconds.value = dm > 0 ? dm * 60 : 60 * 60
  }
  startTimer()
})

onBeforeUnmount(() => {
  if (timer) window.clearInterval(timer)
})
</script>

<style scoped>
.cc-title {
  font-weight: 700;
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  flex-wrap: wrap;
  gap: 8px;
}

.q {
  margin-bottom: 16px;
}

.q-title {
  font-weight: 600;
  margin-bottom: 6px;
}
</style>
