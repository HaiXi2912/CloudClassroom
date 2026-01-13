<template>
  <div class="page cc-page">
    <div class="cc-page-head">
      <div class="cc-page-title">资料</div>
      <div class="cc-page-actions">
        <el-button size="small" @click="load">刷新</el-button>
        <el-button size="small" type="primary" :disabled="!selectedCourseId" @click="openCreate">新增资料</el-button>
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
        <el-empty v-else-if="rows.length === 0" description="该课程暂无资料" />

        <div v-else class="grid">
          <el-card v-for="m in rows" :key="m.id" class="item" shadow="never">
            <div class="item-title">{{ m.title }}</div>
            <div class="item-meta">
              <span>
                <el-tag v-if="m.materialType === 1" type="success" size="small">资料</el-tag>
                <el-tag v-else type="info" size="small">类型{{ m.materialType }}</el-tag>
              </span>
              <span class="item-id">#{{ m.id }}</span>
            </div>
            <div class="item-meta">
              <span>创建：{{ m.createdAt || '-' }}</span>
              <span>排序：{{ m.sortNo }}</span>
            </div>
            <div class="item-actions">
              <el-button size="small" @click="download(m)">下载</el-button>
            </div>
          </el-card>
        </div>
      </div>

    <el-dialog v-model="showCreate" title="新增资料" width="760px">
      <el-form :model="createForm" label-width="90px">
        <el-form-item label="课程">
          <el-select v-model="createForm.courseId" placeholder="请选择课程" style="width: 420px">
            <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="标题">
          <el-input v-model="createForm.title" placeholder="请输入资料标题" />
        </el-form-item>

        <el-form-item label="类型">
          <el-select v-model="createForm.materialType" style="width: 200px">
            <el-option label="资料" :value="1" />
          </el-select>
        </el-form-item>

        <el-form-item label="排序">
          <el-input-number v-model="createForm.sortNo" :min="0" :max="9999" />
        </el-form-item>

        <el-divider>上传文件</el-divider>

        <el-form-item label="文件">
          <el-upload
            :limit="1"
            :auto-upload="false"
            :on-change="onFileChange"
            :on-remove="onFileRemove"
          >
            <el-button>选择文件</el-button>
          </el-upload>
        </el-form-item>

        <el-form-item label="fileId">
          <el-input :model-value="String(createForm.fileId || '')" disabled />
          <el-button style="margin-left: 8px" :disabled="!selectedFile" :loading="uploading" @click="upload">上传并获取fileId</el-button>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showCreate = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="create">提交</el-button>
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
import { downloadFromAxiosResponse } from '@/utils/download'

type CourseRow = { id: number; courseName: string; courseCode: string; description: string }

type MaterialRow = {
  id: number
  courseId: number
  fileId: number
  title: string
  materialType: number
  sortNo: number
  createdAt: string
}

type FileUploadRow = {
  id: number
  originalName: string
  contentType: string
  fileExt: string
  fileSize: number
  sha256: string
}

const route = useRoute()
const isCourseScoped = computed(() => !!route.params.courseId)

const courses = ref<CourseRow[]>([])
const selectedCourseId = ref<number | undefined>(undefined)

const rows = ref<MaterialRow[]>([])
const loading = ref(false)

const showCreate = ref(false)
const creating = ref(false)
const uploading = ref(false)

const selectedFile = ref<File | null>(null)

const createForm = reactive({
  courseId: undefined as number | undefined,
  fileId: undefined as number | undefined,
  title: '',
  materialType: 1,
  sortNo: 0
})

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
    const resp = await http.get<ApiResponse<MaterialRow[]>>('/api/teacher/materials', {
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
  createForm.materialType = 1
  createForm.sortNo = 0
  createForm.fileId = undefined
  selectedFile.value = null
  showCreate.value = true
}

function onFileChange(file: any) {
  selectedFile.value = file.raw as File
  createForm.fileId = undefined
}

function onFileRemove() {
  selectedFile.value = null
  createForm.fileId = undefined
}

async function upload() {
  if (!selectedFile.value) {
    ElMessage.error('请先选择文件')
    return
  }
  uploading.value = true
  try {
    const form = new FormData()
    form.append('file', selectedFile.value)
    const resp = await http.post<ApiResponse<FileUploadRow>>('/api/teacher/files/upload', form, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    createForm.fileId = resp.data.data.id
    ElMessage.success(`上传成功，fileId=${createForm.fileId}`)
  } finally {
    uploading.value = false
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
  if (!createForm.fileId) {
    ElMessage.error('请先上传文件获取 fileId')
    return
  }

  creating.value = true
  try {
    await http.post<ApiResponse<number>>('/api/teacher/materials', {
      courseId: createForm.courseId,
      fileId: createForm.fileId,
      title: createForm.title,
      materialType: createForm.materialType,
      sortNo: createForm.sortNo
    })
    ElMessage.success('已创建')
    showCreate.value = false
    selectedCourseId.value = createForm.courseId
    await load()
  } finally {
    creating.value = false
  }
}

async function download(row: MaterialRow) {
  const resp = await http.get<Blob>(`/api/teacher/materials/${row.id}/file`, { responseType: 'blob' })
  downloadFromAxiosResponse(resp as any, `${row.title}.bin`)
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
