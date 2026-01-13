package com.cloudclassroom.modules.task.dto;

/**
 * 成绩权重配置。
 */
public class GradeConfigRow {

  private Long courseId;
  private Integer weightTask;
  private Integer weightAssignment;
  private Integer weightExam;

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
}
