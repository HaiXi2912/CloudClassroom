<template>
  <div class="cc-page">
    <div class="cc-page-head">
      <div class="cc-page-title">题库</div>
      <div class="cc-page-actions">
        <el-button size="small" @click="openImport">从其它课程导入</el-button>
        <el-button size="small" type="primary" @click="openCreate">新增题目</el-button>
      </div>
    </div>

    <el-skeleton v-if="loading" :rows="6" animated />

    <el-empty v-else-if="rows.length === 0" description="暂无题目" />

    <el-table v-else :data="rows" stripe>
      <el-table-column prop="id" label="ID" width="90" />
      <el-table-column prop="questionText" label="题目" min-width="260" />
      <el-table-column label="答案" width="90">
        <template #default="scope">
          <el-tag type="success">{{ scope.row.correctAnswer }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="score" label="分值" width="90" />
      <el-table-column label="操作" width="160">
        <template #default="scope">
          <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showEdit" :title="editId ? '编辑题目' : '新增题目'" width="900px">
      <el-form :model="editForm" label-width="90px">
        <el-form-item label="题干">
          <el-input v-model="editForm.questionText" placeholder="请输入题干" />
        </el-form-item>

        <el-form-item label="选项A">
          <el-input v-model="editForm.optionA" placeholder="A" />
        </el-form-item>
        <el-form-item label="选项B">
          <el-input v-model="editForm.optionB" placeholder="B" />
        </el-form-item>
        <el-form-item label="选项C">
          <el-input v-model="editForm.optionC" placeholder="C" />
        </el-form-item>
        <el-form-item label="选项D">
          <el-input v-model="editForm.optionD" placeholder="D" />
        </el-form-item>

        <el-form-item label="答案">
          <el-select v-model="editForm.correctAnswer" style="width: 140px">
            <el-option label="A" value="A" />
            <el-option label="B" value="B" />
            <el-option label="C" value="C" />
            <el-option label="D" value="D" />
          </el-select>
        </el-form-item>

        <el-form-item label="分值">
          <el-input-number v-model="editForm.score" :min="1" :max="100" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showEdit = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showImport" title="从其它课程导入" width="520px">
      <el-form label-width="90px">
        <el-form-item label="源课程">
          <el-select v-model="importSourceCourseId" placeholder="请选择课程" style="width: 360px">
            <el-option v-for="c in importCourseOptions" :key="c.id" :label="c.courseName" :value="c.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <div style="color: var(--el-text-color-secondary); font-size: 12px">会把该课程题库全部复制到本课程题库</div>

      <template #footer>
        <el-button @click="showImport = false">取消</el-button>
        <el-button type="primary" :disabled="!importSourceCourseId" :loading="importing" @click="doImport">导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute } from 'vue-router'
import { http } from '@/api/http'
import type { ApiResponse } from '@/types/api'

type CourseRow = { id: number; courseName: string; courseCode: string; description: string }

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
const courseId = computed(() => Number(route.params.courseId || 0))

const rows = ref<BankRow[]>([])
const loading = ref(false)

const showEdit = ref(false)
const saving = ref(false)
const editId = ref<number | null>(null)

const editForm = reactive({
  questionType: 1,
  questionText: '',
  optionA: '',
  optionB: '',
  optionC: '',
  optionD: '',
  correctAnswer: 'A',
  score: 5
})

const showImport = ref(false)
const importing = ref(false)
const importSourceCourseId = ref<number | undefined>(undefined)
const importCourses = ref<CourseRow[]>([])

const importCourseOptions = computed(() => importCourses.value.filter((c) => c.id !== courseId.value))

async function load() {
  if (!courseId.value) return
  loading.value = true
  try {
    const resp = await http.get<ApiResponse<BankRow[]>>('/api/teacher/question-bank', { params: { courseId: courseId.value } })
    rows.value = resp.data.data || []
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editId.value = null
  editForm.questionType = 1
  editForm.questionText = ''
  editForm.optionA = ''
  editForm.optionB = ''
  editForm.optionC = ''
  editForm.optionD = ''
  editForm.correctAnswer = 'A'
  editForm.score = 5
  showEdit.value = true
}

function openEdit(row: BankRow) {
  editId.value = row.id
  editForm.questionType = row.questionType
  editForm.questionText = row.questionText
  editForm.optionA = row.optionA
  editForm.optionB = row.optionB
  editForm.optionC = row.optionC
  editForm.optionD = row.optionD
  editForm.correctAnswer = row.correctAnswer
  editForm.score = row.score
  showEdit.value = true
}

async function save() {
  if (!courseId.value) return
  if (!editForm.questionText.trim()) {
    ElMessage.error('题干不能为空')
    return
  }
  if (!editForm.optionA.trim() || !editForm.optionB.trim() || !editForm.optionC.trim() || !editForm.optionD.trim()) {
    ElMessage.error('选项 A-D 不能为空')
    return
  }
  if (!editForm.correctAnswer) {
    ElMessage.error('请选择答案')
    return
  }

  saving.value = true
  try {
    if (!editId.value) {
      await http.post<ApiResponse<number>>('/api/teacher/question-bank', {
        courseId: courseId.value,
        questionType: 1,
        questionText: editForm.questionText,
        optionA: editForm.optionA,
        optionB: editForm.optionB,
        optionC: editForm.optionC,
        optionD: editForm.optionD,
        correctAnswer: editForm.correctAnswer,
        score: editForm.score
      })
    } else {
      await http.put<ApiResponse<null>>(`/api/teacher/question-bank/${editId.value}`, {
        questionType: 1,
        questionText: editForm.questionText,
        optionA: editForm.optionA,
        optionB: editForm.optionB,
        optionC: editForm.optionC,
        optionD: editForm.optionD,
        correctAnswer: editForm.correctAnswer,
        score: editForm.score
      })
    }
    ElMessage.success('已保存')
    showEdit.value = false
    await load()
  } finally {
    saving.value = false
  }
}

async function remove(row: BankRow) {
  await ElMessageBox.confirm('确认删除该题目？', '提示', { type: 'warning' })
  await http.delete<ApiResponse<null>>(`/api/teacher/question-bank/${row.id}`)
  ElMessage.success('已删除')
  await load()
}

async function openImport() {
  importSourceCourseId.value = undefined
  showImport.value = true
  const resp = await http.get<ApiResponse<CourseRow[]>>('/api/teacher/courses')
  importCourses.value = resp.data.data || []
}

async function doImport() {
  if (!courseId.value || !importSourceCourseId.value) return
  importing.value = true
  try {
    const resp = await http.post<ApiResponse<number>>('/api/teacher/question-bank/import', {
      targetCourseId: courseId.value,
      sourceCourseId: importSourceCourseId.value
    })
    ElMessage.success(`已导入 ${resp.data.data || 0} 道题`) 
    showImport.value = false
    await load()
  } finally {
    importing.value = false
  }
}

onMounted(load)
</script>

<style scoped>
</style>
