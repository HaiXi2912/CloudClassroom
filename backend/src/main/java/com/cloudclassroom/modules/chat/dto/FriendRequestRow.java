package com.cloudclassroom.modules.chat.dto;

import java.time.LocalDateTime;

/**
 * 好友申请列表行。
 */
public class FriendRequestRow {
  private Long id;
  private Long fromUserId;
  private String fromNickname;
  private String fromAvatarUrl;
  private String fromRoleCode;
  private String fromRoleName;
  private LocalDateTime createdAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getFromUserId() {
    return fromUserId;
  }

  public void setFromUserId(Long fromUserId) {
    this.fromUserId = fromUserId;
  }

  public String getFromNickname() {
    return fromNickname;
  }

  public void setFromNickname(String fromNickname) {
    this.fromNickname = fromNickname;
  }

  public String getFromAvatarUrl() {
    return fromAvatarUrl;
  }

  public void setFromAvatarUrl(String fromAvatarUrl) {
    this.fromAvatarUrl = fromAvatarUrl;
  }

  public String getFromRoleCode() {
    return fromRoleCode;
  }

  public void setFromRoleCode(String fromRoleCode) {
    this.fromRoleCode = fromRoleCode;
  }

  public String getFromRoleName() {
    return fromRoleName;
  }

  public void setFromRoleName(String fromRoleName) {
    this.fromRoleName = fromRoleName;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
