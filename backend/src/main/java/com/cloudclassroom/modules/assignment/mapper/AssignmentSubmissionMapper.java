package com.cloudclassroom.modules.assignment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudclassroom.modules.assignment.entity.AssignmentSubmission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 作业提交 Mapper。
 */
@Mapper
public interface AssignmentSubmissionMapper extends BaseMapper<AssignmentSubmission> {
}
