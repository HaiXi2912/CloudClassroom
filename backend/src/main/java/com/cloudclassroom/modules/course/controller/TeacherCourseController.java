package com.cloudclassroom.modules.course.controller;

import com.cloudclassroom.common.ApiResponse;
import com.cloudclassroom.modules.course.dto.CourseMemberRow;
import com.cloudclassroom.modules.course.dto.CourseRow;
import com.cloudclassroom.modules.course.entity.Course;
import com.cloudclassroom.modules.course.service.CourseService;
import com.cloudclassroom.security.SecurityUtil;
import com.cloudclassroom.security.annotation.RequirePerm;
import com.cloudclassroom.security.annotation.RequireRole;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 老师端：课程管理。
 */
@RestController
@RequestMapping("/api/teacher/courses")
@RequireRole({"TEACHER"})
public class TeacherCourseController {

  private final CourseService courseService;

  public TeacherCourseController(CourseService courseService) {
    this.courseService = courseService;
  }

  /**
   * 创建课程。
   */
  @PostMapping
  @RequirePerm({"course:create"})
  public ApiResponse<Course> create(@Valid @RequestBody CreateCourseRequest req) {
    Long teacherId = SecurityUtil.requireUserId();
    Course course = courseService.createCourse(teacherId, req.getCourseName(), req.getDescription());
    return ApiResponse.ok(course);
  }

  /**
   * 老师自己的课程列表。
   */
  @GetMapping
  public ApiResponse<List<CourseRow>> myCourses() {
    Long teacherId = SecurityUtil.requireUserId();
    return ApiResponse.ok(courseService.listTeacherCourses(teacherId));
  }

  /**
   * 课程成员列表。
   */
  @GetMapping("/{courseId}/members")
  @RequirePerm({"course:manage_members"})
  public ApiResponse<List<CourseMemberRow>> members(@PathVariable("courseId") Long courseId) {
    Long teacherId = SecurityUtil.requireUserId();
    return ApiResponse.ok(courseService.listMembers(teacherId, courseId));
  }

  /**
   * 移除成员。
   */
  @PostMapping("/{courseId}/members/remove")
  @RequirePerm({"course:manage_members"})
  public ApiResponse<Void> removeMember(@PathVariable("courseId") Long courseId, @Valid @RequestBody RemoveMemberRequest req) {
    Long teacherId = SecurityUtil.requireUserId();
    courseService.removeMember(teacherId, courseId, req.getUserId());
    return ApiResponse.ok();
  }

  public static class CreateCourseRequest {
    @NotBlank(message = "courseName 不能为空")
    private String courseName;

    private String description;

    public String getCourseName() {
      return courseName;
    }

    public void setCourseName(String courseName) {
      this.courseName = courseName;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }
  }

  public static class RemoveMemberRequest {
    @NotNull(message = "userId 不能为空")
    private Long userId;

    public Long getUserId() {
      return userId;
    }

    public void setUserId(Long userId) {
      this.userId = userId;
    }
  }
}
