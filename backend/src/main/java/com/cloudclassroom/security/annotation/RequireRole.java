package com.cloudclassroom.security.annotation;

import java.lang.annotation.*;

/**
 * 角色要求注解：用于 Controller 方法级/类级鉴权。
 *
 * <p>示例：@RequireRole({"ADMIN"})
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {
  String[] value();
}
