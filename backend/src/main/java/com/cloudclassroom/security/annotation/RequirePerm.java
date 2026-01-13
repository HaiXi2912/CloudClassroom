package com.cloudclassroom.security.annotation;

import java.lang.annotation.*;

/**
 * 权限点要求注解：用于 Controller 方法级/类级鉴权。
 *
 * <p>说明：
 * - permCode 建议使用 "模块:动作" 风格，例如：course:create
 * - 当 value 包含多个权限点时：满足任意一个即可通过（OR 逻辑）
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePerm {
  String[] value();
}
