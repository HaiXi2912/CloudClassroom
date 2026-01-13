package com.cloudclassroom.modules.material.dto;

import java.time.LocalDateTime;

/**
 * 学习进度返回。
 */
public class MaterialProgressRow {

  private Long materialId;
  private Long userId;
  private Integer progressPercent;
  private Integer lastPosition;
  private LocalDateTime lastStudyAt;

  public Long getMaterialId() {
    return materialId;
  }

  public void setMaterialId(Long materialId) {
    this.materialId = materialId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Integer getProgressPercent() {
    return progressPercent;
  }

  public void setProgressPercent(Integer progressPercent) {
    this.progressPercent = progressPercent;
  }

  public Integer getLastPosition() {
    return lastPosition;
  }

  public void setLastPosition(Integer lastPosition) {
    this.lastPosition = lastPosition;
  }

  public LocalDateTime getLastStudyAt() {
    return lastStudyAt;
  }

  public void setLastStudyAt(LocalDateTime lastStudyAt) {
    this.lastStudyAt = lastStudyAt;
  }
}
