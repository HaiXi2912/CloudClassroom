package com.cloudclassroom.modules.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cloudclassroom.modules.user.entity.SysUser;
import com.cloudclassroom.modules.user.dto.AdminUserRow;
import com.cloudclassroom.modules.user.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

/**
 * 用户业务服务（最小实现）。
 */
@Service
public class SysUserService {
  private final SysUserMapper sysUserMapper;

  public SysUserService(SysUserMapper sysUserMapper) {
    this.sysUserMapper = sysUserMapper;
  }

  public SysUser findByUsername(String username) {
    return sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
        .eq(SysUser::getUsername, username)
        .eq(SysUser::getDeleted, 0)
        .last("LIMIT 1"));
  }

  public SysUser findById(Long id) {
    return sysUserMapper.selectById(id);
  }

  public void insert(SysUser user) {
    sysUserMapper.insert(user);
  }

  public void updateById(SysUser user) {
    sysUserMapper.updateById(user);
  }

  public IPage<AdminUserRow> pageAdminUsers(IPage<AdminUserRow> page, String username, Integer status, String roleCode) {
    return sysUserMapper.selectAdminUserPage(page, username, status, roleCode);
  }

  public AdminUserRow getAdminUserRowById(Long id) {
    return sysUserMapper.selectAdminUserRowById(id);
  }
}
