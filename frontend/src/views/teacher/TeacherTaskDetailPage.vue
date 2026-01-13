<template>
  <div class="cc-page">
    <div class="cc-page-head">
      <div class="cc-page-title">章节</div>
      <div class="cc-page-actions">
        <el-button size="small" @click="back">返回</el-button>
      </div>
    </div>

    <div class="layout">
      <div class="main">
        <div class="panel cc-glass">
          <el-tabs v-model="activeTab" class="tabs">
            <el-tab-pane label="章节内容" name="content">
              <div v-if="!selectedStep" class="empty">
                <el-empty description="请先在右侧大纲中选择一个章节" />
              </div>

              <div v-else class="editor">
                <el-input v-model="editTitle" placeholder="标题" />

                <div v-if="selectedStep.stepType === 1" class="node-tip">
                  说明：节点用于大纲分组（可折叠），不承载正文/附件。
                </div>

                <template v-else>
                  <div class="rich">
                    <Toolbar :editor="editorRef" :defaultConfig="toolbarConfig" mode="default" />
                    <Editor v-model="editContent" class="rich-editor" :defaultConfig="editorConfig" mode="default" @onCreated="handleEditorCreated" />
                  </div>
                </template>

                <div class="editor-actions">
                  <div class="editor-meta">排序 {{ selectedStep.sortNo }} · ID {{ selectedStep.id }}</div>

                  <el-select
                    v-if="selectedStep.stepType !== 1"
                    v-model="editParentId"
                    placeholder="所属节点"
                    style="width: 200px"
                    clearable
                  >
                    <el-option :label="'未分组'" :value="null" />
                    <el-option v-for="n in nodeSteps" :key="n.id" :label="n.title" :value="n.id" />
                  </el-select>

                  <el-button type="primary" :disabled="!editTitle" @click="saveStep">保存</el-button>
                </div>
              </div>
            </el-tab-pane>

            <el-tab-pane label="章节附件" name="attachments">
              <div v-if="!selectedStep" class="empty">
                <el-empty description="请先选择章节后再管理附件" />
              </div>
              <div v-else-if="selectedStep.stepType === 1" class="empty">
                <el-empty description="节点不支持附件" />
              </div>

              <template v-else>
                <div class="attach-form">
                  <el-select v-model="attachForm.kind" placeholder="类型" style="width: 120px">
                    <el-option label="PDF" :value="1" />
                    <el-option label="PPT" :value="2" />
                    <el-option label="视频" :value="3" />
                  </el-select>
                  <el-input v-model="attachForm.title" placeholder="附件标题" style="min-width: 180px" />
                  <el-upload
                    :auto-upload="false"
                    :limit="1"
                    :on-change="onAttachFileChange"
                    :on-remove="onAttachFileRemove"
                  >
                    <el-button>选择文件</el-button>
                  </el-upload>
                  <el-button :loading="uploadingAttach" :disabled="!attachFile" @click="uploadAttachFile">上传获取 fileId</el-button>
                  <el-button
                    type="primary"
                    :loading="creatingAttach"
                    :disabled="!attachForm.kind || !attachForm.title.trim() || (!attachForm.fileId && !attachForm.url?.trim())"
                    @click="createAttachment"
                  >
                    绑定到章节
                  </el-button>
                </div>

                <div class="attach-tip">说明：先上传文件得到 fileId，再“绑定到章节”。</div>

                <div v-if="!attachments.length" class="empty">
                  <el-empty description="暂无附件" />
                </div>

                <div v-else class="attach-list">
                  <div v-for="a in attachments" :key="a.id" class="attach-item">
                    <div class="attach-left">
                      <el-tag size="small" effect="plain">{{ a.kind === 1 ? 'PDF' : a.kind === 2 ? 'PPT' : '视频' }}</el-tag>
                      <div class="attach-title">{{ a.title }}</div>
                      <div class="attach-meta">{{ a.originalName || (a.url ? '外链' : '') }}</div>
                    </div>
                    <div class="attach-actions">
                      <el-button size="small" @click="downloadAttachment(a)">下载</el-button>
                      <el-button size="small" type="danger" @click="deleteAttachment(a)">删除</el-button>
                    </div>
                  </div>
                </div>
              </template>
            </el-tab-pane>

            <el-tab-pane label="关联作业/考试" name="links">
              <div class="link-form">
                <el-select v-model="linkType" placeholder="类型" style="width: 140px">
                  <el-option label="作业" :value="1" />
                  <el-option label="考试" :value="2" />
                </el-select>
                <el-input v-model.number="refId" placeholder="关联对象ID" style="width: 180px" />
                <el-select v-model="stepId" placeholder="挂到某个内容章节（可选）" style="width: 240px" clearable>
                  <el-option v-for="s in contentSteps" :key="s.id" :label="stepLabel(s)" :value="s.id" />
                </el-select>
                <el-button type="primary" :disabled="!linkType || !refId" @click="createLink">创建关联</el-button>
              </div>

              <div v-if="!links.length" class="empty">
                <el-empty description="暂无关联" />
              </div>
              <div v-else class="link-list">
                <div v-for="l in links" :key="l.id" class="link-item">
                  <el-tag size="small" effect="plain">{{ l.linkType === 1 ? '作业' : l.linkType === 2 ? '考试' : '其他' }}</el-tag>
                  <div class="link-title">{{ l.refTitle || `ID:${l.refId}` }}</div>
                  <div class="link-meta">{{ l.stepId ? `章节 ${l.stepId}` : '挂任务' }}</div>
                  <el-button size="small" type="danger" @click="delLink(l.id)">删除</el-button>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>

      <div class="outline">
        <div class="panel cc-glass">
          <div class="panel-title">大纲</div>

          <div class="outline-create">
            <el-select v-model="newStepType" style="width: 120px">
              <el-option label="节点" :value="1" />
              <el-option label="内容" :value="2" />
            </el-select>
            <el-select
              v-if="newStepType === 2"
              v-model="newParentId"
              placeholder="所属节点（可选）"
              style="min-width: 160px"
              clearable
            >
              <el-option :label="'未分组'" :value="null" />
              <el-option v-for="n in nodeSteps" :key="n.id" :label="n.title" :value="n.id" />
            </el-select>
            <el-input v-model="newTitle" placeholder="标题" />
            <el-button type="primary" :disabled="!newTitle" @click="createStep">添加</el-button>
          </div>

          <div v-if="!steps.length" class="empty">
            <el-empty description="暂无章节" />
          </div>

          <div v-else class="outline-tree">
            <template v-if="ungroupedContents.length">
              <div class="outline-node" @click="toggleUngrouped">
                <div class="outline-left">
                  <div class="outline-arrow">{{ ungroupedExpanded ? '▼' : '▶' }}</div>
                  <div class="outline-title">未分组</div>
                </div>
              </div>
              <div v-if="ungroupedExpanded" class="outline-children">
                <div
                  v-for="(c, idx) in ungroupedContents"
                  :key="c.id"
                  class="outline-item"
                  :class="{ active: c.id === selectedStepId }"
                  @click="selectStep(c.id)"
                >
                  <div class="outline-left">
                    <div class="outline-idx">{{ idx + 1 }}</div>
                    <div class="outline-title">{{ c.title }}</div>
                  </div>
                  <div class="outline-actions" @click.stop>
                    <el-button size="small" :disabled="idx === 0" @click="moveWithinSiblings(c, -1)">上移</el-button>
                    <el-button size="small" :disabled="idx === ungroupedContents.length - 1" @click="moveWithinSiblings(c, 1)">下移</el-button>
                    <el-button size="small" type="danger" plain @click="deleteStep(c)">删除</el-button>
                  </div>
                </div>
              </div>
            </template>

            <template v-for="(n, nIdx) in nodeSteps" :key="n.id">
              <div
                class="outline-node"
                :class="{ active: n.id === selectedStepId }"
                @click="selectStep(n.id)"
              >
                <div class="outline-left">
                  <div class="outline-arrow" @click.stop="toggleNode(n.id)">{{ isNodeExpanded(n.id) ? '▼' : '▶' }}</div>
                  <div class="outline-title">{{ n.title }}</div>
                </div>
                <div class="outline-actions" @click.stop>
                  <el-button size="small" :disabled="nIdx === 0" @click="moveWithinSiblings(n, -1)">上移</el-button>
                  <el-button size="small" :disabled="nIdx === nodeSteps.length - 1" @click="moveWithinSiblings(n, 1)">下移</el-button>
                  <el-button size="small" type="danger" plain @click="deleteStep(n)">删除</el-button>
                </div>
              </div>

              <div v-if="isNodeExpanded(n.id)" class="outline-children">
                <div
                  v-for="(c, idx) in childrenMap.get(n.id) || []"
                  :key="c.id"
                  class="outline-item"
                  :class="{ active: c.id === selectedStepId }"
                  @click="selectStep(c.id)"
                >
                  <div class="outline-left">
                    <div class="outline-idx">{{ idx + 1 }}</div>
                    <div class="outline-title">{{ c.title }}</div>
                  </div>
                  <div class="outline-actions" @click.stop>
                    <el-button size="small" :disabled="idx === 0" @click="moveWithinSiblings(c, -1)">上移</el-button>
                    <el-button size="small" :disabled="idx === (childrenMap.get(n.id) || []).length - 1" @click="moveWithinSiblings(c, 1)">下移</el-button>
                    <el-button size="small" type="danger" plain @click="deleteStep(c)">删除</el-button>
                  </div>
                </div>
              </div>
            </template>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref, shallowRef, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { http } from '@/api/http'
