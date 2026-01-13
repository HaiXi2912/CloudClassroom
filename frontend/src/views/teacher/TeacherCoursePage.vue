<template>
  <div class="cc-page">
    <div class="board">
      <div class="board-head">
        <div class="actions">
          <el-button type="primary" @click="createOpen = true">新建课程</el-button>
        </div>
      </div>

      <el-empty v-if="!rows.length" description="还没有课程，先创建一个吧" />

      <div v-else class="grid">
        <div v-for="c in rows" :key="c.id" class="course cc-glass">
          <div class="course-top">
            <div class="course-title">{{ c.courseName }}</div>
            <el-tag size="small" effect="plain">ID: {{ c.id }}</el-tag>
          </div>

          <div class="course-desc">{{ c.description || '（无描述）' }}</div>

          <div class="course-meta">
            <span>课程码：<b>{{ c.courseCode }}</b></span>
          </div>

          <div class="course-actions">
            <el-button size="small" @click="openInvite(c)">邀请学生</el-button>
            <el-button size="small" @click="goMembers(c.id)">班级管理</el-button>
            <el-button size="small" type="primary" @click="enter(c.id)">进入课程</el-button>
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="createOpen" title="新建课程" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="课程名">
          <el-input v-model="form.courseName" placeholder="例如：数据结构" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.description" type="textarea" :autosize="{ minRows: 3, maxRows: 6 }" placeholder="课程简要信息（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createOpen = false">取消</el-button>
        <el-button type="primary" :disabled="!form.courseName.trim()" @click="create">创建</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="inviteOpen" title="邀请学生加入" width="520px">
      <div class="invite">
        <div class="invite-row">
          <div class="invite-label">课程</div>
          <div class="invite-value">{{ invite.courseName }}</div>
        </div>
        <div class="invite-row">
          <div class="invite-label">课程码</div>
          <div class="invite-code">
            <el-input :model-value="invite.courseCode" readonly />
            <el-button type="primary" @click="copyCourseCode">复制</el-button>
          </div>
        </div>
        <div class="invite-tip">让学生进入“我的课程”页面，输入课程码即可加入。</div>
      </div>
      <template #footer>
        <el-button @click="inviteOpen = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { http } from '@/api/http'
import type { ApiResponse } from '@/types/api'

type CourseRow = {
  id: number
  courseName: string
  courseCode: string
  description: string
}

const form = reactive({
  courseName: '',
  description: ''
})

const createOpen = ref(false)
const inviteOpen = ref(false)
const invite = reactive({
  courseName: '',
  courseCode: ''
})

const rows = ref<CourseRow[]>([])
const router = useRouter()

async function load() {
  const resp = await http.get<ApiResponse<CourseRow[]>>('/api/teacher/courses')
  rows.value = resp.data.data
}

async function create() {
  await http.post<ApiResponse<CourseRow>>('/api/teacher/courses', {
    courseName: form.courseName.trim(),
    description: form.description
  })
  form.courseName = ''
  form.description = ''
  createOpen.value = false
  await load()
}

function enter(courseId: number) {
  router.push(`/teacher/courses/${courseId}`)
}

function goMembers(courseId: number) {
  router.push(`/teacher/courses/${courseId}/members`)
}

function openInvite(c: CourseRow) {
  invite.courseName = c.courseName
  invite.courseCode = c.courseCode
  inviteOpen.value = true
}

async function copyCourseCode() {
  try {
    await navigator.clipboard.writeText(invite.courseCode)
    ElMessage.success('已复制课程码')
  } catch {
    ElMessage.info('复制失败，请手动复制')
  }
}

onMounted(load)
</script>

<style scoped>

.board {
  padding: 0;
}

.board-head {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 10px;
}

.actions {
  display: flex;
  gap: 8px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.course {
  padding: 14px;
  user-select: none;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: var(--el-border-radius-base);
  background: color-mix(in srgb, var(--el-bg-color) 72%, transparent);
}

.course-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.course-title {
  font-weight: 900;
}

.course-desc {
  margin-top: 8px;
  color: var(--el-text-color-secondary);
  line-height: 1.5;
  min-height: 42px;
}

.course-meta {
  margin-top: 10px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.course-actions {
  margin-top: 12px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.invite {
  display: grid;
  gap: 10px;
}

.invite-row {
  display: grid;
  grid-template-columns: 70px 1fr;
  gap: 12px;
  align-items: center;
}

.invite-label {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.invite-code {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 10px;
}

.invite-tip {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

@media (max-width: 980px) {
  .grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
