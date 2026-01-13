package com.cloudclassroom.modules.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudclassroom.modules.exam.entity.ExamAttemptAnswer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考试答题 Mapper。
 */
@Mapper
public interface ExamAttemptAnswerMapper extends BaseMapper<ExamAttemptAnswer> {
}
