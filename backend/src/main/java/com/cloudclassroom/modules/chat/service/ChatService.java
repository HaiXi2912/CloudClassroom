package com.cloudclassroom.modules.chat.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudclassroom.common.BusinessException;
import com.cloudclassroom.modules.chat.dto.ConversationRow;
import com.cloudclassroom.modules.chat.dto.MessageRow;
import com.cloudclassroom.modules.chat.entity.ChatConversation;
import com.cloudclassroom.modules.chat.entity.ChatConversationMember;
import com.cloudclassroom.modules.chat.entity.ChatMessage;
import com.cloudclassroom.modules.chat.mapper.ChatConversationMapper;
import com.cloudclassroom.modules.chat.mapper.ChatConversationMemberMapper;
import com.cloudclassroom.modules.chat.mapper.ChatMessageMapper;
import com.cloudclassroom.modules.user.entity.SysUser;
import com.cloudclassroom.modules.user.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 私聊服务（最小实现）：
 * - 创建/获取私聊会话
 * - 列出我的会话
 * - 拉取会话消息（按ID增量）
 * - 发送消息
 */
@Service
public class ChatService {

  private static final int CONVERSATION_TYPE_PRIVATE = 1;

  private final ChatConversationMapper chatConversationMapper;
  private final ChatConversationMemberMapper chatConversationMemberMapper;
  private final ChatMessageMapper chatMessageMapper;
  private final SysUserMapper sysUserMapper;

  public ChatService(ChatConversationMapper chatConversationMapper,
                     ChatConversationMemberMapper chatConversationMemberMapper,
                     ChatMessageMapper chatMessageMapper,
                     SysUserMapper sysUserMapper) {
    this.chatConversationMapper = chatConversationMapper;
    this.chatConversationMemberMapper = chatConversationMemberMapper;
    this.chatMessageMapper = chatMessageMapper;
    this.sysUserMapper = sysUserMapper;
  }

