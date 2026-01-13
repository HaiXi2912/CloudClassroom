<template>
  <div class="cc-page">
    <div class="cc-page-head">
      <div class="cc-page-title">学习</div>
      <div class="cc-page-actions">
        <el-button size="small" @click="back">返回</el-button>
      </div>
    </div>

    <div class="layout">
      <div class="main">
        <div class="panel">
          <el-tabs v-model="activeTab" class="tabs">
            <el-tab-pane label="章节内容" name="content">
              <div v-if="!selectedStep" class="empty">
                <el-empty description="请先在右侧大纲中选择一个章节" />
              </div>

              <div v-else class="step">
                <div class="step-head">
                  <div class="step-left">
                    <div class="step-title">{{ selectedStep.title }}</div>
                  </div>
                  <el-checkbox
                    v-if="selectedStep.stepType !== 1"
                    :model-value="selectedStep.done === 1"
                    @change="onDoneChange(selectedStep.id, $event)"
                  />
                </div>

                <div v-if="selectedStep.stepType === 1" class="node-tip">
                  说明：节点用于大纲分组（可折叠），不承载正文/附件。
                </div>

                <template v-else>
                  <div v-if="selectedStep.content" ref="contentEl" class="content" @scroll="onContentScroll">
                    <div v-if="contentIsHtml" class="content-inner" v-html="selectedStep.content" />
                    <div v-else class="content-text">{{ selectedStep.content }}</div>
                  </div>
                  <div v-else class="content-empty">（空）</div>

                  <div v-if="study" class="study-meta">
                    <el-tag size="small" effect="plain">正文：{{ study.contentStatus === 2 ? '已学习' : '未学习' }}</el-tag>
                    <el-tag size="small" effect="plain">附件：{{ doneAttachments }}/{{ totalAttachments }}</el-tag>
                  </div>
                </template>
              </div>
            </el-tab-pane>

            <el-tab-pane label="章节附件" name="attachments">
              <div v-if="!selectedStep" class="empty">
                <el-empty description="请先选择章节" />
              </div>
              <div v-else-if="selectedStep.stepType === 1" class="empty">
                <el-empty description="节点不支持附件" />
              </div>

              <template v-else>
                <div v-if="!study || !study.attachments.length" class="empty">
                  <el-empty description="暂无附件" />
                </div>

                <div v-else class="attach-list">
                  <div v-for="a in study.attachments" :key="a.id" class="attach-item">
                    <div class="attach-left">
                      <span class="done-dot" :class="{ on: a.done === 1 }" />
                      <el-tag size="small" effect="plain">{{ a.kind === 1 ? 'PDF' : a.kind === 2 ? 'PPT' : '视频' }}</el-tag>
                      <div class="attach-title">{{ a.title }}</div>
                    </div>
                    <div class="attach-actions">
                      <el-button size="small" @click="openAttachment(a)">打开</el-button>
                      <el-button size="small" @click="downloadAttachment(a)">下载</el-button>
                      <el-button
                        v-if="a.url"
                        size="small"
                        type="primary"
                        :disabled="a.done === 1 || !a._opened"
                        @click="markAttachmentDone(a)"
                      >标记已学习</el-button>
                      <el-tag v-else size="small" effect="plain">进度：{{ a.progressPercent ?? (a.done === 1 ? 100 : 0) }}%</el-tag>
                    </div>
                  </div>
                </div>

                <div v-if="viewerUrl" class="viewer">
                  <div class="viewer-head">
                    <div class="viewer-title">{{ viewerTitle }}</div>
                    <el-button text @click="closeViewer">关闭</el-button>
                  </div>
                  <video
                    v-if="viewerKind === 3"
                    ref="videoEl"
                    class="viewer-video"
                    :src="viewerUrl"
                    controls
                    @timeupdate="onVideoTimeUpdate"
                    @ended="onVideoEnded"
                  />
                  <div v-else-if="viewerKind === 1" ref="pdfEl" class="viewer-doc" @scroll="onDocScroll">
                    <div v-if="pdfLoading" class="viewer-loading">PDF 加载中...</div>
                  </div>
                  <div v-else-if="viewerKind === 2" ref="pptEl" class="viewer-doc" @scroll="onDocScroll">
                    <VueOfficePptx :src="viewerUrl" />
                  </div>
                  <iframe v-else class="viewer-frame" :src="viewerUrl" />
                </div>
              </template>
            </el-tab-pane>

            <el-tab-pane label="关联作业/考试" name="links">
              <div v-if="!links.length" class="empty">
                <el-empty description="暂无关联" />
              </div>
              <div v-else class="link-list">
                <div v-for="l in links" :key="l.id" class="link-item">
                  <el-tag size="small" effect="plain">{{ l.linkType === 1 ? '作业' : l.linkType === 2 ? '考试' : '其他' }}</el-tag>
                  <span class="link-title">{{ l.refTitle || `ID:${l.refId}` }}</span>
                  <span class="link-meta">{{ l.stepId ? `章节 ${l.stepId}` : '挂任务' }}</span>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>

      <div class="outline">
        <div class="panel">
          <div class="panel-title">大纲</div>

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
                  @click="selectStep(c)"
                >
                  <div class="outline-left">
                    <span class="done-dot" :class="{ on: c.done === 1 }" />
                    <div class="outline-idx">{{ idx + 1 }}</div>
                    <div class="outline-title">{{ c.title }}</div>
                  </div>
                </div>
              </div>
            </template>

            <template v-for="n in nodeSteps" :key="n.id">
              <div class="outline-node" :class="{ active: n.id === selectedStepId }" @click="selectStep(n)">
                <div class="outline-left">
                  <div class="outline-arrow" @click.stop="toggleNode(n.id)">{{ isNodeExpanded(n.id) ? '▼' : '▶' }}</div>
                  <span class="done-dot" :class="{ on: isNodeDone(n.id) }" />
                  <div class="outline-title">{{ n.title }}</div>
                </div>
              </div>
              <div v-if="isNodeExpanded(n.id)" class="outline-children">
                <div
                  v-for="(c, idx) in childrenMap.get(n.id) || []"
                  :key="c.id"
                  class="outline-item"
                  :class="{ active: c.id === selectedStepId }"
                  @click="selectStep(c)"
                >
                  <div class="outline-left">
                    <span class="done-dot" :class="{ on: c.done === 1 }" />
                    <div class="outline-idx">{{ idx + 1 }}</div>
                    <div class="outline-title">{{ c.title }}</div>
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
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { http } from '@/api/http'
import type { ApiResponse } from '@/types/api'
import { downloadFromAxiosResponse } from '@/utils/download'
import * as pdfjsLib from 'pdfjs-dist'
import VueOfficePptx from '@vue-office/pptx'

