package com.cloudclassroom.modules.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudclassroom.modules.rbac.entity.SysPermission;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 权限点 Mapper。
 */
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

  /**
   * 判断某角色是否拥有某权限点。
   */
  @Select("""
      SELECT COUNT(1)
      FROM sys_role r
      JOIN sys_role_permission rp ON rp.role_id = r.id AND rp.deleted = 0
      JOIN sys_permission p ON p.id = rp.permission_id AND p.deleted = 0
      WHERE r.deleted = 0
        AND r.role_code = #{roleCode}
        AND p.perm_code = #{permCode}
      """)
  int countRoleHasPerm(@Param("roleCode") String roleCode, @Param("permCode") String permCode);
}
