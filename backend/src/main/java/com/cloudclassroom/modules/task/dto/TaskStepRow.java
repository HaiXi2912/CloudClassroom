package com.cloudclassroom.modules.task.dto;

/**
 * 任务步骤行。
 */
public class TaskStepRow {

  private Long id;
  private Long taskId;
  // 1=节点，2=内容
  private Integer stepType;
  private Long parentId;
  private String title;
  private String content;
  private Integer sortNo;

  // 学生端：是否完成
  private Integer done;

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

  public Integer getStepType() {
    return stepType;
  }

  public void setStepType(Integer stepType) {
    this.stepType = stepType;
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Integer getSortNo() {
    return sortNo;
  }

  public void setSortNo(Integer sortNo) {
    this.sortNo = sortNo;
  }

  public Integer getDone() {
    return done;
  }

  public void setDone(Integer done) {
    this.done = done;
  }
}
