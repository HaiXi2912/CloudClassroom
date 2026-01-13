package com.cloudclassroom.modules.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudclassroom.modules.course.dto.CourseRow;
import com.cloudclassroom.modules.course.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 课程 Mapper。
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {

  @Select("""
      SELECT
        c.id,
        c.teacher_id AS teacherId,
        c.course_name AS courseName,
        c.course_code AS courseCode,
        c.description,
        c.status,
        c.created_at AS createdAt
      FROM course c
      WHERE c.deleted = 0
        AND c.teacher_id = #{teacherId}
      ORDER BY c.id DESC
      """)
  List<CourseRow> selectTeacherCourses(@Param("teacherId") Long teacherId);

  @Select("""
      SELECT
        c.id,
        c.teacher_id AS teacherId,
        c.course_name AS courseName,
        c.course_code AS courseCode,
        c.description,
        c.status,
        c.created_at AS createdAt
      FROM course_member m
      JOIN course c ON c.id = m.course_id AND c.deleted = 0
      WHERE m.deleted = 0
        AND m.user_id = #{userId}
      ORDER BY m.joined_at DESC
      """)
  List<CourseRow> selectStudentCourses(@Param("userId") Long userId);
}
