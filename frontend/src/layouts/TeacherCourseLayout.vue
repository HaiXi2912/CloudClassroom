<template>
  <div class="cc-page">
    <div class="shell">
      <div class="sider">
        <el-menu router :default-active="active">
          <el-menu-item :index="`/teacher/courses/${courseId}/members`">班级管理</el-menu-item>
          <el-menu-item :index="`/teacher/courses/${courseId}/tasks`">章节</el-menu-item>
          <el-menu-item :index="`/teacher/courses/${courseId}/assignments`">作业</el-menu-item>
          <el-menu-item :index="`/teacher/courses/${courseId}/materials`">资料</el-menu-item>
          <el-menu-item :index="`/teacher/courses/${courseId}/exams`">考试</el-menu-item>
          <el-menu-item :index="`/teacher/courses/${courseId}/question-bank`">题库</el-menu-item>
          <el-menu-item :index="`/teacher/courses/${courseId}/grades`">成绩</el-menu-item>
        </el-menu>
      </div>

      <div class="content">
        <RouterView />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const courseId = computed(() => Number(route.params.courseId))
const active = computed(() => route.path)
</script>

<style scoped>
.shell {
  display: grid;
  grid-template-columns: 220px 1fr;
  gap: 12px;
  align-items: start;
}

.sider {
  position: sticky;
  top: 0;
  background: transparent;
}

.sider :deep(.el-menu) {
  border-right: none;
  background: transparent;
}

.content {
  min-width: 0;
  display: grid;
  gap: 12px;
}

@media (max-width: 980px) {
  .shell {
    grid-template-columns: 1fr;
  }

  .sider {
    position: static;
  }
}
</style>
