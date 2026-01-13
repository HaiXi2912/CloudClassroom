package com.cloudclassroom.modules.questionbank.controller;

import com.cloudclassroom.common.ApiResponse;
import com.cloudclassroom.modules.questionbank.dto.QuestionBankQuestionRow;
import com.cloudclassroom.modules.questionbank.entity.QuestionBankQuestion;
import com.cloudclassroom.modules.questionbank.service.QuestionBankService;
import com.cloudclassroom.security.SecurityUtil;
import com.cloudclassroom.security.annotation.RequireRole;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 老师端：题库（按课程）。
 */
@RestController
@RequestMapping("/api/teacher/question-bank")
@RequireRole({"TEACHER"})
public class TeacherQuestionBankController {

  private final QuestionBankService questionBankService;

  public TeacherQuestionBankController(QuestionBankService questionBankService) {
    this.questionBankService = questionBankService;
  }

  @GetMapping
  public ApiResponse<List<QuestionBankQuestionRow>> list(
      @RequestParam("courseId") @NotNull(message = "courseId 不能为空") @Min(value = 1, message = "courseId 必须 >= 1") Long courseId
  ) {
    Long teacherId = SecurityUtil.requireUserId();
    return ApiResponse.ok(questionBankService.listForTeacher(teacherId, courseId));
  }

  @PostMapping
  public ApiResponse<Long> create(@Valid @RequestBody CreateQuestionRequest req) {
    Long teacherId = SecurityUtil.requireUserId();
    QuestionBankQuestion q = new QuestionBankQuestion();
    q.setQuestionType(req.getQuestionType());
    q.setQuestionText(req.getQuestionText());
    q.setOptionA(req.getOptionA());
    q.setOptionB(req.getOptionB());
    q.setOptionC(req.getOptionC());
    q.setOptionD(req.getOptionD());
    q.setCorrectAnswer(req.getCorrectAnswer());
    q.setScore(req.getScore());
    return ApiResponse.ok(questionBankService.create(teacherId, req.getCourseId(), q));
  }

  @PutMapping("/{id}")
  public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateQuestionRequest req) {
    Long teacherId = SecurityUtil.requireUserId();
    QuestionBankQuestion patch = new QuestionBankQuestion();
    patch.setQuestionType(req.getQuestionType());
    patch.setQuestionText(req.getQuestionText());
    patch.setOptionA(req.getOptionA());
    patch.setOptionB(req.getOptionB());
    patch.setOptionC(req.getOptionC());
    patch.setOptionD(req.getOptionD());
    patch.setCorrectAnswer(req.getCorrectAnswer());
    patch.setScore(req.getScore());
    questionBankService.update(teacherId, id, patch);
    return ApiResponse.ok(null);
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable Long id) {
    Long teacherId = SecurityUtil.requireUserId();
    questionBankService.delete(teacherId, id);
    return ApiResponse.ok(null);
  }

  @PostMapping("/import")
  public ApiResponse<Integer> importFromCourse(@Valid @RequestBody ImportRequest req) {
    Long teacherId = SecurityUtil.requireUserId();
    int count = questionBankService.importFromCourse(teacherId, req.getTargetCourseId(), req.getSourceCourseId(), req.getQuestionIds());
    return ApiResponse.ok(count);
  }

  @GetMapping("/random")
  public ApiResponse<List<QuestionBankQuestionRow>> random(
      @RequestParam("courseId") @NotNull(message = "courseId 不能为空") @Min(value = 1, message = "courseId 必须 >= 1") Long courseId,
      @RequestParam("count") @NotNull(message = "count 不能为空") @Min(value = 1, message = "count 必须 >= 1") Integer count
  ) {
    Long teacherId = SecurityUtil.requireUserId();
    return ApiResponse.ok(questionBankService.randomPick(teacherId, courseId, count));
  }

  public static class CreateQuestionRequest {

    @NotNull(message = "courseId 不能为空")
    private Long courseId;

    private Integer questionType;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private Integer score;

    public Long getCourseId() {
      return courseId;
    }

    public void setCourseId(Long courseId) {
      this.courseId = courseId;
    }

    public Integer getQuestionType() {
      return questionType;
    }

    public void setQuestionType(Integer questionType) {
      this.questionType = questionType;
    }

    public String getQuestionText() {
      return questionText;
    }

    public void setQuestionText(String questionText) {
      this.questionText = questionText;
    }

    public String getOptionA() {
      return optionA;
    }

    public void setOptionA(String optionA) {
      this.optionA = optionA;
    }

    public String getOptionB() {
      return optionB;
    }

    public void setOptionB(String optionB) {
      this.optionB = optionB;
    }

    public String getOptionC() {
      return optionC;
    }

    public void setOptionC(String optionC) {
      this.optionC = optionC;
    }

    public String getOptionD() {
      return optionD;
    }

    public void setOptionD(String optionD) {
      this.optionD = optionD;
    }

    public String getCorrectAnswer() {
      return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
      this.correctAnswer = correctAnswer;
    }

    public Integer getScore() {
      return score;
    }

    public void setScore(Integer score) {
      this.score = score;
    }
  }

  public static class UpdateQuestionRequest {
    private Integer questionType;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private Integer score;

    public Integer getQuestionType() {
      return questionType;
    }

    public void setQuestionType(Integer questionType) {
      this.questionType = questionType;
    }

    public String getQuestionText() {
      return questionText;
    }

    public void setQuestionText(String questionText) {
      this.questionText = questionText;
    }

    public String getOptionA() {
      return optionA;
    }

    public void setOptionA(String optionA) {
      this.optionA = optionA;
    }

    public String getOptionB() {
      return optionB;
    }

    public void setOptionB(String optionB) {
      this.optionB = optionB;
    }

    public String getOptionC() {
      return optionC;
    }

    public void setOptionC(String optionC) {
      this.optionC = optionC;
    }

    public String getOptionD() {
      return optionD;
    }

    public void setOptionD(String optionD) {
      this.optionD = optionD;
    }

    public String getCorrectAnswer() {
      return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
      this.correctAnswer = correctAnswer;
    }

    public Integer getScore() {
      return score;
    }

    public void setScore(Integer score) {
      this.score = score;
    }
  }

  public static class ImportRequest {

    @NotNull(message = "targetCourseId 不能为空")
    private Long targetCourseId;

    @NotNull(message = "sourceCourseId 不能为空")
    private Long sourceCourseId;

    private List<Long> questionIds;

    public Long getTargetCourseId() {
      return targetCourseId;
    }

    public void setTargetCourseId(Long targetCourseId) {
      this.targetCourseId = targetCourseId;
    }

    public Long getSourceCourseId() {
      return sourceCourseId;
    }

    public void setSourceCourseId(Long sourceCourseId) {
      this.sourceCourseId = sourceCourseId;
    }

    public List<Long> getQuestionIds() {
      return questionIds;
    }

    public void setQuestionIds(List<Long> questionIds) {
      this.questionIds = questionIds;
    }
  }
}
