package com.cloudclassroom.modules.assignment.dto;

import java.time.LocalDateTime;

/**
 * 作业提交列表行。
 */
public class SubmissionRow {
  private Long id;
  private Long assignmentId;
  private Long studentId;
  private Integer status;
  private Integer autoScore;
  private Integer manualScore;
  private Integer totalScore;
  private LocalDateTime submitAt;
  private LocalDateTime gradedAt;

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

  public LocalDateTime getSubmitAt() {
    return submitAt;
  }

  public void setSubmitAt(LocalDateTime submitAt) {
    this.submitAt = submitAt;
  }

  public LocalDateTime getGradedAt() {
    return gradedAt;
  }

  public void setGradedAt(LocalDateTime gradedAt) {
    this.gradedAt = gradedAt;
  }
}
