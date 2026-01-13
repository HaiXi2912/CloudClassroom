package com.cloudclassroom.modules.task.dto;

import java.util.List;

/**
 * 学生端：章节学习状态（正文完成 + 附件完成）。
 */
public class StepStudyStatusRow {

  private Long stepId;

  /**
   * 1未完成 2已完成。
   */
  private Integer contentStatus;

  private List<TaskStepAttachmentRow> attachments;

  public Long getStepId() {
    return stepId;
  }

  public void setStepId(Long stepId) {
    this.stepId = stepId;
  }

  public Integer getContentStatus() {
    return contentStatus;
  }

  public void setContentStatus(Integer contentStatus) {
    this.contentStatus = contentStatus;
  }

  public List<TaskStepAttachmentRow> getAttachments() {
    return attachments;
  }

  public void setAttachments(List<TaskStepAttachmentRow> attachments) {
    this.attachments = attachments;
  }
}
