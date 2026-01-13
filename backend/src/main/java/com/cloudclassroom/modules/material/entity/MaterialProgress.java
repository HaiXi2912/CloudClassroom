package com.cloudclassroom.modules.material.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 资料学习进度表实体（material_progress）。
 */
@TableName("material_progress")
public class MaterialProgress {

  @TableId(type = IdType.AUTO)
  private Long id;

  private Long materialId;
  private Long userId;

  /**
   * 进度百分比：0-100。
   */
  private Integer progressPercent;

  /**
   * 最近位置（页码/秒数，前端定义）。
   */
  private Integer lastPosition;

  private LocalDateTime lastStudyAt;
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
