package com.cloudclassroom.modules.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudclassroom.modules.exam.entity.ExamAttempt;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考试作答记录 Mapper。
 */
@Mapper
public interface ExamAttemptMapper extends BaseMapper<ExamAttempt> {
}
