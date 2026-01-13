import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

import LoginPage from '@/views/LoginPage.vue'
import MainLayout from '@/layouts/MainLayout.vue'
import StudentCourseLayout from '@/layouts/StudentCourseLayout.vue'
import TeacherCourseLayout from '@/layouts/TeacherCourseLayout.vue'

import HomePage from '@/views/HomePage.vue'
import ChatPage from '@/views/chat/ChatPage.vue'

import AdminUserPage from '@/views/admin/AdminUserPage.vue'
import TeacherCoursePage from '@/views/teacher/TeacherCoursePage.vue'

import TeacherAssignmentPage from '@/views/teacher/TeacherAssignmentPage.vue'
import TeacherAssignmentSubmissionsPage from '@/views/teacher/TeacherAssignmentSubmissionsPage.vue'
import TeacherMaterialPage from '@/views/teacher/TeacherMaterialPage.vue'
import TeacherExamPage from '@/views/teacher/TeacherExamPage.vue'
import TeacherQuestionBankPage from '@/views/teacher/TeacherQuestionBankPage.vue'
import TeacherCourseMembersPage from '@/views/teacher/TeacherCourseMembersPage.vue'

import TeacherTaskPage from '@/views/teacher/TeacherTaskPage.vue'
import TeacherTaskDetailPage from '@/views/teacher/TeacherTaskDetailPage.vue'
import TeacherGradePage from '@/views/teacher/TeacherGradePage.vue'

import StudentCoursePage from '@/views/student/StudentCoursePage.vue'
import StudentAssignmentPage from '@/views/student/StudentAssignmentPage.vue'
import StudentAssignmentDoPage from '@/views/student/StudentAssignmentDoPage.vue'
import StudentMaterialPage from '@/views/student/StudentMaterialPage.vue'
import StudentMaterialReadPage from '@/views/student/StudentMaterialReadPage.vue'
import StudentExamPage from '@/views/student/StudentExamPage.vue'
import StudentExamDoPage from '@/views/student/StudentExamDoPage.vue'

import StudentTaskPage from '@/views/student/StudentTaskPage.vue'
import StudentTaskDetailPage from '@/views/student/StudentTaskDetailPage.vue'

function homeByRole(role: string | undefined) {
  if (role === 'ADMIN') return '/admin/users'
  if (role === 'TEACHER') return '/teacher/courses'
  if (role === 'STUDENT') return '/student/courses'
  return '/home'
}

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: LoginPage, meta: { public: true } },
    {
      path: '/',
      component: MainLayout,
      children: [
        { path: '', redirect: '/home' },
        { path: 'home', component: HomePage },
        { path: 'chat', component: ChatPage },
        { path: 'admin/users', component: AdminUserPage, meta: { role: 'ADMIN' } },
        { path: 'teacher/courses', component: TeacherCoursePage, meta: { role: 'TEACHER' } },
        {
          path: 'teacher/courses/:courseId',
          component: TeacherCourseLayout,
          meta: { role: 'TEACHER' },
          children: [
            { path: '', redirect: (to) => `/teacher/courses/${to.params.courseId}/tasks` },
            { path: 'members', component: TeacherCourseMembersPage },
            { path: 'tasks', component: TeacherTaskPage },
            { path: 'assignments', component: TeacherAssignmentPage },
            { path: 'materials', component: TeacherMaterialPage },
            { path: 'exams', component: TeacherExamPage },
            { path: 'question-bank', component: TeacherQuestionBankPage },
            { path: 'grades', component: TeacherGradePage }
          ]
        },
        // 保留原有老师全局入口（兼容旧链接）
        { path: 'teacher/assignments', component: TeacherAssignmentPage, meta: { role: 'TEACHER' } },
        {
          path: 'teacher/assignments/:assignmentId/submissions',
          component: TeacherAssignmentSubmissionsPage,
          meta: { role: 'TEACHER' }
        },
        { path: 'teacher/materials', component: TeacherMaterialPage, meta: { role: 'TEACHER' } },
        { path: 'teacher/exams', component: TeacherExamPage, meta: { role: 'TEACHER' } },
        { path: 'teacher/tasks', component: TeacherTaskPage, meta: { role: 'TEACHER' } },
        { path: 'teacher/tasks/:taskId', component: TeacherTaskDetailPage, meta: { role: 'TEACHER' } },
        { path: 'teacher/grades', component: TeacherGradePage, meta: { role: 'TEACHER' } },

        { path: 'student/courses', component: StudentCoursePage, meta: { role: 'STUDENT' } },
        {
          path: 'student/courses/:courseId',
          component: StudentCourseLayout,
          meta: { role: 'STUDENT' },
          children: [
            { path: '', redirect: (to) => `/student/courses/${to.params.courseId}/tasks` },
            { path: 'tasks', component: StudentTaskPage }
          ]
        },

        // 保留原有学生全局入口（兼容旧链接）
        { path: 'student/assignments', component: StudentAssignmentPage, meta: { role: 'STUDENT' } },
        { path: 'student/assignments/:assignmentId', component: StudentAssignmentDoPage, meta: { role: 'STUDENT' } },
        { path: 'student/materials', component: StudentMaterialPage, meta: { role: 'STUDENT' } },
        { path: 'student/materials/:materialId/read', component: StudentMaterialReadPage, meta: { role: 'STUDENT' } },
        { path: 'student/exams', component: StudentExamPage, meta: { role: 'STUDENT' } },
        { path: 'student/exams/:examId', component: StudentExamDoPage, meta: { role: 'STUDENT' } },
        { path: 'student/tasks', component: StudentTaskPage, meta: { role: 'STUDENT' } },
        { path: 'student/tasks/:taskId', component: StudentTaskDetailPage, meta: { role: 'STUDENT' } },
        
      ]
    }
  ]
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.public) return true
  if (!auth.token) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }

  // 已登录访问根路径：按角色去各自首页
  if (to.path === '/' || to.path === '') {
    return homeByRole(auth.role)
  }

  const requiredRole = to.meta.role as string | undefined
  if (requiredRole && auth.role && auth.role !== requiredRole) {
    return homeByRole(auth.role)
  }
  return true
})

export default router
