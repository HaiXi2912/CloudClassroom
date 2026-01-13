package com.cloudclassroom.modules.assignment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 作业提交表实体（assignment_submission）。
 */
@TableName("assignment_submission")
public class AssignmentSubmission {
  @TableId(type = IdType.AUTO)
  private Long id;

  private Long assignmentId;
  private Long studentId;

  /**
   * 状态：1未提交 2已提交 3已批改。
   */
  private Integer status;

  private LocalDateTime submitAt;
  private Integer autoScore;
  private Integer manualScore;
  private Integer totalScore;
  private String teacherComment;
  private Long gradedBy;
  private LocalDateTime gradedAt;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @TableLogic
  private Integer deleted;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getAssignmentId() {
    return assignmentId;
  }

  public void setAssignmentId(Long assignmentId) {
    this.assignmentId = assignmentId;
  }

  public Long getStudentId() {
    return studentId;
  }

  public void setStudentId(Long studentId) {
    this.studentId = studentId;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public LocalDateTime getSubmitAt() {
    return submitAt;
  }

  public void setSubmitAt(LocalDateTime submitAt) {
    this.submitAt = submitAt;
  }

  public Integer getAutoScore() {
    return autoScore;
  }

  public void setAutoScore(Integer autoScore) {
    this.autoScore = autoScore;
  }

  public Integer getManualScore() {
    return manualScore;
  }

  public void setManualScore(Integer manualScore) {
    this.manualScore = manualScore;
  }

  public Integer getTotalScore() {
    return totalScore;
  }

  public void setTotalScore(Integer totalScore) {
    this.totalScore = totalScore;
  }

  public String getTeacherComment() {
    return teacherComment;
  }

  public void setTeacherComment(String teacherComment) {
    this.teacherComment = teacherComment;
  }

  public Long getGradedBy() {
    return gradedBy;
  }

  public void setGradedBy(Long gradedBy) {
    this.gradedBy = gradedBy;
  }

  public LocalDateTime getGradedAt() {
    return gradedAt;
  }

  public void setGradedAt(LocalDateTime gradedAt) {
    this.gradedAt = gradedAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Integer getDeleted() {
    return deleted;
  }

  public void setDeleted(Integer deleted) {
    this.deleted = deleted;
  }
}
