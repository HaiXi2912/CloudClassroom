<template>
  <div class="cc-center">
    <el-card class="login-card">
      <template #header>
        <div style="font-weight: 600">登录</div>
      </template>

      <el-form :model="form" label-width="80px" @submit.prevent>
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="onLogin">登录</el-button>
          <el-button link @click="showRegister = true">没有账号？注册</el-button>
        </el-form-item>
      </el-form>

      <el-dialog v-model="showRegister" title="注册" width="420px">
        <el-form :model="reg" label-width="80px">
          <el-form-item label="用户名">
            <el-input v-model="reg.username" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="reg.password" type="password" show-password />
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="reg.nickname" />
          </el-form-item>
          <el-form-item label="角色">
            <el-select v-model="reg.role" style="width: 100%">
              <el-option label="学生" value="STUDENT" />
              <el-option label="老师" value="TEACHER" />
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showRegister = false">取消</el-button>
          <el-button type="primary" :loading="regLoading" @click="onRegister">注册</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { loginApi, registerApi } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const loading = ref(false)
const form = reactive({
  username: '',
  password: ''
})

const showRegister = ref(false)
const regLoading = ref(false)
const reg = reactive({
  username: '',
  password: '',
  nickname: '',
  role: 'STUDENT' as 'STUDENT' | 'TEACHER'
})

async function onLogin() {
  loading.value = true
  try {
    const data = await loginApi(form.username, form.password)
    auth.setLogin(data)

    const redirect = (route.query.redirect as string) || ''
    if (redirect && redirect !== '/login') {
      await router.push(redirect)
      return
    }

    if (auth.role === 'ADMIN') {
      await router.push('/admin/users')
    } else if (auth.role === 'TEACHER') {
      await router.push('/teacher/courses')
    } else {
      await router.push('/student/courses')
    }
  } finally {
    loading.value = false
  }
}

async function onRegister() {
  regLoading.value = true
  try {
    await registerApi(reg.username, reg.password, reg.nickname, reg.role)
    ElMessage.success('注册成功，请登录')
    showRegister.value = false
  } finally {
    regLoading.value = false
  }
}
</script>

<style scoped>
.login-card {
  width: min(420px, calc(100vw - 24px));
}
</style>
