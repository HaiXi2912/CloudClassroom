<template>
  <el-card>
    <template #header>
      <div style="font-weight: 600">用户管理</div>
    </template>

    <div style="display:flex; gap: 12px; margin-bottom: 12px">
      <el-input v-model="q.username" placeholder="用户名" style="width: 200px" />
      <el-select v-model="q.status" placeholder="状态" clearable style="width: 140px">
        <el-option label="启用" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-select v-model="q.roleCode" placeholder="角色" clearable style="width: 140px">
        <el-option label="管理员" value="ADMIN" />
        <el-option label="老师" value="TEACHER" />
        <el-option label="学生" value="STUDENT" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
    </div>

    <el-table :data="rows" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="roleCode" label="角色" width="110" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="scope">
          <el-button size="small" @click="toggle(scope.row)">切换状态</el-button>
          <el-button size="small" type="warning" @click="resetPwd(scope.row)">重置密码</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="display:flex; justify-content:flex-end; margin-top: 12px">
      <el-pagination
        layout="prev, pager, next"
        :current-page="pageNo"
        :page-size="pageSize"
        :total="total"
        @current-change="onPage"
      />
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { http } from '@/api/http'
import type { ApiResponse } from '@/types/api'

type AdminUserRow = {
  id: number
  username: string
  nickname: string
  status: number
  roleCode: string
}

type PageResult<T> = {
  total: number
  pageNo: number
  pageSize: number
  records: T[]
}

const q = reactive({
  username: '',
  status: undefined as number | undefined,
  roleCode: ''
})

const rows = ref<AdminUserRow[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(10)

async function load() {
  const resp = await http.get<ApiResponse<PageResult<AdminUserRow>>>('/api/admin/users', {
    params: {
      pageNo: pageNo.value,
      pageSize: pageSize.value,
      username: q.username || undefined,
      status: q.status,
      roleCode: q.roleCode || undefined
    }
  })
  rows.value = resp.data.data.records
  total.value = resp.data.data.total
}

async function toggle(row: AdminUserRow) {
  const next = row.status === 1 ? 0 : 1
  await http.post<ApiResponse<void>>(`/api/admin/users/${row.id}/status`, { status: next })
  await load()
}

async function resetPwd(row: AdminUserRow) {
  await http.post<ApiResponse<void>>(`/api/admin/users/${row.id}/reset-password`)
}

function onPage(p: number) {
  pageNo.value = p
  load()
}

onMounted(load)
</script>
