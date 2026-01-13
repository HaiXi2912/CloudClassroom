<template>
  <div class="page cc-page">
    <div class="cc-page-head">
      <div class="cc-page-title">考试</div>
      <div class="cc-page-actions">
        <el-button size="small" @click="load">刷新</el-button>
        <el-button size="small" type="primary" :disabled="!selectedCourseId" @click="openCreate">创建考试</el-button>
      </div>
    </div>

      <div class="filters">
        <el-form v-if="!isCourseScoped" inline>
          <el-form-item label="课程">
            <el-select v-model="selectedCourseId" placeholder="请选择课程" style="width: 360px" @change="load">
              <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
            </el-select>
          </el-form-item>
        </el-form>
      </div>

      <el-skeleton v-if="loading" :rows="6" animated />

      <div v-else>
        <el-empty v-if="!selectedCourseId" description="请先选择课程" />
        <el-empty v-else-if="rows.length === 0" description="该课程暂无考试" />

        <div v-else class="grid">
          <el-card v-for="e in rows" :key="e.id" class="item" shadow="never">
            <div class="item-title">{{ e.title }}</div>
            <div class="item-meta">
              <span>总分：{{ e.totalScore }} 分</span>
              <span class="item-id">#{{ e.id }}</span>
            </div>
            <div class="item-meta">
              <span>时长：{{ e.durationMinutes }} 分钟</span>
              <span></span>
            </div>
            <div class="item-meta">
              <span>开始：{{ e.startAt || '-' }}</span>
              <span>结束：{{ e.endAt || '-' }}</span>
            </div>
            <div class="item-actions">
              <el-button size="small" @click="openQuestions(e)">查看题目</el-button>
            </div>
          </el-card>
        </div>
      </div>

    <el-dialog v-model="showQuestions" title="考试题目" width="900px">
      <el-table :data="questionRows" stripe>
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="questionType" label="题型" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.questionType === 1" type="success">单选</el-tag>
            <el-tag v-else type="info">类型{{ scope.row.questionType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="questionText" label="题目" />
        <el-table-column prop="score" label="分值" width="90" />
      </el-table>
      <template #footer>
        <el-button @click="showQuestions = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showCreate" title="创建考试" width="1000px">
      <el-form :model="createForm" label-width="90px">
        <el-form-item label="课程">
          <el-select v-model="createForm.courseId" placeholder="请选择课程" style="width: 420px">
            <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="标题">
          <el-input v-model="createForm.title" placeholder="请输入考试标题" />
        </el-form-item>

        <el-form-item label="时长">
          <el-input-number v-model="createForm.durationMinutes" :min="1" :max="600" />
          <span style="margin-left: 8px; color: var(--el-text-color-secondary)">分钟</span>
        </el-form-item>

        <el-form-item label="开始">
          <el-date-picker v-model="createForm.startAt" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>

        <el-form-item label="结束">
          <el-date-picker v-model="createForm.endAt" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>

        <el-divider>题目（单选）</el-divider>

        <div style="margin-bottom: 10px; display: flex; gap: 8px">
          <el-button size="small" :disabled="!createForm.courseId" @click="openBankPicker">从题库导入</el-button>
          <el-button size="small" :disabled="!createForm.courseId" @click="openRandomPicker">随机抽题</el-button>
          <div style="flex: 1"></div>
          <div style="color: var(--el-text-color-secondary); font-size: 12px">总分：{{ totalScore }} 分</div>
        </div>

        <el-table :data="createForm.questions" stripe>
          <el-table-column label="#" width="60">
            <template #default="scope">
              {{ scope.$index + 1 }}
            </template>
          </el-table-column>

          <el-table-column label="题目" min-width="220">
            <template #default="scope">
              <el-input v-model="scope.row.questionText" placeholder="请输入题目" />
            </template>
          </el-table-column>

          <el-table-column label="A" width="140">
            <template #default="scope">
              <el-input v-model="scope.row.optionA" placeholder="A" />
            </template>
          </el-table-column>
          <el-table-column label="B" width="140">
            <template #default="scope">
              <el-input v-model="scope.row.optionB" placeholder="B" />
            </template>
          </el-table-column>
          <el-table-column label="C" width="140">
            <template #default="scope">
              <el-input v-model="scope.row.optionC" placeholder="C" />
            </template>
          </el-table-column>
          <el-table-column label="D" width="140">
            <template #default="scope">
              <el-input v-model="scope.row.optionD" placeholder="D" />
            </template>
          </el-table-column>

          <el-table-column label="答案" width="110">
            <template #default="scope">
              <el-select v-model="scope.row.correctAnswer" style="width: 90px">
                <el-option label="A" value="A" />
                <el-option label="B" value="B" />
                <el-option label="C" value="C" />
                <el-option label="D" value="D" />
              </el-select>
            </template>
          </el-table-column>

          <el-table-column label="分值" width="110">
            <template #default="scope">
              <el-input-number v-model="scope.row.score" :min="1" :max="100" />
            </template>
          </el-table-column>

          <el-table-column label="操作" width="100">
            <template #default="scope">
              <el-button size="small" type="danger" @click="removeQuestion(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div style="margin-top: 10px; display: flex; gap: 8px">
          <el-button @click="addQuestion">新增题目</el-button>
        </div>
      </el-form>

      <template #footer>
        <el-button @click="showCreate = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="create">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showBankPicker" title="从题库导入" width="900px">
      <el-skeleton v-if="bankLoading" :rows="6" animated />
      <el-empty v-else-if="bankRows.length === 0" description="题库暂无题目" />

      <el-table
        v-else
        :data="bankRows"
        stripe
        @selection-change="onBankSelectionChange"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="questionText" label="题目" min-width="260" />
        <el-table-column label="答案" width="90">
          <template #default="scope">
            <el-tag type="success">{{ scope.row.correctAnswer }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="分值" width="90" />
      </el-table>

      <template #footer>
        <el-button @click="showBankPicker = false">取消</el-button>
        <el-button type="primary" :disabled="bankSelected.length === 0" @click="applyBankSelection">导入</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showRandomPicker" title="随机抽题" width="520px">
      <el-form label-width="90px">
        <el-form-item label="题目数量">
          <el-input-number v-model="randomCount" :min="1" :max="50" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRandomPicker = false">取消</el-button>
        <el-button type="primary" :loading="randomLoading" @click="doRandomPick">抽取并导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'
import { http } from '@/api/http'
import type { ApiResponse } from '@/types/api'

type CourseRow = { id: number; courseName: string; courseCode: string; description: string }

type ExamRow = {
  id: number
  courseId: number
  title: string
  totalScore: number
  durationMinutes: number
  startAt: string
  endAt: string
  createdAt: string
}

type ExamQuestionRow = {
  id: number
  examId: number
  questionType: number
  questionText: string
  optionA: string
  optionB: string
  optionC: string
  optionD: string
  correctAnswer: string
  score: number
}

type CreateExamQuestion = {
  questionType: number
  questionText: string
  optionA: string
  optionB: string
  optionC: string
  optionD: string
  correctAnswer: string
  score: number
}

type BankRow = {
  id: number
  courseId: number
  questionType: number
  questionText: string
  optionA: string
  optionB: string
  optionC: string
  optionD: string
  correctAnswer: string
  score: number
}

const route = useRoute()
const isCourseScoped = computed(() => !!route.params.courseId)

const courses = ref<CourseRow[]>([])
const selectedCourseId = ref<number | undefined>(undefined)

const rows = ref<ExamRow[]>([])
const loading = ref(false)

const showQuestions = ref(false)
const questionRows = ref<ExamQuestionRow[]>([])

const showCreate = ref(false)
const creating = ref(false)

const createForm = reactive({
  courseId: undefined as number | undefined,
  title: '',
  durationMinutes: 60,
  startAt: '',
  endAt: '',
  questions: [] as CreateExamQuestion[]
})

const totalScore = computed(() => createForm.questions.reduce((sum, q) => sum + (q.score || 0), 0))

const showBankPicker = ref(false)
const bankLoading = ref(false)
const bankRows = ref<BankRow[]>([])
const bankSelected = ref<BankRow[]>([])

const showRandomPicker = ref(false)
const randomCount = ref(5)
const randomLoading = ref(false)

async function loadCourses() {
  const resp = await http.get<ApiResponse<CourseRow[]>>('/api/teacher/courses')
  courses.value = resp.data.data || []

  if (isCourseScoped.value) {
    const cid = Number(route.params.courseId || 0)
    selectedCourseId.value = cid || undefined
    return
  }

  if (!selectedCourseId.value && courses.value.length > 0) selectedCourseId.value = courses.value[0].id
}

async function load() {
  if (!selectedCourseId.value) return
  loading.value = true
  try {
    const resp = await http.get<ApiResponse<ExamRow[]>>('/api/teacher/exams', {
      params: { courseId: selectedCourseId.value }
    })
    rows.value = resp.data.data || []
  } finally {
    loading.value = false
  }
}

async function openQuestions(exam: ExamRow) {
  const resp = await http.get<ApiResponse<ExamQuestionRow[]>>(`/api/teacher/exams/${exam.id}/questions`)
  questionRows.value = resp.data.data || []
  showQuestions.value = true
}

function openCreate() {
  createForm.courseId = selectedCourseId.value
  createForm.title = ''
  createForm.durationMinutes = 60
  createForm.questions = []
  createForm.startAt = ''
  createForm.endAt = ''
  addQuestion()
  showCreate.value = true
}

function addQuestion() {
  createForm.questions.push({
    questionType: 1,
    questionText: '',
    optionA: '',
    optionB: '',
    optionC: '',
    optionD: '',
    correctAnswer: 'A',
    score: 5
  })
}

function removeQuestion(index: number) {
  createForm.questions.splice(index, 1)
}

async function openBankPicker() {
  if (!createForm.courseId) {
    ElMessage.error('请选择课程')
    return
  }
  bankSelected.value = []
  showBankPicker.value = true
  bankLoading.value = true
  try {
    const resp = await http.get<ApiResponse<BankRow[]>>('/api/teacher/question-bank', {
      params: { courseId: createForm.courseId }
    })
    bankRows.value = resp.data.data || []
  } finally {
    bankLoading.value = false
  }
}

function onBankSelectionChange(sel: BankRow[]) {
  bankSelected.value = sel
}

function toCreateQuestion(r: BankRow): CreateExamQuestion {
  return {
    questionType: r.questionType || 1,
    questionText: r.questionText || '',
    optionA: r.optionA || '',
    optionB: r.optionB || '',
    optionC: r.optionC || '',
    optionD: r.optionD || '',
    correctAnswer: r.correctAnswer || 'A',
    score: r.score || 5
  }
}

function applyBankSelection() {
  if (bankSelected.value.length === 0) return
  for (const r of bankSelected.value) {
    createForm.questions.push(toCreateQuestion(r))
  }
  ElMessage.success(`已导入 ${bankSelected.value.length} 道题`)
  showBankPicker.value = false
}

function openRandomPicker() {
  if (!createForm.courseId) {
    ElMessage.error('请选择课程')
    return
  }
  randomCount.value = 5
  showRandomPicker.value = true
}

async function doRandomPick() {
  if (!createForm.courseId) return
  randomLoading.value = true
  try {
    const resp = await http.get<ApiResponse<BankRow[]>>('/api/teacher/question-bank/random', {
      params: { courseId: createForm.courseId, count: randomCount.value }
    })
    const picked = resp.data.data || []
    for (const r of picked) {
      createForm.questions.push(toCreateQuestion(r))
    }
    ElMessage.success(`已抽取并导入 ${picked.length} 道题`)
    showRandomPicker.value = false
  } finally {
    randomLoading.value = false
  }
}

async function create() {
  if (!createForm.courseId) {
    ElMessage.error('请选择课程')
    return
  }
  if (!createForm.title.trim()) {
    ElMessage.error('请输入标题')
    return
  }
  if (!createForm.startAt || !createForm.endAt) {
    ElMessage.error('请选择开始/结束时间')
    return
  }
  if (createForm.questions.length === 0) {
    ElMessage.error('请至少添加 1 道题')
    return
  }

  for (const q of createForm.questions) {
    if (!q.questionText.trim()) {
      ElMessage.error('题目不能为空')
      return
    }
    if (!q.optionA.trim() || !q.optionB.trim() || !q.optionC.trim() || !q.optionD.trim()) {
      ElMessage.error('选项 A-D 不能为空')
      return
    }
    if (!q.correctAnswer) {
      ElMessage.error('请选择正确答案')
      return
    }
  }

  creating.value = true
  try {
    await http.post<ApiResponse<number>>('/api/teacher/exams', {
      courseId: createForm.courseId,
      title: createForm.title,
      durationMinutes: createForm.durationMinutes,
      startAt: createForm.startAt,
      endAt: createForm.endAt,
      questions: createForm.questions
    })

    ElMessage.success('已创建')
    showCreate.value = false
    selectedCourseId.value = createForm.courseId
    await load()
  } finally {
    creating.value = false
  }
}

onMounted(async () => {
  await loadCourses()
  if (selectedCourseId.value) {
    await load()
  }
})

watch(
  () => route.params.courseId,
  async () => {
    if (!isCourseScoped.value) return
    const cid = Number(route.params.courseId || 0)
    selectedCourseId.value = cid || undefined
    if (selectedCourseId.value) await load()
  }
)
</script>

<style scoped>
.page {
  max-width: 1200px;
  margin: 0 auto;
}

.filters {
  margin-bottom: 12px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.item {
  overflow: hidden;
}

.item-title {
  font-weight: 700;
  font-size: 14px;
  line-height: 1.3;
}

.item-meta {
  margin-top: 8px;
  display: flex;
  justify-content: space-between;
  gap: 10px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.item-id {
  color: var(--el-text-color-secondary);
}

.item-actions {
  margin-top: 10px;
  display: flex;
  gap: 8px;
}

@media (max-width: 1100px) {
  .grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
