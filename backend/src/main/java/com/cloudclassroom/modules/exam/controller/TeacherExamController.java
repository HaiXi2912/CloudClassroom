package com.cloudclassroom.modules.exam.controller;

import com.cloudclassroom.common.ApiResponse;
import com.cloudclassroom.modules.exam.dto.ExamQuestionRow;
import com.cloudclassroom.modules.exam.dto.ExamRow;
import com.cloudclassroom.modules.exam.entity.Exam;
import com.cloudclassroom.modules.exam.entity.ExamQuestion;
import com.cloudclassroom.modules.exam.service.ExamService;
import com.cloudclassroom.security.SecurityUtil;
import com.cloudclassroom.security.annotation.RequirePerm;
import com.cloudclassroom.security.annotation.RequireRole;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 老师端：考试组卷与查看。
 */
@RestController
@RequestMapping("/api/teacher/exams")
@RequireRole({"TEACHER"})
public class TeacherExamController {

  private final ExamService examService;

  public TeacherExamController(ExamService examService) {
    this.examService = examService;
  }

  @PostMapping
  @RequirePerm({"exam:create"})
  public ApiResponse<Long> create(@Valid @RequestBody CreateExamRequest req) {
    Long teacherId = SecurityUtil.requireUserId();

    Exam e = examService.createExam(
        teacherId,
        req.getCourseId(),
        req.getTitle(),
        req.getDescription(),
        req.getStartAt(),
        req.getEndAt(),
        req.getDurationMinutes(),
        req.getQuestions()
    );

    return ApiResponse.ok(e.getId());
  }

  @GetMapping
  public ApiResponse<List<ExamRow>> list(
      @RequestParam("courseId") @NotNull(message = "courseId 不能为空") @Min(value = 1, message = "courseId 必须 >= 1") Long courseId
  ) {
    Long teacherId = SecurityUtil.requireUserId();
    return ApiResponse.ok(examService.listTeacherExams(teacherId, courseId));
  }

  @GetMapping("/{examId}/questions")
  public ApiResponse<List<ExamQuestionRow>> questions(@PathVariable Long examId) {
    Long teacherId = SecurityUtil.requireUserId();
    return ApiResponse.ok(examService.listQuestionsForTeacher(teacherId, examId));
  }

  public static class CreateExamRequest {

    @NotNull(message = "courseId 不能为空")
    private Long courseId;

    @NotBlank(message = "title 不能为空")
    private String title;

    private String description;

    @NotNull(message = "startAt 不能为空")
    private LocalDateTime startAt;

    @NotNull(message = "endAt 不能为空")
    private LocalDateTime endAt;

    private Integer durationMinutes;

    @NotNull(message = "questions 不能为空")
    private List<ExamQuestion> questions;

    public Long getCourseId() {
      return courseId;
    }

    public void setCourseId(Long courseId) {
      this.courseId = courseId;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public LocalDateTime getStartAt() {
      return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
      this.startAt = startAt;
    }

    public LocalDateTime getEndAt() {
      return endAt;
    }

    public void setEndAt(LocalDateTime endAt) {
      this.endAt = endAt;
    }

    public Integer getDurationMinutes() {
      return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
      this.durationMinutes = durationMinutes;
    }

    public List<ExamQuestion> getQuestions() {
      return questions;
    }

    public void setQuestions(List<ExamQuestion> questions) {
      this.questions = questions;
    }
  }
}
