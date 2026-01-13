package com.cloudclassroom.modules.course.controller;

import com.cloudclassroom.common.ApiResponse;
import com.cloudclassroom.modules.task.dto.GradeConfigRow;
import com.cloudclassroom.modules.task.dto.StudentGradeRow;
import com.cloudclassroom.modules.task.service.CourseGradeService;
import com.cloudclassroom.security.SecurityUtil;
import com.cloudclassroom.security.annotation.RequireRole;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 老师端：课程成绩权重与成绩汇总。
 */
@RestController
@RequestMapping("/api/teacher/courses")
@RequireRole({"TEACHER"})
public class TeacherGradeController {

  private final CourseGradeService courseGradeService;

  public TeacherGradeController(CourseGradeService courseGradeService) {
    this.courseGradeService = courseGradeService;
  }

  @GetMapping("/{courseId}/grade-config")
  public ApiResponse<GradeConfigRow> getConfig(@PathVariable("courseId") @NotNull @Min(1) Long courseId) {
    Long teacherId = SecurityUtil.requireUserId();
    return ApiResponse.ok(courseGradeService.getConfig(teacherId, courseId));
  }

  @PostMapping("/{courseId}/grade-config")
  public ApiResponse<Void> upsertConfig(@PathVariable("courseId") @NotNull @Min(1) Long courseId, @Valid @RequestBody UpsertConfigRequest req) {
    Long teacherId = SecurityUtil.requireUserId();
    courseGradeService.upsertConfig(teacherId, courseId, req.getWeightTask(), req.getWeightAssignment(), req.getWeightExam());
    return ApiResponse.ok();
  }

  @GetMapping("/{courseId}/grades")
  public ApiResponse<List<StudentGradeRow>> listGrades(@PathVariable("courseId") @NotNull @Min(1) Long courseId) {
    Long teacherId = SecurityUtil.requireUserId();
    return ApiResponse.ok(courseGradeService.listStudentGrades(teacherId, courseId));
  }

  public static class UpsertConfigRequest {

    @NotNull(message = "weightTask 不能为空")
    @Min(value = 0, message = "weightTask 必须在 0-100")
    @Max(value = 100, message = "weightTask 必须在 0-100")
    private Integer weightTask;

    @NotNull(message = "weightAssignment 不能为空")
    @Min(value = 0, message = "weightAssignment 必须在 0-100")
    @Max(value = 100, message = "weightAssignment 必须在 0-100")
    private Integer weightAssignment;

    @NotNull(message = "weightExam 不能为空")
    @Min(value = 0, message = "weightExam 必须在 0-100")
    @Max(value = 100, message = "weightExam 必须在 0-100")
    private Integer weightExam;

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
}
