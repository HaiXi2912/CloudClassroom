<template>
  <div class="page cc-page">
    <div class="cc-page-head">
      <div class="cc-page-title">作业</div>
      <div class="cc-page-actions">
        <el-button size="small" @click="load">刷新</el-button>
        <el-button size="small" type="primary" :disabled="!selectedCourseId" @click="openCreate">布置作业</el-button>
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
        <el-empty v-else-if="rows.length === 0" description="该课程暂无作业" />

        <div v-else class="grid">
          <el-card v-for="a in rows" :key="a.id" class="item" shadow="never">
            <div class="item-title">{{ a.title }}</div>
            <div class="item-meta">
              <span>发布：{{ a.publishAt || '-' }}</span>
              <span>截止：{{ a.dueAt || '-' }}</span>
            </div>
            <div class="item-meta">
              <span>总分：{{ a.totalScore }} 分</span>
              <span class="item-id">#{{ a.id }}</span>
            </div>
            <div class="item-actions">
              <el-button size="small" @click="viewQuestions(a.id)">查看题目</el-button>
              <el-button size="small" type="primary" @click="router.push(`/teacher/assignments/${a.id}/submissions`)">提交/批改</el-button>
            </div>
          </el-card>
        </div>
      </div>

    <el-dialog v-model="showCreate" title="布置作业" width="920px">
      <el-form :model="createForm" label-width="90px">
        <el-form-item label="课程">
          <el-select v-model="createForm.courseId" placeholder="请选择课程" style="width: 420px">
            <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="createForm.title" placeholder="请输入作业标题" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="createForm.content" type="textarea" :rows="3" placeholder="可选" />
        </el-form-item>
        <el-form-item label="截止时间">
          <el-date-picker
            v-model="createForm.dueAt"
            type="datetime"
            value-format="YYYY-MM-DDTHH:mm:ss"
            placeholder="可选"
            style="width: 260px"
          />
        </el-form-item>

        <el-divider>题目</el-divider>

        <div class="q-actions">
          <el-button size="small" @click="addQuestion">添加题目</el-button>
        </div>

        <el-table :data="createForm.questions" stripe>
          <el-table-column label="#" width="60">
            <template #default="scope">{{ scope.$index + 1 }}</template>
          </el-table-column>
          <el-table-column label="题型" width="140">
            <template #default="scope">
              <el-select v-model="scope.row.questionType" style="width: 120px">
                <el-option label="单选" :value="1" />
                <el-option label="多选" :value="2" />
                <el-option label="判断" :value="3" />
                <el-option label="简答" :value="4" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="题干">
            <template #default="scope">
              <el-input v-model="scope.row.questionText" type="textarea" :rows="2" placeholder="请输入题干" />
            </template>
          </el-table-column>
          <el-table-column label="选项A" width="160">
            <template #default="scope">
              <el-input v-model="scope.row.optionA" placeholder="可选" />
            </template>
          </el-table-column>
          <el-table-column label="选项B" width="160">
            <template #default="scope">
              <el-input v-model="scope.row.optionB" placeholder="可选" />
            </template>
          </el-table-column>
          <el-table-column label="选项C" width="160">
            <template #default="scope">
              <el-input v-model="scope.row.optionC" placeholder="可选" />
            </template>
          </el-table-column>
          <el-table-column label="选项D" width="160">
            <template #default="scope">
              <el-input v-model="scope.row.optionD" placeholder="可选" />
            </template>
          </el-table-column>
          <el-table-column label="正确答案" width="140">
            <template #default="scope">
              <el-input v-model="scope.row.correctAnswer" placeholder="A/B/C/D/TRUE/FALSE" />
            </template>
          </el-table-column>
          <el-table-column label="分值" width="90">
            <template #default="scope">
              <el-input-number v-model="scope.row.score" :min="1" :max="100" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="90">
            <template #default="scope">
              <el-button size="small" type="danger" @click="removeQuestion(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form>

      <template #footer>
        <el-button @click="showCreate = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="create">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showQuestions" title="题目列表" width="820px">
      <el-table :data="questionRows" border>
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="questionType" label="题型" width="90" />
        <el-table-column prop="questionText" label="题干" />
        <el-table-column prop="score" label="分值" width="90" />
      </el-table>
      <template #footer>
        <el-button @click="showQuestions = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { http } from '@/api/http'
import type { ApiResponse } from '@/types/api'

type CourseRow = { id: number; courseName: string; courseCode: string; description: string }

type AssignmentRow = {
  id: number
  courseId: number
  title: string
  publishAt: string
  dueAt: string
  totalScore: number
}

type AssignmentQuestionRow = {
  id: number
  questionType: number
  questionText: string
  optionA: string
  optionB: string
  optionC: string
  optionD: string
  score: number
  sortNo: number
}

type CreateQuestion = {
  questionType: number
  questionText: string
  optionA?: string
  optionB?: string
  optionC?: string
  optionD?: string
  correctAnswer?: string
  score: number
  sortNo?: number
}

const router = useRouter()
const route = useRoute()

const isCourseScoped = computed(() => !!route.params.courseId)

const courses = ref<CourseRow[]>([])
const selectedCourseId = ref<number | undefined>(Number(route.query.courseId || 0) || undefined)

const rows = ref<AssignmentRow[]>([])
const loading = ref(false)

const showCreate = ref(false)
const creating = ref(false)

const createForm = reactive({
  courseId: undefined as number | undefined,
  title: '',
  content: '',
  dueAt: '' as string | undefined,
  questions: [] as CreateQuestion[]
})

const showQuestions = ref(false)
const questionRows = ref<AssignmentQuestionRow[]>([])

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
    const resp = await http.get<ApiResponse<AssignmentRow[]>>('/api/teacher/assignments', {
      params: { courseId: selectedCourseId.value }
    })
    rows.value = resp.data.data || []
  } finally {
    loading.value = false
  }
}

function openCreate() {
  createForm.courseId = selectedCourseId.value
  createForm.title = ''
  createForm.content = ''
  createForm.dueAt = ''
  createForm.questions = []
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

async function create() {
  if (!createForm.courseId) {
    ElMessage.error('请选择课程')
    return
  }
  if (!createForm.title.trim()) {
    ElMessage.error('请输入标题')
    return
  }
  if (!createForm.questions.length) {
    ElMessage.error('请至少添加一个题目')
    return
  }

  creating.value = true
  try {
    await http.post<ApiResponse<number>>('/api/teacher/assignments', {
      courseId: createForm.courseId,
      title: createForm.title,
      content: createForm.content,
      dueAt: createForm.dueAt || null,
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

async function viewQuestions(assignmentId: number) {
  const resp = await http.get<ApiResponse<AssignmentQuestionRow[]>>(`/api/teacher/assignments/${assignmentId}/questions`)
  questionRows.value = resp.data.data || []
  showQuestions.value = true
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
  width: 100%;
  max-width: none;
  margin: 0;
}

.filters {
  margin-bottom: 12px;
}

.q-actions {
  display: flex;
  justify-content: flex-end;
  margin: 8px 0;
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
