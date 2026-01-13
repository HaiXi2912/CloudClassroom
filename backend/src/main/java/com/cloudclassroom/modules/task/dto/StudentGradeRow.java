package com.cloudclassroom.modules.task.dto;

/**
 * 学生成绩行（课程维度）。
 */
public class StudentGradeRow {

  private Long studentId;
  private String nickname;

  private Integer taskPercent;
  private Integer assignmentPercent;
  private Integer examPercent;

  private Integer taskScorePercent;
  private Integer assignmentScorePercent;
  private Integer examScorePercent;

  private Double finalScore;

  public Long getStudentId() {
    return studentId;
  }

  public void setStudentId(Long studentId) {
    this.studentId = studentId;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
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

  public Double getFinalScore() {
    return finalScore;
  }

  public void setFinalScore(Double finalScore) {
    this.finalScore = finalScore;
  }
}
