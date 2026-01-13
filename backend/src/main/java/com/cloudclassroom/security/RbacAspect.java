package com.cloudclassroom.security;

import com.cloudclassroom.common.BusinessException;
import com.cloudclassroom.modules.rbac.service.RbacService;
import com.cloudclassroom.security.annotation.RequirePerm;
import com.cloudclassroom.security.annotation.RequireRole;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * RBAC 注解切面：处理 @RequireRole。
 */
@Aspect
@Component
public class RbacAspect {

  private final RbacService rbacService;

  public RbacAspect(RbacService rbacService) {
    this.rbacService = rbacService;
  }

  @Around("@within(com.cloudclassroom.security.annotation.RequireRole) || @annotation(com.cloudclassroom.security.annotation.RequireRole)"
      + " || @within(com.cloudclassroom.security.annotation.RequirePerm) || @annotation(com.cloudclassroom.security.annotation.RequirePerm)")
  public Object around(ProceedingJoinPoint pjp) throws Throwable {
    MethodSignature signature = (MethodSignature) pjp.getSignature();
    Method method = signature.getMethod();

    RequireRole requireRole = method.getAnnotation(RequireRole.class);
    if (requireRole == null) {
      requireRole = pjp.getTarget().getClass().getAnnotation(RequireRole.class);
    }

    RequirePerm requirePerm = method.getAnnotation(RequirePerm.class);
    if (requirePerm == null) {
      requirePerm = pjp.getTarget().getClass().getAnnotation(RequirePerm.class);
    }

    if (requireRole == null && requirePerm == null) {
      return pjp.proceed();
    }

    // 未登录直接拒绝
    SecurityUtil.requireUserId();

    if (requireRole != null) {
      boolean ok = Arrays.stream(requireRole.value()).anyMatch(SecurityUtil::hasRole);
      if (!ok) {
        throw new BusinessException(40300, "无权限");
      }
    }

    if (requirePerm != null) {
      String roleCode = SecurityUtil.getCurrentRoleCodeOrNull();
      boolean ok = roleCode != null && Arrays.stream(requirePerm.value()).anyMatch(p -> rbacService.roleHasPerm(roleCode, p));
      if (!ok) {
        throw new BusinessException(40300, "无权限");
      }
    }

    return pjp.proceed();
  }
}
