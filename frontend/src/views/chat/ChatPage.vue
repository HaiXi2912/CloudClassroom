<template>
  <div class="page cc-page">
    <div class="wechat">
      <div class="sidebar">
        <div class="sidebar-top">
          <el-input v-model="keyword" placeholder="搜索（昵称 / 用户名）" clearable @input="onKeywordInput">
            <template #prefix>
              <el-icon class="search-prefix"><Search /></el-icon>
            </template>
          </el-input>

          <el-segmented v-model="tab" :options="tabOptions" size="small" class="seg" />
        </div>

        <div class="sidebar-body">
          <template v-if="tab === 'conversations'">
            <div class="section-head">
              <div class="title">消息</div>
            </div>

            <el-skeleton v-if="loadingConversations" :rows="6" animated />
            <el-empty v-else-if="filteredConversations.length === 0" description="暂无会话" />

            <div v-else class="list">
              <button
                v-for="c in filteredConversations"
                :key="c.conversationId"
                type="button"
                class="row"
                :class="activeConversationId === c.conversationId ? 'active' : ''"
                @click="selectConversation(c.conversationId)"
              >
                <el-avatar :size="34" class="avatar">{{ (c.peerNickname || String(c.peerUserId)).slice(0, 1) }}</el-avatar>
                <div class="row-main">
                  <div class="row-top">
                    <div class="name">{{ c.peerNickname || ('用户#' + c.peerUserId) }}</div>
                    <div class="meta">{{ c.lastMessageAt || '' }}</div>
                  </div>
                  <div class="sub">{{ c.lastMessageContent || '暂无消息' }}</div>
                </div>
              </button>
            </div>
          </template>

          <template v-else>
            <div class="section-head">
              <div class="title">联系人</div>
            </div>

            <el-skeleton v-if="loadingContacts" :rows="8" animated />

            <template v-else>
              <div v-if="incomingRequests.length > 0" class="requests">
                <div class="requests-title">好友申请</div>
                <div class="list">
                  <div v-for="r in incomingRequests" :key="r.id" class="req-row">
                    <div class="req-left">
                      <el-avatar :size="30">{{ (r.fromNickname || String(r.fromUserId)).slice(0, 1) }}</el-avatar>
                      <div class="req-main">
                        <div class="name">{{ r.fromNickname }}</div>
                        <div class="sub">{{ r.fromRoleName || r.fromRoleCode || '' }}</div>
                      </div>
                    </div>
                    <div class="req-actions">
                      <el-button size="small" type="primary" @click="acceptRequest(r.id)">通过</el-button>
                      <el-button size="small" @click="rejectRequest(r.id)">拒绝</el-button>
                    </div>
                  </div>
                </div>
              </div>

              <el-empty v-if="filteredUsers.length === 0" description="暂无联系人" />

              <div v-else class="contacts">
                <div v-if="teachers.length" class="group">
                  <div class="group-title">老师</div>
                  <div class="list">
                    <div v-for="u in teachers" :key="u.userId" class="contact-row">
                      <button type="button" class="contact-left" @click="startChat(u.userId)">
                        <el-avatar :size="34">{{ (u.nickname || String(u.userId)).slice(0, 1) }}</el-avatar>
                        <div class="row-main">
                          <div class="name">{{ u.nickname || u.username }}</div>
                          <div class="sub">{{ u.roleName || u.roleCode || '' }}</div>
                        </div>
                      </button>
                      <div class="contact-actions">
                        <el-button size="small" @click="startChat(u.userId)">私聊</el-button>
                        <el-button
                          v-if="!u.friend"
                          size="small"
                          type="primary"
                          :disabled="u.pendingOut"
                          @click="addFriend(u.userId)"
                        >
                          {{ u.pendingOut ? '已申请' : '添加好友' }}
                        </el-button>
                        <el-tag v-else type="success" size="small">好友</el-tag>
                      </div>
                    </div>
                  </div>
                </div>

                <div v-if="students.length" class="group">
                  <div class="group-title">学生</div>
                  <div class="list">
                    <div v-for="u in students" :key="u.userId" class="contact-row">
                      <button type="button" class="contact-left" @click="startChat(u.userId)">
                        <el-avatar :size="34">{{ (u.nickname || String(u.userId)).slice(0, 1) }}</el-avatar>
                        <div class="row-main">
                          <div class="name">{{ u.nickname || u.username }}</div>
                          <div class="sub">{{ u.roleName || u.roleCode || '' }}</div>
                        </div>
                      </button>
                      <div class="contact-actions">
                        <el-button size="small" @click="startChat(u.userId)">私聊</el-button>
                        <el-button
                          v-if="!u.friend"
                          size="small"
                          type="primary"
                          :disabled="u.pendingOut"
                          @click="addFriend(u.userId)"
                        >
                          {{ u.pendingOut ? '已申请' : '添加好友' }}
                        </el-button>
                        <el-tag v-else type="success" size="small">好友</el-tag>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </template>
          </template>
        </div>
      </div>

      <div class="chat">
        <div class="chat-head">
          <div>
            <div class="chat-title">{{ activeTitle }}</div>
          </div>
        </div>

        <el-empty v-if="!activeConversationId" description="从左侧选择一个会话或联系人开始聊天" />

        <div v-else class="chat-body">
          <div class="messages" ref="msgBox">
            <div v-for="m in messages" :key="m.id" class="msg" :class="m.senderId === myUserId ? 'me' : 'other'">
              <div class="bubble">
                <div class="content">{{ m.content }}</div>
                <div class="time">{{ m.sentAt }}</div>
              </div>
            </div>
          </div>

          <div class="send">
            <el-input v-model="draft" type="textarea" :autosize="{ minRows: 2, maxRows: 4 }" placeholder="输入消息内容…" />
            <div class="send-actions">
              <el-button type="primary" :disabled="!draft.trim()" @click="send">发送</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import {
  acceptFriendRequestApi,
  createOrGetPrivateConversationApi,
  listConversationsApi,
  listIncomingFriendRequestsApi,
  listMessagesApi,
  listUsersApi,
  rejectFriendRequestApi,
  sendFriendRequestApi,
  sendMessageApi,
  type ChatUserRow,
  type ConversationRow,
  type FriendRequestRow,
  type MessageRow
} from '@/api/chat'

