package com.cloudclassroom.modules.chat.dto;

import java.time.LocalDateTime;

/**
 * 会话列表行（最小实现：私聊对端信息 + 最近一条消息）。
 */
public class ConversationRow {

  private Long conversationId;
  private Long peerUserId;
  private String peerNickname;

  private Long lastMessageId;
  private String lastMessageContent;
  private LocalDateTime lastMessageAt;

  public Long getConversationId() {
    return conversationId;
  }

  public void setConversationId(Long conversationId) {
    this.conversationId = conversationId;
  }

  public Long getPeerUserId() {
    return peerUserId;
  }

  public void setPeerUserId(Long peerUserId) {
    this.peerUserId = peerUserId;
  }

  public String getPeerNickname() {
    return peerNickname;
  }

  public void setPeerNickname(String peerNickname) {
    this.peerNickname = peerNickname;
  }

  public Long getLastMessageId() {
    return lastMessageId;
  }

  public void setLastMessageId(Long lastMessageId) {
    this.lastMessageId = lastMessageId;
  }

  public String getLastMessageContent() {
    return lastMessageContent;
  }

  public void setLastMessageContent(String lastMessageContent) {
    this.lastMessageContent = lastMessageContent;
  }

  public LocalDateTime getLastMessageAt() {
    return lastMessageAt;
  }

  public void setLastMessageAt(LocalDateTime lastMessageAt) {
    this.lastMessageAt = lastMessageAt;
  }
}
