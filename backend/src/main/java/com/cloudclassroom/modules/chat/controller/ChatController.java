package com.cloudclassroom.modules.chat.controller;

import com.cloudclassroom.common.ApiResponse;
import com.cloudclassroom.modules.chat.dto.ConversationRow;
import com.cloudclassroom.modules.chat.dto.MessageRow;
import com.cloudclassroom.modules.chat.service.ChatService;
import com.cloudclassroom.security.SecurityUtil;
import com.cloudclassroom.security.annotation.RequirePerm;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 私聊接口（最小实现）。
 */
@RestController
@RequestMapping("/api/chat")
@RequirePerm({"chat:send"})
public class ChatController {

  private final ChatService chatService;

  public ChatController(ChatService chatService) {
    this.chatService = chatService;
  }

  /**
   * 获取或创建“我与某人”的私聊会话。
   */
  @PostMapping("/conversations/private")
  public ApiResponse<CreateOrGetConversationResponse> createOrGetPrivate(@Valid @RequestBody CreateOrGetPrivateConversationRequest req) {
    Long userId = SecurityUtil.requireUserId();
    Long cid = chatService.getOrCreatePrivateConversation(userId, req.getPeerUserId());
    CreateOrGetConversationResponse resp = new CreateOrGetConversationResponse();
    resp.setConversationId(cid);
    return ApiResponse.ok(resp);
  }

  /**
   * 列出我的会话。
   */
  @GetMapping("/conversations")
  public ApiResponse<List<ConversationRow>> listMyConversations() {
    Long userId = SecurityUtil.requireUserId();
    return ApiResponse.ok(chatService.listMyConversations(userId));
  }

  /**
   * 拉取会话消息（按消息ID增量）。
   */
  @GetMapping("/conversations/{conversationId}/messages")
  public ApiResponse<List<MessageRow>> listMessages(@PathVariable Long conversationId,
                                                    @RequestParam(value = "afterId", required = false) Long afterId,
                                                    @RequestParam(value = "limit", required = false) Integer limit) {
    Long userId = SecurityUtil.requireUserId();
    return ApiResponse.ok(chatService.listMessages(userId, conversationId, afterId, limit));
  }

  /**
   * 发送消息。
   */
  @PostMapping("/conversations/{conversationId}/messages")
  public ApiResponse<MessageRow> send(@PathVariable Long conversationId, @Valid @RequestBody SendMessageRequest req) {
    Long userId = SecurityUtil.requireUserId();
    return ApiResponse.ok(chatService.sendMessage(userId, conversationId, req.getContent()));
  }

  public static class CreateOrGetPrivateConversationRequest {
    @NotNull(message = "peerUserId 不能为空")
    private Long peerUserId;

    public Long getPeerUserId() {
      return peerUserId;
    }

    public void setPeerUserId(Long peerUserId) {
      this.peerUserId = peerUserId;
    }
  }

  public static class CreateOrGetConversationResponse {
    private Long conversationId;

    public Long getConversationId() {
      return conversationId;
    }

    public void setConversationId(Long conversationId) {
      this.conversationId = conversationId;
    }
  }

  public static class SendMessageRequest {
    @NotBlank(message = "content 不能为空")
    private String content;

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }
  }
}
