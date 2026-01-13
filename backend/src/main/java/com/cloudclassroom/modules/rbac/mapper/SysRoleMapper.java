package com.cloudclassroom.modules.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudclassroom.modules.rbac.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色表 Mapper。
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
}
