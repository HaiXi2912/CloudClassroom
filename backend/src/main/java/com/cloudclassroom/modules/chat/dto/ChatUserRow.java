package com.cloudclassroom.modules.chat.dto;

/**
 * 通讯录用户行。
 */
public class ChatUserRow {
  private Long userId;
  private String username;
  private String nickname;
  private String avatarUrl;
  private String roleCode;
  private String roleName;

  /** 是否好友 */
  private Boolean friend;
  /** 我是否已向对方发出待处理申请 */
  private Boolean pendingOut;
  /** 对方是否向我发出待处理申请 */
  private Boolean pendingIn;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public String getRoleCode() {
    return roleCode;
  }

  public void setRoleCode(String roleCode) {
    this.roleCode = roleCode;
  }

  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

  public Boolean getFriend() {
    return friend;
  }

  public void setFriend(Boolean friend) {
    this.friend = friend;
  }

  public Boolean getPendingOut() {
    return pendingOut;
  }

  public void setPendingOut(Boolean pendingOut) {
    this.pendingOut = pendingOut;
  }

  public Boolean getPendingIn() {
    return pendingIn;
  }

  public void setPendingIn(Boolean pendingIn) {
    this.pendingIn = pendingIn;
  }
}
