package com.cloudclassroom.modules.assignment.dto;

import java.time.LocalDateTime;

/**
 * 作业列表行。
 */
public class AssignmentRow {
  private Long id;
  private Long courseId;
  private String title;
  private LocalDateTime publishAt;
  private LocalDateTime dueAt;
  private Integer totalScore;

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

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public LocalDateTime getPublishAt() {
    return publishAt;
  }

  public void setPublishAt(LocalDateTime publishAt) {
    this.publishAt = publishAt;
  }

  public LocalDateTime getDueAt() {
    return dueAt;
  }

  public void setDueAt(LocalDateTime dueAt) {
    this.dueAt = dueAt;
  }

  public Integer getTotalScore() {
    return totalScore;
  }

  public void setTotalScore(Integer totalScore) {
    this.totalScore = totalScore;
  }
}
