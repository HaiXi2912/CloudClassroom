<template>
  <el-container class="layout">
    <el-header class="header">
      <div class="header-left" @click="goHome">
        <div class="brand-title">云课堂</div>
        <div class="brand-sub">CloudClassroom</div>
        <div class="divider">/</div>
        <div class="page-title">{{ pageTitle }}</div>
      </div>

      <div class="header-right">
        <el-button text class="msg-btn" @click="goChat">
          <el-icon><ChatDotRound /></el-icon>
          <span class="msg-text">消息</span>
        </el-button>

        <el-dropdown>
          <span class="user">
            <el-avatar :size="30" :src="avatarUrl || undefined">{{ avatarFallback }}</el-avatar>
            <span class="user-text">{{ displayName }}（{{ roleText }}）</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="openProfile">个人信息</el-dropdown-item>
              <el-dropdown-item @click="openPassword">修改密码</el-dropdown-item>
              <el-dropdown-item divided @click="onLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <el-main class="main">
      <RouterView />
    </el-main>
  </el-container>

  <el-dialog v-model="profileOpen" title="个人信息" width="460px">
    <el-form :model="profileForm" label-width="90px">
      <el-form-item label="昵称">
        <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
      </el-form-item>
      <el-form-item label="头像URL">
        <el-input v-model="profileForm.avatarUrl" placeholder="可选：粘贴头像图片链接" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="profileOpen = false">取消</el-button>
      <el-button type="primary" :disabled="!profileForm.nickname.trim()" @click="saveProfile">保存</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="passwordOpen" title="修改密码" width="460px">
    <el-form :model="pwdForm" label-width="90px">
      <el-form-item label="旧密码">
        <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入旧密码" />
      </el-form-item>
      <el-form-item label="新密码">
        <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="至少 6 位" />
      </el-form-item>
      <el-form-item label="确认新密码">
        <el-input v-model="pwdForm.confirm" type="password" show-password placeholder="再次输入新密码" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="passwordOpen = false">取消</el-button>
      <el-button type="primary" :disabled="!canSubmitPwd" @click="savePassword">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import { ChatDotRound } from '@element-plus/icons-vue'
import { http } from '@/api/http'
import type { ApiResponse } from '@/types/api'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const active = computed(() => route.path)
const role = computed(() => auth.role)

const roleText = computed(() => {
  if (auth.role === 'ADMIN') return '管理员'
  if (auth.role === 'TEACHER') return '老师'
  return '学生'
})

const pageTitle = computed(() => {
  const p = route.path
  if (p.startsWith('/teacher/courses/')) {
    if (p.includes('/members')) return '班级管理'
    if (p.includes('/tasks')) return '章节（任务）'
    if (p.includes('/assignments')) return '作业'
    if (p.includes('/materials')) return '资料'
    if (p.includes('/exams')) return '考试'
    if (p.includes('/grades')) return '成绩'
    return '课程'
  }
  if (p.startsWith('/teacher/courses')) return '课程管理'
  if (p.startsWith('/student/courses/')) {
    if (p.includes('/tasks')) return '章节（任务）'
    if (p.includes('/dashboard')) return '看板'
    if (p.includes('/assignments')) return '作业'
    if (p.includes('/materials')) return '资料'
    if (p.includes('/exams')) return '考试'
    return '课程'
  }
  if (p.startsWith('/student/courses')) return '我的课程'
  if (p.startsWith('/admin/users')) return '用户管理'
  if (p.startsWith('/chat')) return '消息'
  return '首页'
})

function goHome() {
  router.push('/home')
}

function goChat() {
  router.push('/chat')
}

function onLogout() {
  auth.logout()
  router.push('/login')
}

type ProfileResponse = {
  userId: number
  username: string
  nickname: string
  avatarUrl: string | null
}

const profileOpen = ref(false)
const passwordOpen = ref(false)

const avatarUrl = ref<string | null>(null)

const profileForm = reactive({
  nickname: '',
  avatarUrl: ''
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirm: ''
})

const displayName = computed(() => auth.nickname || `ID:${auth.userId}`)
const avatarFallback = computed(() => (auth.nickname || String(auth.userId || '')).slice(0, 1) || 'U')

const canSubmitPwd = computed(() => {
  if (!pwdForm.oldPassword) return false
  if (!pwdForm.newPassword || pwdForm.newPassword.length < 6) return false
  if (pwdForm.newPassword !== pwdForm.confirm) return false
  return true
})

async function loadProfile() {
  const resp = await http.get<ApiResponse<ProfileResponse>>('/api/auth/profile')
  const p = resp.data.data
  if (!p) return
  avatarUrl.value = p.avatarUrl
  profileForm.nickname = p.nickname || auth.nickname || ''
  profileForm.avatarUrl = p.avatarUrl || ''

  if (p.nickname && p.nickname !== auth.nickname) {
    auth.nickname = p.nickname
    localStorage.setItem('cc_nickname', p.nickname)
  }
}

function openProfile() {
  profileOpen.value = true
}

function openPassword() {
  passwordOpen.value = true
}

async function saveProfile() {
  const nickname = profileForm.nickname.trim()
  const resp = await http.put<ApiResponse<ProfileResponse>>('/api/auth/profile', {
    nickname,
    avatarUrl: profileForm.avatarUrl ? profileForm.avatarUrl.trim() : null
  })
  const p = resp.data.data
  if (p) {
    avatarUrl.value = p.avatarUrl
    auth.nickname = p.nickname
    localStorage.setItem('cc_nickname', p.nickname)
  }
  profileOpen.value = false
  ElMessage.success('已保存')
}

async function savePassword() {
  await http.put<ApiResponse<void>>('/api/auth/password', {
    oldPassword: pwdForm.oldPassword,
    newPassword: pwdForm.newPassword
  })
  pwdForm.oldPassword = ''
  pwdForm.newPassword = ''
  pwdForm.confirm = ''
  passwordOpen.value = false
  ElMessage.success('密码已修改')
}

onMounted(loadProfile)
</script>

<style scoped>
.layout {
  height: 100vh;
  background: transparent;
}

.brand-title {
  font-weight: 800;
  font-size: 18px;
}

.brand-sub {
  margin-top: 4px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: transparent;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.divider {
  color: var(--el-text-color-secondary);
}

.page-title {
  font-weight: 700;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.msg-btn {
  display: flex;
  align-items: center;
  gap: 6px;
}

.msg-text {
  font-weight: 600;
}

.user {
  cursor: pointer;
  color: var(--el-text-color-regular);
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.user-text {
  font-weight: 600;
}

.main {
  padding: 16px;
}

.main {
  padding: 16px;
  background: transparent;
}

@media (max-width: 768px) {
  .aside {
    display: none;
  }

  .menu-btn {
    display: inline-flex;
  }

  .main {
    padding: 12px;
  }
}
</style>
