package com.cloudclassroom.modules.course.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudclassroom.common.BusinessException;
import com.cloudclassroom.modules.course.dto.CourseMemberRow;
import com.cloudclassroom.modules.course.dto.CourseRow;
import com.cloudclassroom.modules.course.entity.Course;
import com.cloudclassroom.modules.course.entity.CourseMember;
import com.cloudclassroom.modules.course.mapper.CourseMapper;
import com.cloudclassroom.modules.course.mapper.CourseMemberMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * 课程服务：创建课程、加入课程、成员管理。
 */
@Service
public class CourseService {

  private final CourseMapper courseMapper;
  private final CourseMemberMapper courseMemberMapper;

  public CourseService(CourseMapper courseMapper, CourseMemberMapper courseMemberMapper) {
    this.courseMapper = courseMapper;
    this.courseMemberMapper = courseMemberMapper;
  }

  public Course createCourse(Long teacherId, String courseName, String description) {
    if (teacherId == null) {
      throw new BusinessException(40100, "未登录");
    }

    Course course = new Course();
    course.setTeacherId(teacherId);
    course.setCourseName(courseName);
    course.setDescription(description);
    course.setStatus(1);
    course.setCreatedAt(LocalDateTime.now());
    course.setUpdatedAt(LocalDateTime.now());
    course.setDeleted(0);

    // 生成唯一课程码
    course.setCourseCode(generateUniqueCourseCode());

    courseMapper.insert(course);
    return course;
  }

  public List<CourseRow> listTeacherCourses(Long teacherId) {
    return courseMapper.selectTeacherCourses(teacherId);
  }

  public List<CourseRow> listStudentCourses(Long userId) {
    return courseMapper.selectStudentCourses(userId);
  }

  public void joinByCourseCode(Long userId, String courseCode) {
    if (userId == null) {
      throw new BusinessException(40100, "未登录");
    }

    Course course = courseMapper.selectOne(new LambdaQueryWrapper<Course>()
        .eq(Course::getCourseCode, courseCode)
        .eq(Course::getDeleted, 0)
        .last("LIMIT 1"));
    if (course == null || course.getStatus() == null || course.getStatus() == 0) {
      throw new BusinessException(40401, "课程不存在或已停用");
    }

    CourseMember existed = courseMemberMapper.selectOne(new LambdaQueryWrapper<CourseMember>()
        .eq(CourseMember::getCourseId, course.getId())
        .eq(CourseMember::getUserId, userId)
        .last("LIMIT 1"));
    if (existed != null) {
      // 已在班级中：直接返回；曾被移除：恢复
      if (existed.getDeleted() != null && existed.getDeleted() == 0) {
        return;
      }
      existed.setDeleted(0);
      existed.setJoinedAt(LocalDateTime.now());
      existed.setMemberRole(1);
      courseMemberMapper.updateById(existed);
      return;
    }

    CourseMember member = new CourseMember();
    member.setCourseId(course.getId());
    member.setUserId(userId);
    member.setMemberRole(1);
    member.setJoinedAt(LocalDateTime.now());
    member.setDeleted(0);
    courseMemberMapper.insert(member);
  }

  public List<CourseMemberRow> listMembers(Long teacherId, Long courseId) {
    Course course = requireTeacherOwnsCourse(teacherId, courseId);
    if (course.getStatus() == null || course.getStatus() == 0) {
      throw new BusinessException(40010, "课程已停用");
    }
    return courseMemberMapper.selectCourseMembers(courseId);
  }

  public void removeMember(Long teacherId, Long courseId, Long userId) {
    Course course = requireTeacherOwnsCourse(teacherId, courseId);
    if (course.getStatus() == null || course.getStatus() == 0) {
      throw new BusinessException(40010, "课程已停用");
    }

    CourseMember existed = courseMemberMapper.selectOne(new LambdaQueryWrapper<CourseMember>()
        .eq(CourseMember::getCourseId, courseId)
        .eq(CourseMember::getUserId, userId)
        .eq(CourseMember::getDeleted, 0)
        .last("LIMIT 1"));
    if (existed == null) {
      return;
    }

    // 使用 MyBatis-Plus 逻辑删除，保证 deleted 字段语义一致
    courseMemberMapper.deleteById(existed.getId());
  }

  private Course requireTeacherOwnsCourse(Long teacherId, Long courseId) {
    if (teacherId == null) {
      throw new BusinessException(40100, "未登录");
    }

    Course course = courseMapper.selectOne(new LambdaQueryWrapper<Course>()
        .eq(Course::getId, courseId)
        .eq(Course::getTeacherId, teacherId)
        .eq(Course::getDeleted, 0)
        .last("LIMIT 1"));

    if (course == null) {
      throw new BusinessException(40301, "无权限或课程不存在");
    }

    return course;
  }

  private String generateUniqueCourseCode() {
    // 规则：6 位大写字母+数字
    Random random = new Random();
    String alphabet = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";

    for (int i = 0; i < 20; i++) {
      StringBuilder sb = new StringBuilder();
      for (int j = 0; j < 6; j++) {
        sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
      }
      String code = sb.toString();

      Course existed = courseMapper.selectOne(new LambdaQueryWrapper<Course>()
          .eq(Course::getCourseCode, code)
          .eq(Course::getDeleted, 0)
          .last("LIMIT 1"));
      if (existed == null) {
        return code;
      }
    }

    // 极低概率兜底
    return String.valueOf(System.currentTimeMillis());
  }
}
