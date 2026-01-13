package com.cloudclassroom.modules.rbac.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudclassroom.modules.rbac.entity.SysRole;
import com.cloudclassroom.modules.rbac.entity.SysUserRole;
import com.cloudclassroom.modules.rbac.mapper.SysPermissionMapper;
import com.cloudclassroom.modules.rbac.mapper.SysRoleMapper;
import com.cloudclassroom.modules.rbac.mapper.SysUserRoleMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * RBAC 服务：查询用户角色、绑定角色。
 */
@Service
public class RbacService {
  private final SysRoleMapper sysRoleMapper;
  private final SysUserRoleMapper sysUserRoleMapper;
  private final SysPermissionMapper sysPermissionMapper;

  public RbacService(SysRoleMapper sysRoleMapper, SysUserRoleMapper sysUserRoleMapper, SysPermissionMapper sysPermissionMapper) {
    this.sysRoleMapper = sysRoleMapper;
    this.sysUserRoleMapper = sysUserRoleMapper;
    this.sysPermissionMapper = sysPermissionMapper;
  }

  public SysRole findRoleByCode(String roleCode) {
    return sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>()
        .eq(SysRole::getRoleCode, roleCode)
        .eq(SysRole::getDeleted, 0)
        .last("LIMIT 1"));
  }

  public String findUserRoleCode(Long userId) {
    // 最小实现：一个用户只绑定一个角色
    SysUserRole ur = sysUserRoleMapper.selectOne(new LambdaQueryWrapper<SysUserRole>()
        .eq(SysUserRole::getUserId, userId)
        .eq(SysUserRole::getDeleted, 0)
        .last("LIMIT 1"));

    if (ur == null) {
      return null;
    }

    SysRole role = sysRoleMapper.selectById(ur.getRoleId());
    if (role == null || role.getDeleted() == null || role.getDeleted() != 0) {
      return null;
    }

    return role.getRoleCode();
  }

  public void bindUserRole(Long userId, String roleCode) {
    SysRole role = findRoleByCode(roleCode);
    if (role == null) {
      return;
    }

    SysUserRole existed = sysUserRoleMapper.selectOne(new LambdaQueryWrapper<SysUserRole>()
        .eq(SysUserRole::getUserId, userId)
        .eq(SysUserRole::getDeleted, 0)
        .last("LIMIT 1"));

    if (existed != null) {
      existed.setRoleId(role.getId());
      sysUserRoleMapper.updateById(existed);
      return;
    }

    SysUserRole ur = new SysUserRole();
    ur.setUserId(userId);
    ur.setRoleId(role.getId());
    ur.setCreatedAt(LocalDateTime.now());
    ur.setDeleted(0);
    sysUserRoleMapper.insert(ur);
  }

  /**
   * 判断角色是否拥有权限点。
   *
   * <p>约定：ADMIN 默认拥有全部权限。
   */
  public boolean roleHasPerm(String roleCode, String permCode) {
    if (roleCode == null || permCode == null) {
      return false;
    }
    if ("ADMIN".equalsIgnoreCase(roleCode)) {
      return true;
    }
    return sysPermissionMapper.countRoleHasPerm(roleCode, permCode) > 0;
  }
}
