package com.cloudclassroom.modules.assignment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudclassroom.modules.assignment.entity.AssignmentQuestion;
import org.apache.ibatis.annotations.Mapper;

/**
 * 作业题目 Mapper。
 */
@Mapper
public interface AssignmentQuestionMapper extends BaseMapper<AssignmentQuestion> {
}
