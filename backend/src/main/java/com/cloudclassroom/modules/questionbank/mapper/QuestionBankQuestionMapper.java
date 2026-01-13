package com.cloudclassroom.modules.questionbank.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudclassroom.modules.questionbank.entity.QuestionBankQuestion;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuestionBankQuestionMapper extends BaseMapper<QuestionBankQuestion> {
}
