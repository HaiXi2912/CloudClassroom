package com.cloudclassroom.modules.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudclassroom.modules.task.entity.CourseGradeConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程成绩权重配置 Mapper。
 */
@Mapper
public interface CourseGradeConfigMapper extends BaseMapper<CourseGradeConfig> {
}
