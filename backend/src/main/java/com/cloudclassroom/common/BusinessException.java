package com.cloudclassroom.common;

/**
 * 业务异常：用于可预期的业务错误（例如：用户名已存在）。
 */
public class BusinessException extends RuntimeException {
  private final int code;

  public BusinessException(int code, String message) {
    super(message);
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
