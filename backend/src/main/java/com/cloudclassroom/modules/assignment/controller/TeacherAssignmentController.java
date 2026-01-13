package com.cloudclassroom.modules.assignment.controller;

import com.cloudclassroom.common.ApiResponse;
import com.cloudclassroom.modules.assignment.entity.Assignment;
import com.cloudclassroom.modules.assignment.entity.AssignmentQuestion;
import com.cloudclassroom.modules.assignment.dto.AssignmentQuestionRow;
import com.cloudclassroom.modules.assignment.dto.AssignmentRow;
import com.cloudclassroom.modules.assignment.dto.SubmissionRow;
import com.cloudclassroom.modules.assignment.service.AssignmentService;
import com.cloudclassroom.security.SecurityUtil;
import com.cloudclassroom.security.annotation.RequireRole;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 老师端作业接口：布置、查看、批改。
 */
@RestController
@RequestMapping("/api/teacher/assignments")
public class TeacherAssignmentController {

  private final AssignmentService assignmentService;

  public TeacherAssignmentController(AssignmentService assignmentService) {
    this.assignmentService = assignmentService;
  }

  @PostMapping
  @RequireRole("TEACHER")
  public ApiResponse<Long> create(@Valid @RequestBody CreateAssignmentRequest req) {
    Long teacherId = SecurityUtil.requireUserId();

    Assignment a = assignmentService.createAssignment(
        teacherId,
        req.getCourseId(),
        req.getTitle(),
        req.getContent(),
        req.getDueAt(),
        req.getQuestions()
    );

    return ApiResponse.ok(a.getId());
  }

  @GetMapping
  @RequireRole("TEACHER")
  public ApiResponse<List<AssignmentRow>> list(
      @RequestParam("courseId") @NotNull(message = "courseId 不能为空") @Min(value = 1, message = "courseId 必须 >= 1") Long courseId
  ) {
    Long teacherId = SecurityUtil.requireUserId();
    return ApiResponse.ok(assignmentService.listTeacherAssignments(teacherId, courseId));
  }

  @GetMapping("/{assignmentId}/questions")
  @RequireRole("TEACHER")
  public ApiResponse<List<AssignmentQuestionRow>> listQuestions(@PathVariable Long assignmentId) {
    Long teacherId = SecurityUtil.requireUserId();
    return ApiResponse.ok(assignmentService.listQuestionsForTeacher(teacherId, assignmentId));
  }

  @GetMapping("/{assignmentId}/submissions")
  @RequireRole("TEACHER")
  public ApiResponse<List<SubmissionRow>> listSubmissions(@PathVariable Long assignmentId) {
    Long teacherId = SecurityUtil.requireUserId();
    return ApiResponse.ok(assignmentService.listSubmissionsForTeacher(teacherId, assignmentId));
  }

  @PostMapping("/submissions/{submissionId}/grade")
  @RequireRole("TEACHER")
  public ApiResponse<Void> grade(@PathVariable Long submissionId, @Valid @RequestBody GradeRequest req) {
    Long teacherId = SecurityUtil.requireUserId();
    assignmentService.gradeSubmission(teacherId, submissionId, req.getManualScore(), req.getTeacherComment());
    return ApiResponse.ok();
  }

  /**
   * 创建作业请求。
   */
  public static class CreateAssignmentRequest {

    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String content;

    private LocalDateTime dueAt;

    @NotNull(message = "题目不能为空")
    private List<AssignmentQuestion> questions;

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

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }

    public LocalDateTime getDueAt() {
      return dueAt;
    }

    public void setDueAt(LocalDateTime dueAt) {
      this.dueAt = dueAt;
    }

    public List<AssignmentQuestion> getQuestions() {
      return questions;
    }

    public void setQuestions(List<AssignmentQuestion> questions) {
      this.questions = questions;
    }
  }

  /**
   * 批改请求。
   */
  public static class GradeRequest {

    private Integer manualScore;

    private String teacherComment;

    public Integer getManualScore() {
      return manualScore;
    }

    public void setManualScore(Integer manualScore) {
      this.manualScore = manualScore;
    }

    public String getTeacherComment() {
      return teacherComment;
    }

    public void setTeacherComment(String teacherComment) {
      this.teacherComment = teacherComment;
    }
  }
}
