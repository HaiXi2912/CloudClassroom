package com.cloudclassroom.modules.material.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudclassroom.modules.material.entity.Material;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程资料 Mapper。
 */
@Mapper
public interface MaterialMapper extends BaseMapper<Material> {
}
