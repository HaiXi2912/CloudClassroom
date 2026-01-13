package com.cloudclassroom;

import com.cloudclassroom.common.ApiResponse;
import com.cloudclassroom.modules.rbac.service.RbacService;
import com.cloudclassroom.modules.user.entity.SysUser;
import com.cloudclassroom.modules.user.service.SysUserService;
import com.cloudclassroom.security.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 管理端用户管理接口冒烟测试：
 * - ADMIN 可访问分页/详情/禁用/重置密码
 * - STUDENT 被拒绝
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminUserApiTest {

  @Autowired
  private org.springframework.boot.test.web.client.TestRestTemplate restTemplate;

  @Autowired
  private SysUserService sysUserService;

  @Autowired
  private RbacService rbacService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JwtTokenService jwtTokenService;

  @Test
  void admin_可分页查询并可修改用户() {
    // 准备：创建一个 ADMIN
    SysUser admin = new SysUser();
    admin.setUsername("admin_api_" + (System.currentTimeMillis() % 1_000_000_000L));
    admin.setNickname("管理员API测试");
    admin.setPasswordHash(passwordEncoder.encode("123456"));
    admin.setStatus(1);
    admin.setCreatedAt(LocalDateTime.now());
    admin.setUpdatedAt(LocalDateTime.now());
    admin.setDeleted(0);
    sysUserService.insert(admin);
    rbacService.bindUserRole(admin.getId(), "ADMIN");

    // 准备：创建一个普通用户（被管理对象）
    SysUser target = new SysUser();
    target.setUsername("user_api_" + (System.currentTimeMillis() % 1_000_000_000L));
    target.setNickname("用户API测试");
    target.setPasswordHash(passwordEncoder.encode("123456"));
    target.setStatus(1);
    target.setCreatedAt(LocalDateTime.now());
    target.setUpdatedAt(LocalDateTime.now());
    target.setDeleted(0);
    sysUserService.insert(target);
    rbacService.bindUserRole(target.getId(), "STUDENT");

    String token = jwtTokenService.generateToken(admin.getId(), "ADMIN");
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);

    // 1) 分页
    ResponseEntity<ApiEnvelope> pageResp = restTemplate.exchange(
        "/api/admin/users?pageNo=1&pageSize=10",
        HttpMethod.GET,
        new HttpEntity<>(headers),
        ApiEnvelope.class
    );
    assertEquals(HttpStatus.OK, pageResp.getStatusCode());
    assertNotNull(pageResp.getBody());
    assertEquals(0, pageResp.getBody().getCode());

    // 2) 详情
    ResponseEntity<ApiEnvelope> getResp = restTemplate.exchange(
        "/api/admin/users/" + target.getId(),
        HttpMethod.GET,
        new HttpEntity<>(headers),
        ApiEnvelope.class
    );
    assertEquals(HttpStatus.OK, getResp.getStatusCode());
    assertNotNull(getResp.getBody());
    assertEquals(0, getResp.getBody().getCode());

    // 3) 禁用
    Map<String, Object> body = Map.of("status", 0);
    ResponseEntity<ApiEnvelope> banResp = restTemplate.exchange(
      "/api/admin/users/" + target.getId() + "/status",
      HttpMethod.POST,
      new HttpEntity<>(body, headers),
      ApiEnvelope.class
    );
    assertEquals(HttpStatus.OK, banResp.getStatusCode());
    assertNotNull(banResp.getBody());
    assertEquals(0, banResp.getBody().getCode());

    SysUser afterBan = sysUserService.findById(target.getId());
    assertNotNull(afterBan);
    assertEquals(0, afterBan.getStatus());

    // 4) 重置密码
    ResponseEntity<ApiEnvelope> resetResp = restTemplate.exchange(
        "/api/admin/users/" + target.getId() + "/reset-password",
        HttpMethod.POST,
        new HttpEntity<>(headers),
        ApiEnvelope.class
    );
    assertEquals(HttpStatus.OK, resetResp.getStatusCode());
    assertNotNull(resetResp.getBody());
    assertEquals(0, resetResp.getBody().getCode());

    SysUser afterReset = sysUserService.findById(target.getId());
    assertNotNull(afterReset);
    assertTrue(passwordEncoder.matches("123456", afterReset.getPasswordHash()));
  }

  @Test
  void student_访问管理端应被拒绝() {
    SysUser student = new SysUser();
    student.setUsername("stu_api_" + (System.currentTimeMillis() % 1_000_000_000L));
    student.setNickname("学生API测试");
    student.setPasswordHash(passwordEncoder.encode("123456"));
    student.setStatus(1);
    student.setCreatedAt(LocalDateTime.now());
    student.setUpdatedAt(LocalDateTime.now());
    student.setDeleted(0);
    sysUserService.insert(student);
    rbacService.bindUserRole(student.getId(), "STUDENT");

    String token = jwtTokenService.generateToken(student.getId(), "STUDENT");
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);

    ResponseEntity<ApiEnvelope> resp = restTemplate.exchange(
        "/api/admin/users?pageNo=1&pageSize=10",
        HttpMethod.GET,
        new HttpEntity<>(headers),
        ApiEnvelope.class
    );

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertNotNull(resp.getBody());
    assertEquals(40300, resp.getBody().getCode());
  }

  /**
   * 为测试用的响应包装（对应 ApiResponse<T>）。
   */
  public static class ApiEnvelope {
    private int code;
    private String message;
    private Object data;
    private long timestamp;

    public int getCode() {
      return code;
    }

    public void setCode(int code) {
      this.code = code;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public Object getData() {
      return data;
    }

    public void setData(Object data) {
      this.data = data;
    }

    public long getTimestamp() {
      return timestamp;
    }

    public void setTimestamp(long timestamp) {
      this.timestamp = timestamp;
    }
  }
}
