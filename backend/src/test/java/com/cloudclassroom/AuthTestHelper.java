package com.cloudclassroom;

import com.cloudclassroom.common.ApiResponse;
import org.springframework.http.*;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试辅助：注册并登录，返回 token。
 */
public class AuthTestHelper {

  public static class RegisterAndLoginResult {
    public final Long userId;
    public final String token;

    public RegisterAndLoginResult(Long userId, String token) {
      this.userId = userId;
      this.token = token;
    }
  }

  public static RegisterAndLoginResult registerAndLogin(TestRestTemplate rest,
                                                        String username,
                                                        String password,
                                                        String nickname,
                                                        String role) {

    // 注册
    Map<String, Object> reg = new HashMap<>();
    reg.put("username", username);
    reg.put("password", password);
    reg.put("nickname", nickname);
    reg.put("role", role);

    ResponseEntity<ApiResponse> regResp = rest.postForEntity("/api/auth/register", reg, ApiResponse.class);
    assertEquals(HttpStatus.OK, regResp.getStatusCode());
    assertNotNull(regResp.getBody());
    assertEquals(0, regResp.getBody().getCode());

    // 登录
    Map<String, Object> login = new HashMap<>();
    login.put("username", username);
    login.put("password", password);

    ResponseEntity<ApiResponse> loginResp = rest.postForEntity("/api/auth/login", login, ApiResponse.class);
    assertEquals(HttpStatus.OK, loginResp.getStatusCode());
    assertNotNull(loginResp.getBody());
    assertEquals(0, loginResp.getBody().getCode());

    Map<String, Object> data = (Map<String, Object>) loginResp.getBody().getData();
    assertNotNull(data);
    String token = (String) data.get("token");
    assertNotNull(token);

    Long userId = null;
    Object userIdObj = data.get("userId");
    if (userIdObj instanceof Number) {
      userId = ((Number) userIdObj).longValue();
    }

    return new RegisterAndLoginResult(userId, token);
  }
}
