package com.cloudclassroom.modules.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 课程成绩权重配置表实体（course_grade_config）。
 */
@TableName("course_grade_config")
public class CourseGradeConfig {

  @TableId(type = IdType.AUTO)
  private Long id;

  private Long courseId;
  private Integer weightTask;
  private Integer weightAssignment;
  private Integer weightExam;
  private Long createdBy;
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

  public Long getCourseId() {
    return courseId;
  }

  public void setCourseId(Long courseId) {
    this.courseId = courseId;
  }

  public Integer getWeightTask() {
    return weightTask;
  }

  public void setWeightTask(Integer weightTask) {
    this.weightTask = weightTask;
  }

  public Integer getWeightAssignment() {
    return weightAssignment;
  }

  public void setWeightAssignment(Integer weightAssignment) {
    this.weightAssignment = weightAssignment;
  }

  public Integer getWeightExam() {
    return weightExam;
  }

  public void setWeightExam(Integer weightExam) {
    this.weightExam = weightExam;
  }

  public Long getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Long createdBy) {
    this.createdBy = createdBy;
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
