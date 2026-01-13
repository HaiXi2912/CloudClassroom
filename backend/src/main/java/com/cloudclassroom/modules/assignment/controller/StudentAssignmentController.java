package com.cloudclassroom.modules.assignment.controller;

import com.cloudclassroom.common.ApiResponse;
import com.cloudclassroom.modules.assignment.dto.AssignmentQuestionRow;
import com.cloudclassroom.modules.assignment.dto.AssignmentRow;
import com.cloudclassroom.modules.assignment.service.AssignmentService;
import com.cloudclassroom.security.SecurityUtil;
import com.cloudclassroom.security.annotation.RequireRole;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生端作业接口：查看、提交。
 */
@RestController
@RequestMapping("/api/student/assignments")
public class StudentAssignmentController {

  private final AssignmentService assignmentService;

  public StudentAssignmentController(AssignmentService assignmentService) {
    this.assignmentService = assignmentService;
  }

  @GetMapping
  @RequireRole("STUDENT")
  public ApiResponse<List<AssignmentRow>> list(
      @RequestParam("courseId") @NotNull(message = "courseId 不能为空") @Min(value = 1, message = "courseId 必须 >= 1") Long courseId
  ) {
    Long studentId = SecurityUtil.requireUserId();
    return ApiResponse.ok(assignmentService.listStudentAssignments(studentId, courseId));
  }

  @GetMapping("/{assignmentId}/questions")
  @RequireRole("STUDENT")
  public ApiResponse<List<AssignmentQuestionRow>> listQuestions(@PathVariable Long assignmentId) {
    Long studentId = SecurityUtil.requireUserId();
    return ApiResponse.ok(assignmentService.listQuestionsForStudent(studentId, assignmentId));
  }

  @PostMapping("/{assignmentId}/submit")
  @RequireRole("STUDENT")
  public ApiResponse<Void> submit(@PathVariable Long assignmentId, @Valid @RequestBody SubmitRequest req) {
    Long studentId = SecurityUtil.requireUserId();

    Map<Long, String> map = new HashMap<>();
    if (req.getAnswers() != null) {
      for (AnswerItem item : req.getAnswers()) {
        if (item == null || item.getQuestionId() == null) {
          continue;
        }
        map.put(item.getQuestionId(), item.getAnswerText());
      }
    }

    assignmentService.submit(studentId, assignmentId, map);
    return ApiResponse.ok();
  }

  /**
   * 提交请求。
   */
  public static class SubmitRequest {

    @NotNull(message = "答案不能为空")
    private List<AnswerItem> answers;

    public List<AnswerItem> getAnswers() {
      return answers;
    }

    public void setAnswers(List<AnswerItem> answers) {
      this.answers = answers;
    }
  }

  public static class AnswerItem {

    @NotNull(message = "题目ID不能为空")
    private Long questionId;

    private String answerText;

    public Long getQuestionId() {
      return questionId;
    }

    public void setQuestionId(Long questionId) {
      this.questionId = questionId;
    }

    public String getAnswerText() {
      return answerText;
    }

    public void setAnswerText(String answerText) {
      this.answerText = answerText;
    }
  }
}
