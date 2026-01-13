<template>
  <div class="cc-page">
    <div class="board">
      <div class="board-head">
        <div class="actions">
          <el-button type="primary" @click="joinOpen = true">加入课程</el-button>
        </div>
      </div>

      <el-empty v-if="!rows.length" description="还没有加入任何课程" />

      <div v-else class="grid">
        <div v-for="c in rows" :key="c.id" class="course">
          <div class="course-top">
            <div class="course-title">{{ c.courseName }}</div>
            <el-tag size="small" effect="plain">ID: {{ c.id }}</el-tag>
          </div>
          <div class="course-desc">{{ c.description || '（无描述）' }}</div>
          <div class="course-actions">
            <el-button size="small" type="primary" @click="enter(c.id)">进入课程</el-button>
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="joinOpen" title="加入课程" width="520px">
      <el-form :model="join" label-width="90px">
        <el-form-item label="课程码">
          <el-input v-model="join.courseCode" placeholder="请输入课程码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="joinOpen = false">取消</el-button>
        <el-button type="primary" :disabled="!join.courseCode.trim()" @click="doJoin">加入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { http } from '@/api/http'
import type { ApiResponse } from '@/types/api'

type CourseRow = {
  id: number
  courseName: string
  description: string
}

const router = useRouter()
const rows = ref<CourseRow[]>([])

const joinOpen = ref(false)

const join = reactive({
  courseCode: ''
})

async function load() {
  const resp = await http.get<ApiResponse<CourseRow[]>>('/api/student/courses')
  rows.value = resp.data.data
}

async function doJoin() {
  await http.post<ApiResponse<void>>('/api/student/courses/join', { courseCode: join.courseCode.trim() })
  join.courseCode = ''
  joinOpen.value = false
  await load()
}

function enter(courseId: number) {
  router.push(`/student/courses/${courseId}`)
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
  background: var(--el-bg-color);
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

.course-actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
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
