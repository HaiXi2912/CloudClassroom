package com.cloudclassroom.modules.course.dto;

import java.time.LocalDateTime;

/**
 * 课程成员列表行。
 */
public class CourseMemberRow {
  private Long userId;
  private String username;
  private String nickname;
  private Integer memberRole;
  private LocalDateTime joinedAt;

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

  public Integer getMemberRole() {
    return memberRole;
  }

  public void setMemberRole(Integer memberRole) {
    this.memberRole = memberRole;
  }

  public LocalDateTime getJoinedAt() {
    return joinedAt;
  }

  public void setJoinedAt(LocalDateTime joinedAt) {
    this.joinedAt = joinedAt;
  }
}
