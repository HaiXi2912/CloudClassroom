package com.cloudclassroom.modules.chat.controller;

import com.cloudclassroom.common.ApiResponse;
import com.cloudclassroom.modules.chat.dto.ChatUserRow;
import com.cloudclassroom.modules.chat.dto.FriendRequestRow;
import com.cloudclassroom.modules.chat.service.ChatSocialService;
import com.cloudclassroom.security.SecurityUtil;
import com.cloudclassroom.security.annotation.RequirePerm;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 通讯录/好友接口。
 */
@RestController
@RequestMapping("/api/chat")
@RequirePerm({"chat:send"})
public class ChatSocialController {

  private final ChatSocialService chatSocialService;

  public ChatSocialController(ChatSocialService chatSocialService) {
    this.chatSocialService = chatSocialService;
  }

  /**
   * 通讯录：所有用户（学生/老师等）。支持 keyword 搜索（用户名/昵称）。
   */
  @GetMapping("/users")
  public ApiResponse<List<ChatUserRow>> listUsers(@RequestParam(value = "keyword", required = false) String keyword) {
    Long myUserId = SecurityUtil.requireUserId();
    return ApiResponse.ok(chatSocialService.listAllUsersWithRelation(myUserId, keyword));
  }

  /**
   * 发起好友申请（幂等）。
   */
  @PostMapping("/friend-requests")
  public ApiResponse<Void> sendFriendRequest(@Valid @RequestBody SendFriendRequest req) {
    Long myUserId = SecurityUtil.requireUserId();
    chatSocialService.sendFriendRequest(myUserId, req.getToUserId());
    return ApiResponse.ok();
  }

  /**
   * 我收到的待处理好友申请。
   */
  @GetMapping("/friend-requests/incoming")
  public ApiResponse<List<FriendRequestRow>> listIncoming() {
    Long myUserId = SecurityUtil.requireUserId();
    return ApiResponse.ok(chatSocialService.listIncomingRequests(myUserId));
  }

  @PostMapping("/friend-requests/{id}/accept")
  public ApiResponse<Void> accept(@PathVariable("id") Long id) {
    Long myUserId = SecurityUtil.requireUserId();
    chatSocialService.acceptRequest(myUserId, id);
    return ApiResponse.ok();
  }

  @PostMapping("/friend-requests/{id}/reject")
  public ApiResponse<Void> reject(@PathVariable("id") Long id) {
    Long myUserId = SecurityUtil.requireUserId();
    chatSocialService.rejectRequest(myUserId, id);
    return ApiResponse.ok();
  }

  @DeleteMapping("/friends/{userId}")
  public ApiResponse<Void> deleteFriend(@PathVariable("userId") Long userId) {
    Long myUserId = SecurityUtil.requireUserId();
    chatSocialService.deleteFriend(myUserId, userId);
    return ApiResponse.ok();
  }

  public static class SendFriendRequest {
    @NotNull(message = "toUserId 不能为空")
    private Long toUserId;

    public Long getToUserId() {
      return toUserId;
    }

    public void setToUserId(Long toUserId) {
      this.toUserId = toUserId;
    }
  }
}
