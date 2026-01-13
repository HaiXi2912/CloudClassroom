package com.cloudclassroom;

import com.cloudclassroom.common.ApiResponse;
import com.cloudclassroom.modules.rbac.service.RbacService;
import com.cloudclassroom.modules.user.entity.SysUser;
import com.cloudclassroom.modules.user.service.SysUserService;
import com.cloudclassroom.security.JwtTokenService;
import com.cloudclassroom.security.annotation.RequireRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 联调冒烟：验证注册/登录/me 以及 @RequireRole 鉴权链路可用。
 *
 * <p>说明：这里直接连接本地 MySQL（application.yml 默认配置），以符合“确保跑得通”的要求。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(AuthFlowTest.TestEndpoints.class)
public class AuthFlowTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private SysUserService sysUserService;

  @Autowired
  private RbacService rbacService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JwtTokenService jwtTokenService;

  @Test
  void 注册_登录_me_角色应一致() {
    String username = "tea" + (System.currentTimeMillis() % 1_000_000_000L);
    String password = "123456";

    // 1) 注册老师
    Map<String, Object> registerBody = Map.of(
        "username", username,
        "password", password,
        "nickname", "老师测试",
        "role", "TEACHER"
    );
    ResponseEntity<ApiEnvelope> registerResp = restTemplate.postForEntity(
        "/api/auth/register",
        registerBody,
        ApiEnvelope.class
    );
    assertEquals(HttpStatus.OK, registerResp.getStatusCode());
    assertNotNull(registerResp.getBody());
    assertEquals(0, registerResp.getBody().getCode());

    // 2) 登录
    Map<String, Object> loginBody = Map.of(
        "username", username,
        "password", password
    );
    ResponseEntity<ApiEnvelope> loginResp = restTemplate.postForEntity(
        "/api/auth/login",
        loginBody,
        ApiEnvelope.class
    );
    assertEquals(HttpStatus.OK, loginResp.getStatusCode());
    assertNotNull(loginResp.getBody());
    assertEquals(0, loginResp.getBody().getCode());

    @SuppressWarnings("unchecked")
    Map<String, Object> loginData = (Map<String, Object>) loginResp.getBody().getData();
    assertNotNull(loginData);
    assertEquals("TEACHER", loginData.get("role"));
    assertNotNull(loginData.get("token"));

    String token = String.valueOf(loginData.get("token"));

    // 3) /me
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);

    ResponseEntity<ApiEnvelope> meResp = restTemplate.exchange(
        "/api/auth/me",
        HttpMethod.GET,
        new HttpEntity<>(headers),
        ApiEnvelope.class
    );
    assertEquals(HttpStatus.OK, meResp.getStatusCode());
    assertNotNull(meResp.getBody());
    assertEquals(0, meResp.getBody().getCode());

    @SuppressWarnings("unchecked")
    Map<String, Object> meData = (Map<String, Object>) meResp.getBody().getData();
    assertNotNull(meData);
    assertEquals("TEACHER", meData.get("role"));
  }

  @Test
  void RequireRole_管理员可访问_学生不可访问() {
    // 1) 创建一个管理员账号（测试专用），并绑定 ADMIN 角色
    String username = "admin" + (System.currentTimeMillis() % 1_000_000_000L);
    SysUser user = new SysUser();
    user.setUsername(username);
    user.setNickname("管理员测试");
    user.setPasswordHash(passwordEncoder.encode("123456"));
    user.setStatus(1);
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());
    user.setDeleted(0);
    sysUserService.insert(user);
    rbacService.bindUserRole(user.getId(), "ADMIN");

    // 2) 管理员 token 调用 admin-only
    String adminToken = jwtTokenService.generateToken(user.getId(), "ADMIN");
    HttpHeaders adminHeaders = new HttpHeaders();
    adminHeaders.setBearerAuth(adminToken);

    ResponseEntity<ApiEnvelope> okResp = restTemplate.exchange(
        "/api/test/admin-only",
        HttpMethod.GET,
        new HttpEntity<>(adminHeaders),
        ApiEnvelope.class
    );
    assertEquals(HttpStatus.OK, okResp.getStatusCode());
    assertNotNull(okResp.getBody());
    assertEquals(0, okResp.getBody().getCode());

    // 3) 学生 token 调用 admin-only（应被拒绝）
    String studentToken = jwtTokenService.generateToken(user.getId(), "STUDENT");
    HttpHeaders studentHeaders = new HttpHeaders();
    studentHeaders.setBearerAuth(studentToken);

    ResponseEntity<ApiEnvelope> denyResp = restTemplate.exchange(
        "/api/test/admin-only",
        HttpMethod.GET,
        new HttpEntity<>(studentHeaders),
        ApiEnvelope.class
    );
    assertEquals(HttpStatus.OK, denyResp.getStatusCode());
    assertNotNull(denyResp.getBody());
    assertEquals(40300, denyResp.getBody().getCode());
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

  @TestConfiguration
  static class TestEndpoints {

    @RestController
    @RequestMapping("/api/test")
    static class TestRbacController {
      @GetMapping("/admin-only")
      @RequireRole("ADMIN")
      public ApiResponse<String> adminOnly() {
        return ApiResponse.ok("ok");
      }
    }
  }
}