import type { ApiResponse } from '@/types/api'
import { downloadFromAxiosResponse } from '@/utils/download'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import type { IDomEditor, IEditorConfig, IToolbarConfig } from '@wangeditor/editor'

type StepRow = {
  id: number
  taskId: number
  stepType: number
  parentId: number | null
  title: string
  content: string
  sortNo: number
}

type LinkRow = {
  id: number
  taskId: number
  stepId: number | null
  linkType: number
  refId: number
  refTitle: string
}

type FileUploadRow = {
  id: number
  originalName: string
  contentType: string
  fileExt: string
  fileSize: number
  sha256: string
}

type AttachmentRow = {
  id: number
  stepId: number
  kind: number
  title: string
  fileId?: number
  url?: string
  sortNo: number
  originalName?: string
  contentType?: string
  fileSize?: number
}

const route = useRoute()
const router = useRouter()

const taskId = Number(route.params.taskId)

const steps = ref<StepRow[]>([])
const links = ref<LinkRow[]>([])

const newTitle = ref('')
const newStepType = ref<number>(2)
const newParentId = ref<number | null>(null)

const selectedStepId = ref<number | null>(null)
const editTitle = ref('')
const editContent = ref('')
const editParentId = ref<number | null>(null)

const activeTab = ref<'content' | 'attachments' | 'links'>('content')

