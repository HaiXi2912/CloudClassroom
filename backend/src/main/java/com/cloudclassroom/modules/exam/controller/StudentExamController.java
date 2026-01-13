package com.cloudclassroom.modules.exam.controller;

import com.cloudclassroom.common.ApiResponse;
import com.cloudclassroom.modules.exam.dto.AttemptRow;
import com.cloudclassroom.modules.exam.dto.ExamQuestionRow;
import com.cloudclassroom.modules.exam.dto.ExamRow;
import com.cloudclassroom.modules.exam.service.ExamService;
import com.cloudclassroom.security.SecurityUtil;
import com.cloudclassroom.security.annotation.RequirePerm;
import com.cloudclassroom.security.annotation.RequireRole;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生端：查看考试、答题提交、查询成绩。
 */
@RestController
@RequestMapping("/api/student/exams")
@RequireRole({"STUDENT"})
public class StudentExamController {

  private final ExamService examService;

  public StudentExamController(ExamService examService) {
    this.examService = examService;
  }

  @GetMapping
  public ApiResponse<List<ExamRow>> list(
      @RequestParam("courseId") @NotNull(message = "courseId 不能为空") @Min(value = 1, message = "courseId 必须 >= 1") Long courseId
  ) {
    Long studentId = SecurityUtil.requireUserId();
    return ApiResponse.ok(examService.listStudentExams(studentId, courseId));
  }

  @GetMapping("/{examId}/questions")
  public ApiResponse<List<ExamQuestionRow>> questions(@PathVariable Long examId) {
    Long studentId = SecurityUtil.requireUserId();
    return ApiResponse.ok(examService.listQuestionsForStudent(studentId, examId));
  }

  @GetMapping("/{examId}/my-attempt")
  public ApiResponse<AttemptRow> myAttempt(@PathVariable Long examId) {
    Long studentId = SecurityUtil.requireUserId();
    return ApiResponse.ok(examService.getMyAttempt(studentId, examId));
  }

  @PostMapping("/{examId}/submit")
  @RequirePerm({"exam:submit"})
  public ApiResponse<Void> submit(@PathVariable Long examId, @Valid @RequestBody SubmitRequest req) {
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

    examService.submit(studentId, examId, map);
    return ApiResponse.ok();
  }

  public static class SubmitRequest {

    @NotNull(message = "answers 不能为空")
    private List<AnswerItem> answers;

    public List<AnswerItem> getAnswers() {
      return answers;
    }

    public void setAnswers(List<AnswerItem> answers) {
      this.answers = answers;
    }
  }

  public static class AnswerItem {

    @NotNull(message = "questionId 不能为空")
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