type StepRow = {
  id: number
  title: string
  content: string
  stepType: number
  parentId: number | null
  sortNo: number
  done: number
}

type AttachmentRow = {
  id: number
  stepId: number
  kind: number
  title: string
  fileId?: number
  url?: string
  sortNo: number
  done: number
  progressPercent?: number
  positionSeconds?: number
  durationSeconds?: number
  _opened?: boolean
}

type StudyRow = {
  stepId: number
  contentStatus: number
  attachments: AttachmentRow[]
}

type LinkRow = {
  id: number
  stepId: number | null
  linkType: number
  refId: number
  refTitle: string
}

const route = useRoute()
const router = useRouter()

const taskId = Number(route.params.taskId)

const steps = ref<StepRow[]>([])
const links = ref<LinkRow[]>([])

const nodeSteps = computed(() =>
  steps.value
    .filter(s => s.stepType === 1 && s.parentId == null)
    .slice()
    .sort((a, b) => (a.sortNo ?? 0) - (b.sortNo ?? 0) || a.id - b.id)
)

const ungroupedContents = computed(() =>
  steps.value
    .filter(s => s.stepType !== 1 && s.parentId == null)
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

function isNodeDone(nodeId: number) {
  const children = childrenMap.value.get(nodeId) || []
  if (!children.length) return false
  return children.every(c => c.done === 1)
}

const study = ref<StudyRow | null>(null)
const contentEl = ref<HTMLElement | null>(null)

const viewerUrl = ref<string>('')
const viewerKind = ref<number>(1)
const viewerTitle = ref<string>('')
const viewerAttachmentId = ref<number | null>(null)

const activeTab = ref<'content' | 'attachments' | 'links'>('content')

const videoEl = ref<HTMLVideoElement | null>(null)
const pdfEl = ref<HTMLDivElement | null>(null)
const pptEl = ref<HTMLDivElement | null>(null)
const pdfLoading = ref(false)

let lastVideoReportAt = 0
let lastDocReportAttachmentId: number | null = null
let pdfRenderToken = 0

// pdf.js worker（Vite + ESM）
try {
  ;(pdfjsLib as any).GlobalWorkerOptions.workerSrc = new URL(
    'pdfjs-dist/build/pdf.worker.min.mjs',
    import.meta.url
  ).toString()
} catch {
  // ignore
}

const selectedStepId = ref<number | null>(null)
const selectedStep = computed(() => steps.value.find(s => s.id === selectedStepId.value) || null)

const contentIsHtml = computed(() => {
  const c = selectedStep.value?.content || ''
  return /<[^>]+>/.test(c)
})

const totalAttachments = computed(() => study.value?.attachments?.length || 0)
const doneAttachments = computed(() => (study.value?.attachments || []).filter(a => a.done === 1).length)

async function load() {
  const s = await http.get<ApiResponse<StepRow[]>>(`/api/student/tasks/${taskId}/steps`)
  steps.value = s.data.data

  if (steps.value.length) {
    if (!expandedNodeIds.value.length) {
      expandedNodeIds.value = nodeSteps.value.map(n => n.id)
    }

    const allContent = steps.value.filter(x => x.stepType !== 1)
    const exists = selectedStepId.value && allContent.some(x => x.id === selectedStepId.value)
    if (!exists) {
      selectedStepId.value = allContent.length ? allContent[0].id : null
    }
  } else {
    selectedStepId.value = null
  }

  const l = await http.get<ApiResponse<LinkRow[]>>(`/api/student/tasks/${taskId}/links`)
  links.value = l.data.data

  if (selectedStepId.value) {
    await loadStudy(selectedStepId.value)
  } else {
    study.value = null
  }
}

function selectStep(s: StepRow) {
  selectedStepId.value = s.id
}

async function loadStudy(stepId: number) {
  const resp = await http.get<ApiResponse<StudyRow>>(`/api/student/tasks/steps/${stepId}/study`)
  const data = resp.data.data
  if (data?.attachments) {
    data.attachments = data.attachments.map(a => ({ ...a, _opened: false }))
  }
  study.value = data

  // 内容不够长无法滚动时，自动完成正文
  await nextTick()
  tryAutoCompleteContent()
}

function tryAutoCompleteContent() {
  if (!selectedStep.value?.content) return
  if (!study.value || study.value.contentStatus === 2) return
  const el = contentEl.value
  if (!el) return
  const noNeedScroll = el.scrollHeight <= el.clientHeight + 2
  if (!noNeedScroll) return
  // 直接复用滚动到底逻辑
  onContentScroll()
}

function onDoneChange(stepId: number, checked: boolean) {
  return toggle(stepId, checked)
}

async function toggle(stepId: number, checked: boolean) {
  try {
    await http.post<ApiResponse<void>>(`/api/student/tasks/steps/${stepId}/done`, { done: checked ? 1 : 0 })
  } finally {
    await load()
  }
}

async function onContentScroll() {
  if (!selectedStep.value) return
  if (!study.value || study.value.contentStatus === 2) return
  const el = contentEl.value
  if (!el) return
  const nearBottom = el.scrollTop + el.clientHeight >= el.scrollHeight - 2
  if (!nearBottom) return

  await http.post<ApiResponse<void>>(`/api/student/tasks/steps/${selectedStep.value.id}/content/done`)
  await loadStudy(selectedStep.value.id)
}

function closeViewer() {
  if (viewerUrl.value) {
    try {
      URL.revokeObjectURL(viewerUrl.value)
    } catch {
      // ignore
    }
  }
  viewerUrl.value = ''
  viewerAttachmentId.value = null
  viewerTitle.value = ''
  viewerKind.value = 1

  if (pdfEl.value) {
    pdfEl.value.innerHTML = ''
  }
  pdfLoading.value = false
  lastDocReportAttachmentId = null
}

async function openAttachment(a: AttachmentRow) {
  if (!selectedStep.value) return
  if (a.url) {
    window.open(a.url, '_blank')
    a._opened = true
    ElMessage.success('已打开外链')
    return
  }
  const resp = await http.get<Blob>(`/api/student/tasks/attachments/${a.id}/file`, { responseType: 'blob' })
  const url = URL.createObjectURL(resp.data)
  closeViewer()
  viewerUrl.value = url
  viewerKind.value = a.kind
  viewerTitle.value = a.title
  viewerAttachmentId.value = a.id
  a._opened = true

  await nextTick()
  if (a.kind === 1) {
    await renderPdf(url)
  }
}

async function downloadAttachment(a: AttachmentRow) {
  if (a.url) {
    window.open(a.url, '_blank')
    a._opened = true
    return
  }
  const resp = await http.get<Blob>(`/api/student/tasks/attachments/${a.id}/file`, { responseType: 'blob' })
  downloadFromAxiosResponse(resp as any, `${a.title || 'attachment'}.bin`)
  a._opened = true
}

async function markAttachmentDone(a: AttachmentRow) {
  await http.post<ApiResponse<void>>(`/api/student/tasks/attachments/${a.id}/done`)
  if (selectedStep.value) {
    await loadStudy(selectedStep.value.id)
  }
}

async function reportAttachmentProgress(
  attachmentId: number,
  payload: { progressPercent?: number; positionSeconds?: number; durationSeconds?: number }
) {
  await http.post<ApiResponse<void>>(`/api/student/tasks/attachments/${attachmentId}/progress`, payload)
}

async function onVideoTimeUpdate() {
  const id = viewerAttachmentId.value
  const v = videoEl.value
  if (!id || !v) return

  const now = Date.now()
  if (now - lastVideoReportAt < 1200) return
  lastVideoReportAt = now

  const duration = Number.isFinite(v.duration) ? Math.floor(v.duration) : 0
  const position = Math.floor(v.currentTime || 0)
  const percent = duration > 0 ? Math.min(100, Math.max(0, Math.floor((position * 100) / duration))) : 0

  try {
    await reportAttachmentProgress(id, { progressPercent: percent, positionSeconds: position, durationSeconds: duration })
    if (percent >= 100 && selectedStep.value) {
      await loadStudy(selectedStep.value.id)
    }
  } catch {
    // ignore
  }
}

async function onVideoEnded() {
  const id = viewerAttachmentId.value
  if (!id || !selectedStep.value) return
  const v = videoEl.value
  const duration = v && Number.isFinite(v.duration) ? Math.floor(v.duration) : undefined
  const position = v ? Math.floor(v.currentTime || 0) : undefined
  await reportAttachmentProgress(id, { progressPercent: 100, positionSeconds: position, durationSeconds: duration })
  await loadStudy(selectedStep.value.id)
}

async function onDocScroll() {
  const id = viewerAttachmentId.value
  if (!id || !selectedStep.value) return
  if (lastDocReportAttachmentId === id) return

  const el = viewerKind.value === 1 ? pdfEl.value : viewerKind.value === 2 ? pptEl.value : null
  if (!el) return
  const nearBottom = el.scrollTop + el.clientHeight >= el.scrollHeight - 2
  if (!nearBottom) return

  lastDocReportAttachmentId = id
  await reportAttachmentProgress(id, { progressPercent: 100 })
  await loadStudy(selectedStep.value.id)
}

async function renderPdf(url: string) {
  const el = pdfEl.value
  if (!el) return
  pdfLoading.value = true
  el.innerHTML = ''

  const token = ++pdfRenderToken
  try {
    const task = (pdfjsLib as any).getDocument(url)
    const pdf = await task.promise
    if (token !== pdfRenderToken) return

    for (let i = 1; i <= pdf.numPages; i++) {
      const page = await pdf.getPage(i)
      if (token !== pdfRenderToken) return

      const viewport = page.getViewport({ scale: 1.25 })
      const canvas = document.createElement('canvas')
      canvas.width = Math.floor(viewport.width)
      canvas.height = Math.floor(viewport.height)
      canvas.style.display = 'block'
      canvas.style.margin = '0 auto 12px'

      const ctx = canvas.getContext('2d')
      if (!ctx) continue
      el.appendChild(canvas)
      await page.render({ canvasContext: ctx, viewport }).promise
    }
  } catch {
    ElMessage.error('PDF 加载失败')
  } finally {
    if (token === pdfRenderToken) {
      pdfLoading.value = false
    }
  }
}

function back() {
  router.back()
}

onMounted(() => {
  load()
})

watch(
  () => selectedStepId.value,
  async (id) => {
    closeViewer()
    if (!id) {
      study.value = null
      return
    }
    const step = steps.value.find(x => x.id === id)
    if (!step || step.stepType === 1) {
      study.value = null
      return
    }
    await loadStudy(id)
  }
)
</script>

<style scoped>

.tabs :deep(.el-tabs__header) {
  margin: 0 0 10px 0;
}

.empty {
  padding: 20px 12px;
}

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
  border: 1px solid var(--el-border-color-lighter);
  border-radius: var(--el-border-radius-base);
  background: transparent;
}