  /**
   * 获取或创建私聊会话（当前用户与对端用户）。
   */
  public Long getOrCreatePrivateConversation(Long userId, Long peerUserId) {
    if (userId == null) {
      throw new BusinessException(40100, "未登录");
    }
    if (peerUserId == null) {
      throw new BusinessException(40040, "peerUserId 不能为空");
    }
    if (Objects.equals(userId, peerUserId)) {
      throw new BusinessException(40041, "不能与自己私聊");
    }

    SysUser peerUser = requireActiveUser(peerUserId);
    if (peerUser == null) {
      throw new BusinessException(40440, "对端用户不存在");
    }

    List<ChatConversationMember> my = chatConversationMemberMapper.selectList(new LambdaQueryWrapper<ChatConversationMember>()
        .eq(ChatConversationMember::getUserId, userId)
        .eq(ChatConversationMember::getDeleted, 0));
    if (my.isEmpty()) {
      return createPrivateConversation(userId, peerUserId);
    }

    Set<Long> myConversationIds = my.stream()
        .map(ChatConversationMember::getConversationId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    List<ChatConversationMember> peerMembers = chatConversationMemberMapper.selectList(new LambdaQueryWrapper<ChatConversationMember>()
        .eq(ChatConversationMember::getUserId, peerUserId)
        .eq(ChatConversationMember::getDeleted, 0));

    Set<Long> peerConversationIds = peerMembers.stream()
        .map(ChatConversationMember::getConversationId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    myConversationIds.retainAll(peerConversationIds);
    if (myConversationIds.isEmpty()) {
      return createPrivateConversation(userId, peerUserId);
    }

    // 只取一个（最小实现）；按 id 最小值确定。
    List<Long> candidateIds = new ArrayList<>(myConversationIds);
    Collections.sort(candidateIds);

    for (Long cid : candidateIds) {
      ChatConversation c = chatConversationMapper.selectOne(new LambdaQueryWrapper<ChatConversation>()
          .eq(ChatConversation::getId, cid)
          .eq(ChatConversation::getDeleted, 0)
          .eq(ChatConversation::getConversationType, CONVERSATION_TYPE_PRIVATE)
          .last("LIMIT 1"));
      if (c != null) {
        return c.getId();
      }
    }

    return createPrivateConversation(userId, peerUserId);
  }

  public List<ConversationRow> listMyConversations(Long userId) {
    if (userId == null) {
      throw new BusinessException(40100, "未登录");
    }

    List<ChatConversationMember> my = chatConversationMemberMapper.selectList(new LambdaQueryWrapper<ChatConversationMember>()
        .eq(ChatConversationMember::getUserId, userId)
        .eq(ChatConversationMember::getDeleted, 0));

    if (my.isEmpty()) {
      return List.of();
    }

    List<ConversationRow> rows = new ArrayList<>();
    for (ChatConversationMember m : my) {
      if (m == null || m.getConversationId() == null) {
        continue;
      }
      ChatConversation c = chatConversationMapper.selectOne(new LambdaQueryWrapper<ChatConversation>()
          .eq(ChatConversation::getId, m.getConversationId())
          .eq(ChatConversation::getDeleted, 0)
          .eq(ChatConversation::getConversationType, CONVERSATION_TYPE_PRIVATE)
          .last("LIMIT 1"));
      if (c == null) {
        continue;
      }

      ChatConversationMember peer = chatConversationMemberMapper.selectOne(new LambdaQueryWrapper<ChatConversationMember>()
          .eq(ChatConversationMember::getConversationId, c.getId())
          .ne(ChatConversationMember::getUserId, userId)
          .eq(ChatConversationMember::getDeleted, 0)
          .last("LIMIT 1"));
      if (peer == null || peer.getUserId() == null) {
        continue;
      }

      SysUser peerUser = requireActiveUser(peer.getUserId());
      if (peerUser == null) {
        continue;
      }

      ConversationRow row = new ConversationRow();
      row.setConversationId(c.getId());
      row.setPeerUserId(peerUser.getId());
      row.setPeerNickname(peerUser.getNickname());

      ChatMessage last = chatMessageMapper.selectOne(new LambdaQueryWrapper<ChatMessage>()
          .eq(ChatMessage::getConversationId, c.getId())
          .eq(ChatMessage::getDeleted, 0)
          .orderByDesc(ChatMessage::getId)
          .last("LIMIT 1"));
      if (last != null) {
        row.setLastMessageId(last.getId());
        row.setLastMessageContent(last.getContent());
        row.setLastMessageAt(last.getSentAt());
      }

      rows.add(row);
    }

    // 简单按最近消息排序（无消息则排后）
    rows.sort((a, b) -> {
      Long aId = a.getLastMessageId();
      Long bId = b.getLastMessageId();
      if (aId == null && bId == null) {
        return Long.compare(a.getConversationId(), b.getConversationId());
      }
      if (aId == null) {
        return 1;
      }
      if (bId == null) {
        return -1;
      }
      return Long.compare(bId, aId);
    });

    return rows;
  }

  public List<MessageRow> listMessages(Long userId, Long conversationId, Long afterId, Integer limit) {
    if (userId == null) {
      throw new BusinessException(40100, "未登录");
    }
    if (conversationId == null) {
      throw new BusinessException(40042, "conversationId 不能为空");
    }
    requireMember(userId, conversationId);

    int finalLimit = 50;
    if (limit != null) {
      finalLimit = Math.max(1, Math.min(200, limit));
    }

    LambdaQueryWrapper<ChatMessage> qw = new LambdaQueryWrapper<ChatMessage>()
        .eq(ChatMessage::getConversationId, conversationId)
        .eq(ChatMessage::getDeleted, 0);
    if (afterId != null) {
      qw.gt(ChatMessage::getId, afterId);
    }
    qw.orderByAsc(ChatMessage::getId)
        .last("LIMIT " + finalLimit);

    List<ChatMessage> list = chatMessageMapper.selectList(qw);
    if (list == null || list.isEmpty()) {
      return List.of();
    }
    List<MessageRow> rows = new ArrayList<>();
    for (ChatMessage m : list) {
      if (m == null) {
        continue;
      }
      MessageRow r = new MessageRow();
      r.setId(m.getId());
      r.setConversationId(m.getConversationId());
      r.setSenderId(m.getSenderId());
      r.setContent(m.getContent());
      r.setSentAt(m.getSentAt());
      rows.add(r);
    }
    return rows;
  }

  public MessageRow sendMessage(Long userId, Long conversationId, String content) {
    if (userId == null) {
      throw new BusinessException(40100, "未登录");
    }
    if (conversationId == null) {
      throw new BusinessException(40042, "conversationId 不能为空");
    }
    requireMember(userId, conversationId);

    String c = content == null ? "" : content.trim();
    if (c.isEmpty()) {
      throw new BusinessException(40043, "消息内容不能为空");
    }
    if (c.length() > 2000) {
      throw new BusinessException(40044, "消息内容过长");
    }

    ChatMessage msg = new ChatMessage();
    msg.setConversationId(conversationId);
    msg.setSenderId(userId);
    msg.setContent(c);
    msg.setSentAt(LocalDateTime.now());
    msg.setDeleted(0);
    chatMessageMapper.insert(msg);

    MessageRow row = new MessageRow();
    row.setId(msg.getId());
    row.setConversationId(conversationId);
    row.setSenderId(userId);
    row.setContent(c);
    row.setSentAt(msg.getSentAt());
    return row;
  }

  private Long createPrivateConversation(Long userId, Long peerUserId) {
    ChatConversation c = new ChatConversation();
    c.setConversationType(CONVERSATION_TYPE_PRIVATE);
    c.setCreatedAt(LocalDateTime.now());
    c.setUpdatedAt(LocalDateTime.now());
    c.setDeleted(0);
    chatConversationMapper.insert(c);

    ChatConversationMember m1 = new ChatConversationMember();
    m1.setConversationId(c.getId());
    m1.setUserId(userId);
    m1.setJoinedAt(LocalDateTime.now());
    m1.setDeleted(0);
    chatConversationMemberMapper.insert(m1);

    ChatConversationMember m2 = new ChatConversationMember();
    m2.setConversationId(c.getId());
    m2.setUserId(peerUserId);
    m2.setJoinedAt(LocalDateTime.now());
    m2.setDeleted(0);
    chatConversationMemberMapper.insert(m2);

    return c.getId();
  }

  private void requireMember(Long userId, Long conversationId) {
    Long cnt = chatConversationMemberMapper.selectCount(new LambdaQueryWrapper<ChatConversationMember>()
        .eq(ChatConversationMember::getConversationId, conversationId)
        .eq(ChatConversationMember::getUserId, userId)
        .eq(ChatConversationMember::getDeleted, 0));
    if (cnt == null || cnt <= 0) {
      throw new BusinessException(40303, "不在该会话中");
    }
  }

  private SysUser requireActiveUser(Long userId) {
    return sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
        .eq(SysUser::getId, userId)
        .eq(SysUser::getDeleted, 0)
        .last("LIMIT 1"));
  }
}
