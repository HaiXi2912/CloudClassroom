package com.cloudclassroom.modules.task.dto;

/**
 * 任务关联行。
 */
public class TaskLinkRow {

  private Long id;
  private Long taskId;
  private Long stepId;
  private Integer linkType;
  private Long refId;
  private String refTitle;
  private Integer sortNo;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getTaskId() {
    return taskId;
  }

  public void setTaskId(Long taskId) {
    this.taskId = taskId;
  }

  public Long getStepId() {
    return stepId;
  }

  public void setStepId(Long stepId) {
    this.stepId = stepId;
  }

  public Integer getLinkType() {
    return linkType;
  }

  public void setLinkType(Integer linkType) {
    this.linkType = linkType;
  }

  public Long getRefId() {
    return refId;
  }

  public void setRefId(Long refId) {
    this.refId = refId;
  }

  public String getRefTitle() {
    return refTitle;
  }

  public void setRefTitle(String refTitle) {
    this.refTitle = refTitle;
  }

  public Integer getSortNo() {
    return sortNo;
  }

  public void setSortNo(Integer sortNo) {
    this.sortNo = sortNo;
  }
}
