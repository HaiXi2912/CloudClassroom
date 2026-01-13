package com.cloudclassroom.modules.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 章节附件完成进度表实体（task_step_attachment_progress）。
 */
@TableName("task_step_attachment_progress")
public class TaskStepAttachmentProgress {

  @TableId(type = IdType.AUTO)
  private Long id;

  private Long attachmentId;
  private Long userId;

  /**
   * 状态：1未完成 2已完成。
   */
  private Integer status;

  /**
   * 进度百分比：0-100。
   */
  private Integer progressPercent;

  /**
   * 视频：当前播放秒数（可空）。
   */
  private Integer positionSeconds;

  /**
   * 视频：总时长秒数（可空）。
   */
  private Integer durationSeconds;

  /**
   * 最近上报时间（可空）。
   */
  private LocalDateTime lastReportedAt;

  private LocalDateTime completedAt;
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

  public Long getAttachmentId() {
    return attachmentId;
  }

  public void setAttachmentId(Long attachmentId) {
    this.attachmentId = attachmentId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Integer getProgressPercent() {
    return progressPercent;
  }

  public void setProgressPercent(Integer progressPercent) {
    this.progressPercent = progressPercent;
  }

  public Integer getPositionSeconds() {
    return positionSeconds;
  }

  public void setPositionSeconds(Integer positionSeconds) {
    this.positionSeconds = positionSeconds;
  }

  public Integer getDurationSeconds() {
    return durationSeconds;
  }

  public void setDurationSeconds(Integer durationSeconds) {
    this.durationSeconds = durationSeconds;
  }

  public LocalDateTime getLastReportedAt() {
    return lastReportedAt;
  }

  public void setLastReportedAt(LocalDateTime lastReportedAt) {
    this.lastReportedAt = lastReportedAt;
  }

  public LocalDateTime getCompletedAt() {
    return completedAt;
  }

  public void setCompletedAt(LocalDateTime completedAt) {
    this.completedAt = completedAt;
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
