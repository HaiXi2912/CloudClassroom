package com.cloudclassroom.modules.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudclassroom.modules.course.dto.CourseMemberRow;
import com.cloudclassroom.modules.course.entity.CourseMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 课程成员 Mapper。
 */
@Mapper
public interface CourseMemberMapper extends BaseMapper<CourseMember> {

  @Select("""
      SELECT
        u.id AS userId,
        u.username,
        u.nickname,
        m.member_role AS memberRole,
        m.joined_at AS joinedAt
      FROM course_member m
      JOIN sys_user u ON u.id = m.user_id AND u.deleted = 0
      WHERE m.deleted = 0
        AND m.course_id = #{courseId}
      ORDER BY m.joined_at ASC
      """)
  List<CourseMemberRow> selectCourseMembers(@Param("courseId") Long courseId);
}
