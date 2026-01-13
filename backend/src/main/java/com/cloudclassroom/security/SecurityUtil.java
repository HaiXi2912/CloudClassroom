package com.cloudclassroom.security;

import com.cloudclassroom.common.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全上下文工具：获取当前登录用户。
 */
public final class SecurityUtil {
  private SecurityUtil() {
  }

  public static Long requireUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication.getName() == null) {
      throw new BusinessException(40100, "未登录");
    }

    try {
      return Long.parseLong(authentication.getName());
    } catch (Exception e) {
      throw new BusinessException(40100, "未登录");
    }
  }

  public static boolean hasRole(String roleCode) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      return false;
    }

    return authentication.getAuthorities().stream()
        .anyMatch(a -> ("ROLE_" + roleCode).equalsIgnoreCase(a.getAuthority()));
  }

  /**
   * 获取当前登录用户的角色编码（从 authority 中解析 ROLE_xxx）。
   *
   * <p>说明：本项目最小实现阶段约定“一个用户一个角色”。
   */
  public static String getCurrentRoleCodeOrNull() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      return null;
    }

    return authentication.getAuthorities().stream()
        .map(a -> a.getAuthority() == null ? "" : a.getAuthority().trim())
        .filter(a -> a.toUpperCase().startsWith("ROLE_"))
        .map(a -> a.substring(5))
        .findFirst()
        .orElse(null);
  }
}
