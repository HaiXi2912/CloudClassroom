package com.cloudclassroom.modules.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudclassroom.modules.task.entity.TaskStep;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务步骤 Mapper。
 */
@Mapper
public interface TaskStepMapper extends BaseMapper<TaskStep> {
}