.panel + .panel {
  margin-top: 12px;
}

.panel-title {
  font-weight: 700;
  margin-bottom: 10px;
}

.node-tip {
  padding: 10px 12px;
  border: 1px dashed var(--el-border-color-lighter);
  border-radius: var(--el-border-radius-base);
  color: var(--el-text-color-secondary);
  background: color-mix(in srgb, var(--el-bg-color) 70%, transparent);
}

.step {
  padding: 0;
}

.step-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}

.step-left {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.step-title {
  font-weight: 800;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.content {
  max-height: 520px;
  overflow: auto;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: var(--el-border-radius-base);
  padding: 12px;
  background: color-mix(in srgb, var(--el-bg-color) 72%, transparent);
}

.content-inner {
  line-height: 1.6;
  color: var(--el-text-color-regular);
}

.content-text {
  white-space: pre-wrap;
  line-height: 1.6;
  color: var(--el-text-color-regular);
}

.content-empty {
  color: var(--el-text-color-secondary);
}

.study-meta {
  display: flex;
  gap: 8px;
  margin-top: 10px;
  flex-wrap: wrap;
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

.attach-actions {
  display: flex;
  gap: 8px;
  flex: 0 0 auto;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.viewer {
  margin-top: 12px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: var(--el-border-radius-base);
  overflow: hidden;
  background: color-mix(in srgb, var(--el-bg-color) 72%, transparent);
}

.viewer-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.viewer-title {
  font-weight: 700;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.viewer-frame {
  width: 100%;
  height: 520px;
  border: 0;
}

.viewer-video {
  width: 100%;
  max-height: 520px;
}

.viewer-doc {
  width: 100%;
  height: 520px;
  overflow: auto;
  padding: 12px;
}

.viewer-loading {
  padding: 10px;
  color: var(--el-text-color-secondary);
}

.outline-list {
  display: grid;
  gap: 6px;
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

.done-dot {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  border: 2px solid var(--el-border-color);
  flex: 0 0 auto;
}

.done-dot.on {
  border-color: var(--el-color-success);
  background: var(--el-color-success);
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

.links-title {
  font-weight: 700;
  margin-bottom: 10px;
}

.links-empty {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.link-list {
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
