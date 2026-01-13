package com.cloudclassroom.modules.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudclassroom.modules.exam.entity.ExamQuestion;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考试题目 Mapper。
 */
@Mapper
public interface ExamQuestionMapper extends BaseMapper<ExamQuestion> {
}
