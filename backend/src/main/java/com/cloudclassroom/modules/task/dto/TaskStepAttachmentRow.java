package com.cloudclassroom.modules.task.dto;

/**
 * 章节附件行。
 */
public class TaskStepAttachmentRow {

  private Long id;
  private Long stepId;
  private Integer kind;
  private String title;
  private Long fileId;
  private String url;
  private Integer sortNo;

  // 学生端：是否已完成该附件
  private Integer done;

  // 学生端：学习进度（0-100，可选）
  private Integer progressPercent;
  private Integer positionSeconds;
  private Integer durationSeconds;

  // 展示用（可选）
  private String originalName;
  private String contentType;
  private Long fileSize;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getStepId() {
    return stepId;
  }

  public void setStepId(Long stepId) {
    this.stepId = stepId;
  }

  public Integer getKind() {
    return kind;
  }

  public void setKind(Integer kind) {
    this.kind = kind;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Long getFileId() {
    return fileId;
  }

  public void setFileId(Long fileId) {
    this.fileId = fileId;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
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

  public String getOriginalName() {
    return originalName;
  }

  public void setOriginalName(String originalName) {
    this.originalName = originalName;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public Long getFileSize() {
    return fileSize;
  }

  public void setFileSize(Long fileSize) {
    this.fileSize = fileSize;
  }
}
