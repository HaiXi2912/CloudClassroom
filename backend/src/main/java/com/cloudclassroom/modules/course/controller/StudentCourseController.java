package com.cloudclassroom.modules.course.controller;

import com.cloudclassroom.common.ApiResponse;
import com.cloudclassroom.modules.course.dto.CourseRow;
import com.cloudclassroom.modules.course.service.CourseService;
import com.cloudclassroom.security.SecurityUtil;
import com.cloudclassroom.security.annotation.RequireRole;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 学生端：课程。
 */
@RestController
@RequestMapping("/api/student/courses")
@RequireRole({"STUDENT"})
public class StudentCourseController {

  private final CourseService courseService;

  public StudentCourseController(CourseService courseService) {
    this.courseService = courseService;
  }

  /**
   * 通过课程码加入。
   */
  @PostMapping("/join")
  public ApiResponse<Void> join(@Valid @RequestBody JoinCourseRequest req) {
    Long userId = SecurityUtil.requireUserId();
    courseService.joinByCourseCode(userId, req.getCourseCode());
    return ApiResponse.ok();
  }

  /**
   * 我的课程列表。
   */
  @GetMapping
  public ApiResponse<List<CourseRow>> myCourses() {
    Long userId = SecurityUtil.requireUserId();
    return ApiResponse.ok(courseService.listStudentCourses(userId));
  }

  public static class JoinCourseRequest {
    @NotBlank(message = "courseCode 不能为空")
    private String courseCode;

    public String getCourseCode() {
      return courseCode;
    }

    public void setCourseCode(String courseCode) {
      this.courseCode = courseCode;
    }
  }
}
