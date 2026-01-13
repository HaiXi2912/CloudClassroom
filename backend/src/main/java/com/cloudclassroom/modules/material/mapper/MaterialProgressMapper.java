package com.cloudclassroom.modules.material.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudclassroom.modules.material.entity.MaterialProgress;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学习进度 Mapper。
 */
@Mapper
public interface MaterialProgressMapper extends BaseMapper<MaterialProgress> {
}
