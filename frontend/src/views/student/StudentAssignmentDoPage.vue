<template>
  <div class="cc-page">
    <div class="cc-title">作业答题</div>

    <div class="toolbar">
      <el-button @click="goBack">返回</el-button>
    </div>

    <el-form>
      <div v-for="q in questions" :key="q.id" class="q">
        <div class="q-title">{{ q.questionText }}</div>
        <el-input v-model="answers[q.id]" type="textarea" :rows="3" placeholder="请输入答案" />
      </div>

      <el-button type="primary" @click="submit">提交作业</el-button>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { http } from '@/api/http'
import type { ApiResponse } from '@/types/api'

type AssignmentQuestionRow = {
  id: number
  questionText: string
}

const route = useRoute()
const router = useRouter()
const assignmentId = Number(route.params.assignmentId)
const courseId = computed(() => Number(route.query.courseId || 0))

const questions = ref<AssignmentQuestionRow[]>([])
const answers = reactive<Record<number, string>>({})

async function load() {
  const resp = await http.get<ApiResponse<AssignmentQuestionRow[]>>(`/api/student/assignments/${assignmentId}/questions`)
  questions.value = resp.data.data
}

async function submit() {
  const payload = questions.value.map((q) => ({
    questionId: q.id,
    answerText: answers[q.id] || ''
  }))
  await http.post<ApiResponse<void>>(`/api/student/assignments/${assignmentId}/submit`, { answers: payload })
}

function goBack() {
  if (courseId.value) {
    router.push(`/student/courses/${courseId.value}/assignments`)
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

.q {
  margin-bottom: 16px;
}

.q-title {
  font-weight: 600;
  margin-bottom: 6px;
}
</style>
