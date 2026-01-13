package com.cloudclassroom.modules.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudclassroom.modules.task.entity.TaskLink;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务关联 Mapper。
 */
@Mapper
public interface TaskLinkMapper extends BaseMapper<TaskLink> {
}
