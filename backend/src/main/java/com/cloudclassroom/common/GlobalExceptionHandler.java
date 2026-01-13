package com.cloudclassroom.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLSyntaxErrorException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.ConstraintViolationException;

/**
 * 全局异常处理：保证 Controller 层始终返回统一 JSON。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  private static final Pattern MYSQL_MISSING_TABLE_PATTERN =
      Pattern.compile("Table '([^']+)' doesn't exist", Pattern.CASE_INSENSITIVE);

    private static final Pattern MYSQL_UNKNOWN_COLUMN_PATTERN =
      Pattern.compile("Unknown column '([^']+)'", Pattern.CASE_INSENSITIVE);

  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<Void> handleBusiness(BusinessException e) {
    return ApiResponse.fail(e.getCode(), e.getMessage());
  }

  @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, ConstraintViolationException.class})
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<Void> handleValidation(Exception e) {
    String msg = "参数校验失败";
    if (e instanceof MethodArgumentNotValidException) {
      MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
      if (ex.getBindingResult().getFieldError() != null) {
        msg = ex.getBindingResult().getFieldError().getDefaultMessage();
      }
    } else if (e instanceof BindException) {
      BindException ex = (BindException) e;
      if (ex.getBindingResult().getFieldError() != null) {
        msg = ex.getBindingResult().getFieldError().getDefaultMessage();
      }
    } else if (e instanceof ConstraintViolationException) {
      msg = ((ConstraintViolationException) e).getMessage();
    }

    return ApiResponse.fail(40001, msg);
  }

  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<Void> handleAuth(AuthenticationException e) {
    return ApiResponse.fail(40100, "未登录");
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<Void> handleDenied(AccessDeniedException e) {
    return ApiResponse.fail(40300, "无权限");
  }

  @ExceptionHandler(BadSqlGrammarException.class)
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<Void> handleBadSql(BadSqlGrammarException e) {
    log.error("Bad SQL", e);

    Throwable cause = e.getCause();
    if (cause instanceof SQLSyntaxErrorException) {
      String msg = cause.getMessage();
      if (msg != null) {
        Matcher columnMatcher = MYSQL_UNKNOWN_COLUMN_PATTERN.matcher(msg);
        if (columnMatcher.find()) {
          String col = columnMatcher.group(1);
          if (col != null) {
            String c = col.toLowerCase();
            if ("progress_percent".equals(c)
                || "position_seconds".equals(c)
                || "duration_seconds".equals(c)
                || "last_reported_at".equals(c)) {
              return ApiResponse.fail(50010, "数据库缺少进度字段（" + col + "），请执行 sql/007_add_attachment_progress_fields.sql");
            }
            if ("step_type".equals(c) || "parent_id".equals(c)) {
              return ApiResponse.fail(50010, "数据库缺少大纲分层字段（" + col + "），请执行 sql/008_add_task_step_outline_fields.sql");
            }
          }
        }

        Matcher matcher = MYSQL_MISSING_TABLE_PATTERN.matcher(msg);
        if (matcher.find()) {
          String fullTableName = matcher.group(1);
          String tableName = fullTableName;
          int dot = fullTableName.lastIndexOf('.');
          if (dot >= 0 && dot < fullTableName.length() - 1) {
            tableName = fullTableName.substring(dot + 1);
          }

          String script = null;
          if ("task".equalsIgnoreCase(tableName)
              || tableName.toLowerCase().startsWith("task_")
              || "course_grade_config".equalsIgnoreCase(tableName)) {
            script = "sql/004_add_task_and_grade_tables.sql";
          } else if (tableName.toLowerCase().startsWith("task_step_attachment")
              || tableName.toLowerCase().startsWith("task_step_content_progress")
              || tableName.toLowerCase().startsWith("task_step_attachment_progress")) {
            script = "sql/006_add_task_step_attachments_and_progress.sql";
          } else if ("question_bank_question".equalsIgnoreCase(tableName)) {
            script = "sql/005_add_question_bank_tables.sql";
          }

          if (script != null) {
            return ApiResponse.fail(50010, "数据库缺少数据表（" + tableName + "），请执行 " + script);
          }
          return ApiResponse.fail(
              50010,
              "数据库缺少数据表（" + tableName + "），请执行 sql 目录下最新增量脚本，或重新按顺序执行 001~003 初始化脚本");
        }
      }
    }

    return ApiResponse.fail(50010, "数据库结构不匹配或 SQL 语法错误");
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<Void> handleUnknown(Exception e) {
    log.error("Unhandled exception", e);
    return ApiResponse.fail(50000, "服务器内部错误");
  }
}
