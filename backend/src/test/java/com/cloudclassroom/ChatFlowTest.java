package com.cloudclassroom;

import com.cloudclassroom.common.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 私聊模块联调冒烟：
 * - A 与 B 创建/获取私聊会话
 * - 双方发送消息
 * - 双方查看会话列表
 * - 拉取消息列表（增量）
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatFlowTest {

  @Autowired
  private TestRestTemplate rest;

  @Test
  void chat_private_conversation_and_messages_should_work() {
    AuthTestHelper.RegisterAndLoginResult a = AuthTestHelper.registerAndLogin(
        rest,
        "t_chat_" + (System.currentTimeMillis() % 1_000_000_000L),
        "123456",
        "老师聊天A",
        "TEACHER");

    AuthTestHelper.RegisterAndLoginResult b = AuthTestHelper.registerAndLogin(
        rest,
        "s_chat_" + (System.currentTimeMillis() % 1_000_000_000L),
        "123456",
        "学生聊天B",
        "STUDENT");

    assertNotNull(a.userId);
    assertNotNull(b.userId);

    Long conversationId = createOrGetPrivateConversation(a.token, b.userId);
    assertNotNull(conversationId);

    // A 发一条
    Map<String, Object> send1 = new HashMap<>();
    send1.put("content", "你好，我是A");
    Map<String, Object> msg1 = sendMessage(a.token, conversationId, send1);
    assertNotNull(msg1);
    assertNotNull(msg1.get("id"));

    // B 发一条
    Map<String, Object> send2 = new HashMap<>();
    send2.put("content", "收到，我是B");
    Map<String, Object> msg2 = sendMessage(b.token, conversationId, send2);
    assertNotNull(msg2);
    assertNotNull(msg2.get("id"));

    Long msg1Id = ((Number) msg1.get("id")).longValue();
    Long msg2Id = ((Number) msg2.get("id")).longValue();
    assertTrue(msg2Id > msg1Id);

    // 双方会话列表都应该能看到
    List<Map<String, Object>> listA = listConversations(a.token);
    assertTrue(listA.stream().anyMatch(r -> conversationId.equals(((Number) r.get("conversationId")).longValue())));

    List<Map<String, Object>> listB = listConversations(b.token);
    assertTrue(listB.stream().anyMatch(r -> conversationId.equals(((Number) r.get("conversationId")).longValue())));

    // 拉取全量消息
    List<Map<String, Object>> all = listMessages(a.token, conversationId, null, 50);
    assertNotNull(all);
    assertTrue(all.size() >= 2);
    assertEquals("你好，我是A", all.get(0).get("content"));
    assertEquals("收到，我是B", all.get(1).get("content"));

    // 增量拉取：afterId=msg1Id，只能拿到 msg2
    List<Map<String, Object>> inc = listMessages(a.token, conversationId, msg1Id, 50);
    assertNotNull(inc);
    assertEquals(1, inc.size());
    assertEquals(msg2Id, ((Number) inc.get(0).get("id")).longValue());
  }

  private Long createOrGetPrivateConversation(String token, Long peerUserId) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(token);

    Map<String, Object> body = new HashMap<>();
    body.put("peerUserId", peerUserId);

    @SuppressWarnings("unchecked")
    ResponseEntity<ApiResponse<?>> resp = (ResponseEntity<ApiResponse<?>>) (ResponseEntity<?>) rest.postForEntity(
        "/api/chat/conversations/private",
        new HttpEntity<>(body, headers),
      ApiResponse.class);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertNotNull(resp.getBody());
    assertEquals(0, resp.getBody().getCode());

    @SuppressWarnings("unchecked")
    Map<String, Object> data = (Map<String, Object>) resp.getBody().getData();
    assertNotNull(data);
    assertNotNull(data.get("conversationId"));
    return ((Number) data.get("conversationId")).longValue();
  }

  private Map<String, Object> sendMessage(String token, Long conversationId, Map<String, Object> body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(token);

    @SuppressWarnings("unchecked")
    ResponseEntity<ApiResponse<?>> resp = (ResponseEntity<ApiResponse<?>>) (ResponseEntity<?>) rest.postForEntity(
        "/api/chat/conversations/" + conversationId + "/messages",
        new HttpEntity<>(body, headers),
      ApiResponse.class);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertNotNull(resp.getBody());
    assertEquals(0, resp.getBody().getCode());

    @SuppressWarnings("unchecked")
    Map<String, Object> data = (Map<String, Object>) resp.getBody().getData();
    assertNotNull(data);
    return data;
  }

  private List<Map<String, Object>> listConversations(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);

    @SuppressWarnings("unchecked")
    ResponseEntity<ApiResponse<?>> resp = (ResponseEntity<ApiResponse<?>>) (ResponseEntity<?>) rest.exchange(
        "/api/chat/conversations",
        HttpMethod.GET,
        new HttpEntity<>(headers),
      ApiResponse.class);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertNotNull(resp.getBody());
    assertEquals(0, resp.getBody().getCode());

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> list = (List<Map<String, Object>>) resp.getBody().getData();
    assertNotNull(list);
    return list;
  }

  private List<Map<String, Object>> listMessages(String token, Long conversationId, Long afterId, Integer limit) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);

    String url = "/api/chat/conversations/" + conversationId + "/messages";
    if (afterId != null || limit != null) {
      url += "?";
      boolean first = true;
      if (afterId != null) {
        url += "afterId=" + afterId;
        first = false;
      }
      if (limit != null) {
        if (!first) {
          url += "&";
        }
        url += "limit=" + limit;
      }
    }

    @SuppressWarnings("unchecked")
    ResponseEntity<ApiResponse<?>> resp = (ResponseEntity<ApiResponse<?>>) (ResponseEntity<?>) rest.exchange(
        url,
        HttpMethod.GET,
        new HttpEntity<>(headers),
      ApiResponse.class);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertNotNull(resp.getBody());
    assertEquals(0, resp.getBody().getCode());

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> list = (List<Map<String, Object>>) resp.getBody().getData();
    assertNotNull(list);
    return list;
  }
}
