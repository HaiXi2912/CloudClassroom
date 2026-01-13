package com.cloudclassroom.modules.task.dto;

/**
 * 任务行（老师端/学生端通用）。
 */
public class TaskRow {

  private Long id;
  private Long courseId;
  private String title;
  private String description;
  private Integer status;
  private Integer sortNo;

  // 学生端：进度
  private Integer totalSteps;
  private Integer doneSteps;
  private Integer progressPercent;

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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Integer getSortNo() {
    return sortNo;
  }

  public void setSortNo(Integer sortNo) {
    this.sortNo = sortNo;
  }

  public Integer getTotalSteps() {
    return totalSteps;
  }

  public void setTotalSteps(Integer totalSteps) {
    this.totalSteps = totalSteps;
  }

  public Integer getDoneSteps() {
    return doneSteps;
  }

  public void setDoneSteps(Integer doneSteps) {
    this.doneSteps = doneSteps;
  }

  public Integer getProgressPercent() {
    return progressPercent;
  }

  public void setProgressPercent(Integer progressPercent) {
    this.progressPercent = progressPercent;
  }
}