const auth = useAuthStore()

const conversations = ref<ConversationRow[]>([])
const loadingConversations = ref(false)

const users = ref<ChatUserRow[]>([])
const incomingRequests = ref<FriendRequestRow[]>([])
const loadingContacts = ref(false)

const keyword = ref('')
const tab = ref<'conversations' | 'contacts'>('conversations')
const tabOptions = [
  { label: '消息', value: 'conversations' },
  { label: '联系人', value: 'contacts' }
]

const activeConversationId = ref<number | null>(null)
const messages = ref<MessageRow[]>([])
const draft = ref('')

const msgBox = ref<HTMLDivElement | null>(null)

const myUserId = computed(() => {
  const n = Number(auth.userId)
  return Number.isFinite(n) ? n : -1
})

const activeTitle = computed(() => {
  const c = conversations.value.find((x) => x.conversationId === activeConversationId.value)
  if (!c) return '当前会话'
  return c.peerNickname || `用户#${c.peerUserId}`
})

const filteredConversations = computed(() => {
  const kw = keyword.value.trim()
  if (!kw) return conversations.value
  return conversations.value.filter((c) => {
    const name = (c.peerNickname || String(c.peerUserId)).toLowerCase()
    const sub = (c.lastMessageContent || '').toLowerCase()
    return name.includes(kw.toLowerCase()) || sub.includes(kw.toLowerCase())
  })
})

const filteredUsers = computed(() => {
  const kw = keyword.value.trim().toLowerCase()
  if (!kw) return users.value
  return users.value.filter((u) => {
    const a = (u.nickname || '').toLowerCase()
    const b = (u.username || '').toLowerCase()
    return a.includes(kw) || b.includes(kw)
  })
})

const teachers = computed(() => filteredUsers.value.filter((u) => (u.roleCode || '').toUpperCase() === 'TEACHER'))
const students = computed(() => filteredUsers.value.filter((u) => (u.roleCode || '').toUpperCase() === 'STUDENT'))

async function loadConversations() {
  loadingConversations.value = true
  try {
    conversations.value = await listConversationsApi()
    if (!activeConversationId.value && conversations.value.length > 0) {
      activeConversationId.value = conversations.value[0].conversationId
      await loadMessages()
    }
  } finally {
    loadingConversations.value = false
  }
}

async function loadMessages() {
  if (!activeConversationId.value) return
  messages.value = await listMessagesApi(activeConversationId.value, 50)
  await nextTick()
  if (msgBox.value) msgBox.value.scrollTop = msgBox.value.scrollHeight
}

async function send() {
  if (!activeConversationId.value) return
  const content = draft.value.trim()
  if (!content) return

  await sendMessageApi(activeConversationId.value, content)

  draft.value = ''
  await loadMessages()
  ElMessage.success('已发送')
}

function selectConversation(conversationId: number) {
  activeConversationId.value = conversationId
  loadMessages()
}

