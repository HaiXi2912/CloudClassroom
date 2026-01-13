import { http } from './http'
import type { ApiResponse } from '@/types/api'

export type ConversationRow = {
  conversationId: number
  peerUserId: number
  peerNickname: string
  lastMessageId: number | null
  lastMessageContent: string | null
  lastMessageAt: string | null
}

export type MessageRow = {
  id: number
  conversationId: number
  senderId: number
  content: string
  sentAt: string
}

export type ChatUserRow = {
  userId: number
  username: string
  nickname: string
  avatarUrl: string | null
  roleCode: string | null
  roleName: string | null
  friend: boolean
  pendingOut: boolean
  pendingIn: boolean
}

export type FriendRequestRow = {
  id: number
  fromUserId: number
  fromNickname: string
  fromAvatarUrl: string | null
  fromRoleCode: string | null
  fromRoleName: string | null
  createdAt: string
}

export async function listConversationsApi(): Promise<ConversationRow[]> {
  const resp = await http.get<ApiResponse<ConversationRow[]>>('/api/chat/conversations')
  return resp.data.data || []
}

export async function listMessagesApi(conversationId: number, limit = 50): Promise<MessageRow[]> {
  const resp = await http.get<ApiResponse<MessageRow[]>>(`/api/chat/conversations/${conversationId}/messages`, {
    params: { limit }
  })
  return resp.data.data || []
}

export async function createOrGetPrivateConversationApi(peerUserId: number): Promise<number> {
  const resp = await http.post<ApiResponse<{ conversationId: number }>>('/api/chat/conversations/private', { peerUserId })
  return resp.data.data.conversationId
}

export async function sendMessageApi(conversationId: number, content: string): Promise<void> {
  await http.post<ApiResponse<MessageRow>>(`/api/chat/conversations/${conversationId}/messages`, { content })
}

export async function listUsersApi(keyword?: string): Promise<ChatUserRow[]> {
  const resp = await http.get<ApiResponse<ChatUserRow[]>>('/api/chat/users', { params: { keyword } })
  return resp.data.data || []
}

export async function listIncomingFriendRequestsApi(): Promise<FriendRequestRow[]> {
  const resp = await http.get<ApiResponse<FriendRequestRow[]>>('/api/chat/friend-requests/incoming')
  return resp.data.data || []
}

export async function sendFriendRequestApi(toUserId: number): Promise<void> {
  await http.post<ApiResponse<void>>('/api/chat/friend-requests', { toUserId })
}

export async function acceptFriendRequestApi(id: number): Promise<void> {
  await http.post<ApiResponse<void>>(`/api/chat/friend-requests/${id}/accept`)
}

export async function rejectFriendRequestApi(id: number): Promise<void> {
  await http.post<ApiResponse<void>>(`/api/chat/friend-requests/${id}/reject`)
}
