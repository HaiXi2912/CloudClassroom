package com.cloudclassroom.modules.exam.dto;

import java.time.LocalDateTime;

/**
 * 作答记录行。
 */
public class AttemptRow {

  private Long id;
  private Long examId;
  private Long studentId;
  private Integer status;
  private LocalDateTime startAt;
  private LocalDateTime submitAt;
  private Integer totalScore;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getExamId() {
    return examId;
  }

  public void setExamId(Long examId) {
    this.examId = examId;
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

  public LocalDateTime getStartAt() {
    return startAt;
  }

  public void setStartAt(LocalDateTime startAt) {
    this.startAt = startAt;
  }

  public LocalDateTime getSubmitAt() {
    return submitAt;
  }

  public void setSubmitAt(LocalDateTime submitAt) {
    this.submitAt = submitAt;
  }

  public Integer getTotalScore() {
    return totalScore;
  }

  public void setTotalScore(Integer totalScore) {
    this.totalScore = totalScore;
  }
}