let keywordTimer: number | null = null
function onKeywordInput() {
  if (keywordTimer) {
    window.clearTimeout(keywordTimer)
  }
  keywordTimer = window.setTimeout(() => {
    if (tab.value === 'contacts') {
      loadContacts()
    }
  }, 250)
}

async function loadContacts() {
  loadingContacts.value = true
  try {
    const [u, reqs] = await Promise.all([listUsersApi(keyword.value.trim() || undefined), listIncomingFriendRequestsApi()])
    users.value = u
    incomingRequests.value = reqs
  } finally {
    loadingContacts.value = false
  }
}

async function startChat(peerUserId: number) {
  const cid = await createOrGetPrivateConversationApi(peerUserId)
  await loadConversations()
  activeConversationId.value = cid
  await loadMessages()
}

async function addFriend(toUserId: number) {
  await sendFriendRequestApi(toUserId)
  ElMessage.success('已发送申请')
  await loadContacts()
}

async function acceptRequest(id: number) {
  await acceptFriendRequestApi(id)
  ElMessage.success('已通过')
  await loadContacts()
}

async function rejectRequest(id: number) {
  await rejectFriendRequestApi(id)
  ElMessage.success('已拒绝')
  await loadContacts()
}

onMounted(async () => {
  await loadConversations()
  await loadContacts()
})
</script>

<style scoped>
.page {
  max-width: none;
  margin: 0;
}

.wechat {
  display: grid;
  grid-template-columns: 340px 1fr;
  gap: 12px;
  align-items: stretch;
}

.sidebar,
.chat {
  border-radius: 12px;
  padding: 12px;
}

.sidebar {
  display: flex;
  flex-direction: column;
  min-height: calc(100vh - 140px);
}

.sidebar-top {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.seg {
  width: 100%;
}

.search-prefix {
  display: inline-flex;
  align-items: center;
  color: var(--el-text-color-secondary);
}

.sidebar-body {
  margin-top: 12px;
  flex: 1;
  overflow: auto;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  margin-bottom: 10px;
}

.title {
  font-weight: 700;
}

.list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.row {
  width: 100%;
  border: 1px solid color-mix(in srgb, var(--el-border-color) 70%, transparent);
  background: transparent;
  border-radius: 12px;
  padding: 10px;
  display: flex;
  gap: 10px;
  align-items: center;
  cursor: pointer;
}

.row.active {
  border-color: var(--el-color-primary);
}

.row-main {
  flex: 1;
  min-width: 0;
}

.row-top {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 10px;
}

.name {
  font-weight: 700;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.meta {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  flex: 0 0 auto;
}

.sub {
  margin-top: 4px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.requests {
  margin-bottom: 14px;
}

.requests-title {
  font-weight: 700;
  margin: 2px 0 8px;
}

.req-row {
  border: 1px solid color-mix(in srgb, var(--el-border-color) 70%, transparent);
  border-radius: 12px;
  padding: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.req-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.req-actions {
  display: flex;
  gap: 8px;
}

.group {
  margin-top: 12px;
}

.group-title {
  font-weight: 700;
  margin: 0 0 8px;
  color: var(--el-text-color-secondary);
}

.contact-row {
  border: 1px solid color-mix(in srgb, var(--el-border-color) 70%, transparent);
  border-radius: 12px;
  padding: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.contact-left {
  flex: 1;
  min-width: 0;
  border: 0;
  background: transparent;
  display: flex;
  gap: 10px;
  align-items: center;
  text-align: left;
  cursor: pointer;
  padding: 0;
}

.contact-actions {
  display: flex;
  gap: 8px;
  flex: 0 0 auto;
}

.chat-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid color-mix(in srgb, var(--el-border-color) 70%, transparent);
  margin-bottom: 10px;
}

.chat-title {
  font-weight: 800;
}

.muted {
  margin-top: 6px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}


.chat {
  display: flex;
  flex-direction: column;
  min-height: calc(100vh - 140px);
}

.chat-body {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.messages {
  flex: 1;
  min-height: 260px;
  overflow: auto;
  padding: 8px;
  background: transparent;
  border-radius: 12px;
}

.msg {
  display: flex;
  margin: 6px 0;
}

.msg.me {
  justify-content: flex-end;
}

.msg.other {
  justify-content: flex-start;
}

.bubble {
  max-width: 70%;
  padding: 10px 12px;
  border-radius: 12px;
  background: color-mix(in srgb, var(--el-bg-color) 82%, transparent);
}

.content {
  white-space: pre-wrap;
}

.time {
  margin-top: 6px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.send {
  margin-top: 12px;
}

.send-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
}

@media (max-width: 768px) {
  .wechat {
    grid-template-columns: 1fr;
  }

  .sidebar,
  .chat {
    min-height: auto;
  }
}
</style>
