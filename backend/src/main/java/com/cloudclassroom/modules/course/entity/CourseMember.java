package com.cloudclassroom.modules.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 课程成员表实体（course_member）。
 */
@TableName("course_member")
public class CourseMember {
  @TableId(type = IdType.AUTO)
  private Long id;

  private Long courseId;
  private Long userId;

  /**
   * 成员角色：1学生 2助教/老师（预留）。
   */
  private Integer memberRole;

  private LocalDateTime joinedAt;

  @TableLogic
  private Integer deleted;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getCourseId() {
    return courseId;
  }

  public void setCourseId(Long courseId) {
    this.courseId = courseId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
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

  public Integer getDeleted() {
    return deleted;
  }

  public void setDeleted(Integer deleted) {
    this.deleted = deleted;
  }
}
