package com.cloudclassroom.modules.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 任务关联表实体（task_link）。
 */
@TableName("task_link")
public class TaskLink {

  @TableId(type = IdType.AUTO)
  private Long id;

  private Long taskId;
  private Long stepId;

  /**
   * 关联类型：1作业 2考试 3资料（预留）。
   */
  private Integer linkType;

  /**
   * 关联对象ID（assignmentId/examId/materialId）。
   */
  private Long refId;

  private Integer sortNo;
  private LocalDateTime createdAt;

  @TableLogic
  private Integer deleted;

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

  public Integer getSortNo() {
    return sortNo;
  }

  public void setSortNo(Integer sortNo) {
    this.sortNo = sortNo;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public Integer getDeleted() {
    return deleted;
  }

  public void setDeleted(Integer deleted) {
    this.deleted = deleted;
  }
}
