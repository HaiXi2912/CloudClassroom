package com.cloudclassroom.modules.course.controller;

import com.cloudclassroom.common.ApiResponse;
import com.cloudclassroom.modules.task.dto.DashboardRow;
import com.cloudclassroom.modules.task.service.DashboardService;
import com.cloudclassroom.security.SecurityUtil;
import com.cloudclassroom.security.annotation.RequireRole;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 学生端：课程学习看板。
 */
@RestController
@RequestMapping("/api/student/courses")
@RequireRole({"STUDENT"})
public class StudentDashboardController {

  private final DashboardService dashboardService;

  public StudentDashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @GetMapping("/{courseId}/dashboard")
  public ApiResponse<DashboardRow> dashboard(@PathVariable("courseId") @NotNull @Min(1) Long courseId) {
    Long studentId = SecurityUtil.requireUserId();
    return ApiResponse.ok(dashboardService.getStudentDashboard(studentId, courseId));
  }
}
