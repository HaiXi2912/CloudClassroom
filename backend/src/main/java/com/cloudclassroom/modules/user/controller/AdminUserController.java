package com.cloudclassroom.modules.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudclassroom.common.ApiResponse;
import com.cloudclassroom.common.BusinessException;
import com.cloudclassroom.modules.user.dto.AdminUserRow;
import com.cloudclassroom.modules.user.dto.PageResult;
import com.cloudclassroom.modules.user.entity.SysUser;
import com.cloudclassroom.modules.user.service.SysUserService;
import com.cloudclassroom.security.annotation.RequireRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 管理端：用户管理接口。
 */
@RestController
@RequestMapping("/api/admin/users")
@RequireRole({"ADMIN"})
public class AdminUserController {

  private final SysUserService sysUserService;
  private final PasswordEncoder passwordEncoder;

  public AdminUserController(SysUserService sysUserService, PasswordEncoder passwordEncoder) {
    this.sysUserService = sysUserService;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * 用户分页列表（支持 username/status/roleCode 筛选）。
   */
  @GetMapping
  public ApiResponse<PageResult<AdminUserRow>> page(
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "pageNo 必须 >= 1") long pageNo,
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "pageSize 必须 >= 1") @Max(value = 100, message = "pageSize 最大 100") long pageSize,
      @RequestParam(required = false) String username,
      @RequestParam(required = false) Integer status,
      @RequestParam(required = false) String roleCode
  ) {
    IPage<AdminUserRow> page = sysUserService.pageAdminUsers(new Page<>(pageNo, pageSize), username, status, roleCode);
    PageResult<AdminUserRow> result = new PageResult<>(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords());
    return ApiResponse.ok(result);
  }

  /**
   * 根据 ID 获取用户详情（管理端）。
   */
  @GetMapping("/{id}")
  public ApiResponse<AdminUserRow> get(@PathVariable("id") Long id) {
    AdminUserRow row = sysUserService.getAdminUserRowById(id);
    if (row == null) {
      throw new BusinessException(40400, "用户不存在");
    }
    return ApiResponse.ok(row);
  }

  /**
   * 启用/禁用用户。
   */
  @PostMapping("/{id}/status")
  public ApiResponse<Void> updateStatus(@PathVariable("id") Long id, @Valid @RequestBody UpdateStatusRequest req) {
    SysUser user = sysUserService.findById(id);
    if (user == null || user.getDeleted() == null || user.getDeleted() != 0) {
      throw new BusinessException(40400, "用户不存在");
    }

    user.setStatus(req.getStatus());
    user.setUpdatedAt(LocalDateTime.now());
    sysUserService.updateById(user);
    return ApiResponse.ok();
  }

  /**
   * 重置用户密码为默认值 123456。
   */
  @PostMapping("/{id}/reset-password")
  public ApiResponse<Void> resetPassword(@PathVariable("id") Long id) {
    SysUser user = sysUserService.findById(id);
    if (user == null || user.getDeleted() == null || user.getDeleted() != 0) {
      throw new BusinessException(40400, "用户不存在");
    }

    user.setPasswordHash(passwordEncoder.encode("123456"));
    user.setUpdatedAt(LocalDateTime.now());
    sysUserService.updateById(user);
    return ApiResponse.ok();
  }

  public static class UpdateStatusRequest {
    @NotNull(message = "status 不能为空")
    @Min(value = 0, message = "status 只能为 0/1")
    @Max(value = 1, message = "status 只能为 0/1")
    private Integer status;

    public Integer getStatus() {
      return status;
    }

    public void setStatus(Integer status) {
      this.status = status;
    }
  }
}
