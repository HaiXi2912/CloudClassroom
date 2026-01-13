package com.cloudclassroom.modules.assignment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudclassroom.modules.assignment.entity.AssignmentSubmissionAnswer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 作业答题 Mapper。
 */
@Mapper
public interface AssignmentSubmissionAnswerMapper extends BaseMapper<AssignmentSubmissionAnswer> {
}