const editorRef = shallowRef<IDomEditor | null>(null)
const toolbarConfig: Partial<IToolbarConfig> = {}
const editorConfig: Partial<IEditorConfig> = {
  placeholder: '请输入章节内容…',
  MENU_CONF: {}
}

async function uploadEditorFile(file: File) {
  const form = new FormData()
  form.append('file', file)
  const res = await http.post<ApiResponse<FileUploadRow>>('/api/teacher/files/upload', form, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
  return res.data.data
}

;(editorConfig.MENU_CONF as any)['uploadImage'] = {
  customUpload: async (file: File, insertFn: (url: string, alt?: string, href?: string) => void) => {
    try {
      const row = await uploadEditorFile(file)
      const url = `/api/files/${row.id}/raw`
      insertFn(url, row.originalName, url)
      ElMessage.success('图片已上传')
    } catch (e: any) {
      ElMessage.error(e?.message || '图片上传失败')
    }
  }
}

;(editorConfig.MENU_CONF as any)['uploadVideo'] = {
  customUpload: async (file: File, insertFn: (url: string, poster?: string) => void) => {
    try {
      const row = await uploadEditorFile(file)
      const url = `/api/files/${row.id}/raw`
      insertFn(url)
      ElMessage.success('资源已上传')
    } catch (e: any) {
      ElMessage.error(e?.message || '资源上传失败')
    }
  }
}

;(editorConfig.MENU_CONF as any)['uploadAttachment'] = {
  customUpload: async (file: File, insertFn: (url: string, fileName?: string) => void) => {
    try {
      const row = await uploadEditorFile(file)
      const url = `/api/files/${row.id}/raw`
      insertFn(url, row.originalName)
      ElMessage.success('资源已上传')
    } catch (e: any) {
      ElMessage.error(e?.message || '资源上传失败')
    }
  }
}

const attachments = ref<AttachmentRow[]>([])
const attachFile = ref<File | null>(null)
const uploadingAttach = ref(false)
const creatingAttach = ref(false)

const attachForm = reactive({
  kind: 1 as number,
  title: '',
  fileId: undefined as number | undefined,
  url: ''
})

const linkType = ref<number | null>(null)
const refId = ref<number>(0)
const stepId = ref<number | null>(null)

const selectedStep = computed(() => steps.value.find(s => s.id === selectedStepId.value) || null)

const nodeSteps = computed(() =>
  steps.value
    .filter(s => s.stepType === 1 && s.parentId == null)
    .slice()
    .sort((a, b) => (a.sortNo ?? 0) - (b.sortNo ?? 0) || a.id - b.id)
)

const contentSteps = computed(() =>
  steps.value
    .filter(s => s.stepType !== 1)
    .slice()
    .sort((a, b) => (a.sortNo ?? 0) - (b.sortNo ?? 0) || a.id - b.id)
)

const ungroupedContents = computed(() =>
  steps.value
    .filter(s => s.stepType !== 1 && (s.parentId == null))
    .slice()
    .sort((a, b) => (a.sortNo ?? 0) - (b.sortNo ?? 0) || a.id - b.id)
)

const childrenMap = computed(() => {
  const map = new Map<number, StepRow[]>()
  for (const c of steps.value.filter(s => s.stepType !== 1 && s.parentId != null)) {
    const pid = c.parentId as number
    if (!map.has(pid)) map.set(pid, [])
    map.get(pid)!.push(c)
  }
  for (const [k, arr] of map.entries()) {
    arr.sort((a, b) => (a.sortNo ?? 0) - (b.sortNo ?? 0) || a.id - b.id)
    map.set(k, arr)
  }
  return map
})

const expandedNodeIds = ref<number[]>([])
const ungroupedExpanded = ref(true)

function isNodeExpanded(id: number) {
  return expandedNodeIds.value.includes(id)
}

function toggleNode(id: number) {
  if (isNodeExpanded(id)) {
    expandedNodeIds.value = expandedNodeIds.value.filter(x => x !== id)
  } else {
    expandedNodeIds.value = [...expandedNodeIds.value, id]
  }
}

function toggleUngrouped() {
  ungroupedExpanded.value = !ungroupedExpanded.value
}

function stepLabel(s: StepRow) {
  const pid = s.parentId
  if (!pid) {
    return `${s.id} - ${s.title}`
  }
  const p = nodeSteps.value.find(n => n.id === pid)
  return p ? `${p.title} / ${s.id} - ${s.title}` : `${s.id} - ${s.title}`
}

function handleEditorCreated(editor: IDomEditor) {
  editorRef.value = editor
}

async function load() {
  const s = await http.get<ApiResponse<StepRow[]>>(`/api/teacher/tasks/${taskId}/steps`)
  steps.value = s.data.data

  if (!expandedNodeIds.value.length) {
    expandedNodeIds.value = steps.value
      .filter(x => x.stepType === 1)
      .map(x => x.id)
  }

  if (steps.value.length) {
    const exists = selectedStepId.value && steps.value.some(x => x.id === selectedStepId.value)
    if (!exists) {
      const firstContent = steps.value.find(x => x.stepType !== 1)
      selectedStepId.value = (firstContent || steps.value[0]).id
    }
  } else {
    selectedStepId.value = null
  }

  syncEditorFromSelected()

  if (selectedStepId.value) {
    const cur = steps.value.find(x => x.id === selectedStepId.value)
    if (cur && cur.stepType !== 1) {
      await loadAttachments(selectedStepId.value)
    } else {
      attachments.value = []
    }
  } else {
    attachments.value = []
  }

  const l = await http.get<ApiResponse<LinkRow[]>>(`/api/teacher/tasks/${taskId}/links`)
  links.value = l.data.data
}

async function deleteStep(step: StepRow) {
  const label = step.stepType === 1 ? '节点' : '章节'
  try {
    await ElMessageBox.confirm(`确认删除${label}「${step.title}」？`, '提示', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
  } catch {
    return
  }

  await http.delete<ApiResponse<void>>(`/api/teacher/tasks/steps/${step.id}`)
  ElMessage.success('已删除')
  if (selectedStepId.value === step.id) {
    selectedStepId.value = null
  }
  await load()
}

function selectStep(id: number) {
  selectedStepId.value = id
  syncEditorFromSelected()
}

function syncEditorFromSelected() {
  if (!selectedStep.value) {
    editTitle.value = ''
    editContent.value = ''
    editParentId.value = null
    return
  }
  editTitle.value = selectedStep.value.title || ''
  editContent.value = selectedStep.value.content || ''
  editParentId.value = selectedStep.value.stepType === 1 ? null : (selectedStep.value.parentId ?? null)
}

async function createStep() {
  await http.post<ApiResponse<number>>(`/api/teacher/tasks/${taskId}/steps`, {
    title: newTitle.value,
    content: '',
    stepType: newStepType.value,
    parentId: newStepType.value === 2 ? newParentId.value : null
  })
  newTitle.value = ''
  newParentId.value = null
  await load()
}

async function saveStep() {
  if (!selectedStep.value) return
  await http.put<ApiResponse<void>>(`/api/teacher/tasks/steps/${selectedStep.value.id}`, {
    title: editTitle.value,
    content: selectedStep.value.stepType === 1 ? '' : editContent.value,
    parentId: selectedStep.value.stepType === 1 ? null : editParentId.value
  })
  await load()
}

async function loadAttachments(stepId: number) {
  const resp = await http.get<ApiResponse<AttachmentRow[]>>(`/api/teacher/tasks/steps/${stepId}/attachments`)
  attachments.value = resp.data.data || []
}

function onAttachFileChange(file: any) {
  attachFile.value = file.raw as File
  attachForm.fileId = undefined
}

function onAttachFileRemove() {
  attachFile.value = null
  attachForm.fileId = undefined
}

async function uploadAttachFile() {
  if (!attachFile.value) {
    ElMessage.error('请先选择文件')
    return
  }
  uploadingAttach.value = true
  try {
    const form = new FormData()
    form.append('file', attachFile.value)
    const resp = await http.post<ApiResponse<FileUploadRow>>('/api/teacher/files/upload', form, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    attachForm.fileId = resp.data.data.id
    ElMessage.success(`上传成功，fileId=${attachForm.fileId}`)
  } finally {
    uploadingAttach.value = false
  }
}

async function createAttachment() {
  if (!selectedStep.value) return
  if (!attachForm.title.trim()) {
    ElMessage.error('请输入附件标题')
    return
  }
  if (!attachForm.fileId && !attachForm.url.trim()) {
    ElMessage.error('请先上传文件或填写外链')
    return
  }

  creatingAttach.value = true
  try {
    await http.post<ApiResponse<number>>(`/api/teacher/tasks/steps/${selectedStep.value.id}/attachments`, {
      kind: attachForm.kind,
      title: attachForm.title,
      fileId: attachForm.fileId,
      url: attachForm.url || undefined
    })
    ElMessage.success('已添加附件')
    attachForm.title = ''
    attachForm.fileId = undefined
    attachForm.url = ''
    attachFile.value = null
    await loadAttachments(selectedStep.value.id)
  } finally {
    creatingAttach.value = false
  }
}

async function downloadAttachment(a: AttachmentRow) {
  const resp = await http.get<Blob>(`/api/teacher/tasks/attachments/${a.id}/file`, { responseType: 'blob' })
  downloadFromAxiosResponse(resp as any, `${a.title || 'attachment'}.bin`)
}

async function deleteAttachment(a: AttachmentRow) {
  await http.delete<ApiResponse<void>>(`/api/teacher/tasks/attachments/${a.id}`)
  if (selectedStep.value) {
    await loadAttachments(selectedStep.value.id)
  }
}

async function moveWithinSiblings(step: StepRow, dir: -1 | 1) {
  const siblings = step.stepType === 1
    ? nodeSteps.value
    : steps.value
      .filter(s => s.stepType !== 1 && (s.parentId ?? null) === (step.parentId ?? null))
      .slice()
      .sort((a, b) => (a.sortNo ?? 0) - (b.sortNo ?? 0) || a.id - b.id)

  const idx = siblings.findIndex(s => s.id === step.id)
  const other = siblings[idx + dir]
  if (idx < 0 || !other) return

  const aSort = step.sortNo ?? (idx + 1) * 10
  const bSort = other.sortNo ?? (idx + 1 + dir) * 10

  await http.put<ApiResponse<void>>(`/api/teacher/tasks/steps/${step.id}`, { sortNo: bSort })
  await http.put<ApiResponse<void>>(`/api/teacher/tasks/steps/${other.id}`, { sortNo: aSort })
  await load()
}

async function createLink() {
  await http.post<ApiResponse<number>>(`/api/teacher/tasks/${taskId}/links`, {
    linkType: linkType.value,
    refId: refId.value,
    stepId: stepId.value
  })
  refId.value = 0
  stepId.value = null
  await load()
}

async function delLink(id: number) {
  await http.delete<ApiResponse<void>>(`/api/teacher/tasks/links/${id}`)
  await load()
}

function back() {
  router.back()
}

onMounted(() => {
  load()
})

onBeforeUnmount(() => {
  const ed = editorRef.value
  if (ed) {
    ed.destroy()
    editorRef.value = null
  }
})

watch(
  () => selectedStepId.value,
  async (id) => {
    if (!id) {
      attachments.value = []
      return
    }
    const cur = steps.value.find(s => s.id === id)
    if (!cur || cur.stepType === 1) {
      attachments.value = []
      return
    }
    await loadAttachments(id)
  }
)
</script>

<style scoped>
.layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 12px;
  align-items: start;
}

.main {
  min-width: 0;
}

.outline {
  min-width: 0;
}

.panel {
  padding: 14px;
  margin-bottom: 12px;
}

.panel-title {
  font-weight: 700;
  margin-bottom: 10px;
}

.tabs :deep(.el-tabs__header) {
  margin: 0 0 10px 0;
}

.editor {
  display: grid;
  gap: 10px;
}

.rich {
  border: 1px solid var(--el-border-color-lighter);
  border-radius: var(--el-border-radius-base);
  overflow: hidden;
}

.rich-editor {
  min-height: 260px;
}

.node-tip {
  padding: 10px 12px;
  border: 1px dashed var(--el-border-color-lighter);
  border-radius: var(--el-border-radius-base);
  color: var(--el-text-color-secondary);
  background: color-mix(in srgb, var(--el-bg-color) 70%, transparent);
}

.attach-form {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 8px;
}

.attach-tip {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-bottom: 8px;
}

.attach-list {
  display: grid;
  gap: 8px;
}

.attach-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding: 10px;
  border-radius: var(--el-border-radius-base);
  border: 1px solid var(--el-border-color-lighter);
  background: color-mix(in srgb, var(--el-bg-color) 72%, transparent);
}

.attach-left {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.attach-title {
  font-weight: 700;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.attach-meta {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.attach-actions {
  display: flex;
  gap: 8px;
  flex: 0 0 auto;
}

.editor-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.editor-meta {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.link-form {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.empty {
  padding: 10px 0;
}

.outline-create {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 10px;
}

.outline-tree {
  display: grid;
  gap: 6px;
}

.outline-children {
  display: grid;
  gap: 6px;
  padding-left: 16px;
}

.outline-node {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 10px 10px;
  border-radius: var(--el-border-radius-base);
  border: 1px solid var(--el-border-color-lighter);
  cursor: pointer;
  user-select: none;
  background: color-mix(in srgb, var(--el-bg-color) 72%, transparent);
}

.outline-node.active {
  border-color: var(--el-border-color);
}

.outline-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 10px 10px;
  border-radius: var(--el-border-radius-base);
  border: 1px solid var(--el-border-color-lighter);
  cursor: pointer;
  user-select: none;
  background: color-mix(in srgb, var(--el-bg-color) 72%, transparent);
}

.outline-item.active {
  border-color: var(--el-border-color);
}

.outline-left {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.outline-arrow {
  width: 24px;
  height: 24px;
  display: grid;
  place-items: center;
  border-radius: 999px;
  border: 1px solid var(--el-border-color-lighter);
  font-weight: 800;
  flex: 0 0 auto;
}

.outline-idx {
  width: 24px;
  height: 24px;
  display: grid;
  place-items: center;
  border-radius: 999px;
  border: 1px solid var(--el-border-color-lighter);
  font-weight: 800;
  flex: 0 0 auto;
}

.outline-title {
  font-weight: 700;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.outline-actions {
  display: flex;
  gap: 6px;
  flex: 0 0 auto;
}

.link-list {
  margin-top: 12px;
  display: grid;
  gap: 8px;
}

.link-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: var(--el-border-radius-base);
  border: 1px solid var(--el-border-color-lighter);
  background: color-mix(in srgb, var(--el-bg-color) 72%, transparent);
}

.link-title {
  font-weight: 700;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.link-meta {
  margin-left: auto;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

@media (max-width: 720px) {
  .layout {
    grid-template-columns: 1fr;
  }

  .outline {
    order: -1;
  }
}
</style>
