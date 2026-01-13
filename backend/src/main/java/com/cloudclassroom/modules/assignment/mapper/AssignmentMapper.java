package com.cloudclassroom.modules.assignment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudclassroom.modules.assignment.entity.Assignment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 作业 Mapper。
 */
@Mapper
public interface AssignmentMapper extends BaseMapper<Assignment> {
}
