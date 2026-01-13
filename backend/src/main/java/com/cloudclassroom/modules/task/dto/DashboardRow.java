package com.cloudclassroom.modules.task.dto;

/**
 * 学习看板（课程维度汇总）。
 */
public class DashboardRow {

  private Long courseId;

  private Integer taskPercent;
  private Integer assignmentPercent;
  private Integer examPercent;

  /**
   * 得分率：0-100（作业/考试使用实际得分；任务暂用完成度）。
   */
  private Integer taskScorePercent;
  private Integer assignmentScorePercent;
  private Integer examScorePercent;

  private Integer taskDone;
  private Integer taskTotal;

  private Integer assignmentDone;
  private Integer assignmentTotal;

  private Integer examDone;
  private Integer examTotal;

  private Double finalScore;

  public Long getCourseId() {
    return courseId;
  }

  public void setCourseId(Long courseId) {
    this.courseId = courseId;
  }

  public Integer getTaskPercent() {
    return taskPercent;
  }

  public void setTaskPercent(Integer taskPercent) {
    this.taskPercent = taskPercent;
  }

  public Integer getTaskScorePercent() {
    return taskScorePercent;
  }

  public void setTaskScorePercent(Integer taskScorePercent) {
    this.taskScorePercent = taskScorePercent;
  }

  public Integer getAssignmentPercent() {
    return assignmentPercent;
  }

  public void setAssignmentPercent(Integer assignmentPercent) {
    this.assignmentPercent = assignmentPercent;
  }

  public Integer getAssignmentScorePercent() {
    return assignmentScorePercent;
  }

  public void setAssignmentScorePercent(Integer assignmentScorePercent) {
    this.assignmentScorePercent = assignmentScorePercent;
  }

  public Integer getExamPercent() {
    return examPercent;
  }

  public void setExamPercent(Integer examPercent) {
    this.examPercent = examPercent;
  }

  public Integer getExamScorePercent() {
    return examScorePercent;
  }

  public void setExamScorePercent(Integer examScorePercent) {
    this.examScorePercent = examScorePercent;
  }

  public Integer getTaskDone() {
    return taskDone;
  }

  public void setTaskDone(Integer taskDone) {
    this.taskDone = taskDone;
  }

  public Integer getTaskTotal() {
    return taskTotal;
  }

  public void setTaskTotal(Integer taskTotal) {
    this.taskTotal = taskTotal;
  }

  public Integer getAssignmentDone() {
    return assignmentDone;
  }

  public void setAssignmentDone(Integer assignmentDone) {
    this.assignmentDone = assignmentDone;
  }

  public Integer getAssignmentTotal() {
    return assignmentTotal;
  }

  public void setAssignmentTotal(Integer assignmentTotal) {
    this.assignmentTotal = assignmentTotal;
  }

  public Integer getExamDone() {
    return examDone;
  }

  public void setExamDone(Integer examDone) {
    this.examDone = examDone;
  }

  public Integer getExamTotal() {
    return examTotal;
  }

  public void setExamTotal(Integer examTotal) {
    this.examTotal = examTotal;
  }

  public Double getFinalScore() {
    return finalScore;
  }

  public void setFinalScore(Double finalScore) {
    this.finalScore = finalScore;
  }
}
