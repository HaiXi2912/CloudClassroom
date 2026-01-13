package com.cloudclassroom.modules.chat.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudclassroom.common.BusinessException;
import com.cloudclassroom.modules.chat.dto.ChatUserRow;
import com.cloudclassroom.modules.chat.dto.FriendRequestRow;
import com.cloudclassroom.modules.chat.entity.ChatFriend;
import com.cloudclassroom.modules.chat.entity.ChatFriendRequest;
import com.cloudclassroom.modules.chat.mapper.ChatFriendMapper;
import com.cloudclassroom.modules.chat.mapper.ChatFriendRequestMapper;
import com.cloudclassroom.modules.user.dto.AdminUserRow;
import com.cloudclassroom.modules.user.entity.SysUser;
import com.cloudclassroom.modules.user.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatSocialService {

  private static final int REQUEST_STATUS_PENDING = 1;
  private static final int REQUEST_STATUS_ACCEPTED = 2;
  private static final int REQUEST_STATUS_REJECTED = 3;

  private final SysUserMapper sysUserMapper;
  private final ChatFriendMapper chatFriendMapper;
  private final ChatFriendRequestMapper chatFriendRequestMapper;

  public ChatSocialService(SysUserMapper sysUserMapper,
                           ChatFriendMapper chatFriendMapper,
                           ChatFriendRequestMapper chatFriendRequestMapper) {
    this.sysUserMapper = sysUserMapper;
    this.chatFriendMapper = chatFriendMapper;
    this.chatFriendRequestMapper = chatFriendRequestMapper;
  }

  /**
   * 通讯录：列出所有可见用户（学生+老师+管理员），并标注与我的关系。
   */
  public List<ChatUserRow> listAllUsersWithRelation(Long myUserId, String keyword) {
    if (myUserId == null) {
      throw new BusinessException(40100, "未登录");
    }

    String kw = keyword == null ? null : keyword.trim();

    Set<Long> friendIds = chatFriendMapper.selectList(new LambdaQueryWrapper<ChatFriend>()
            .eq(ChatFriend::getUserId, myUserId)
            .eq(ChatFriend::getDeleted, 0))
        .stream()
        .map(ChatFriend::getFriendUserId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    Set<Long> pendingOutIds = chatFriendRequestMapper.selectList(new LambdaQueryWrapper<ChatFriendRequest>()
            .eq(ChatFriendRequest::getFromUserId, myUserId)
            .eq(ChatFriendRequest::getStatus, REQUEST_STATUS_PENDING)
            .eq(ChatFriendRequest::getDeleted, 0))
        .stream()
        .map(ChatFriendRequest::getToUserId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    Set<Long> pendingInIds = chatFriendRequestMapper.selectList(new LambdaQueryWrapper<ChatFriendRequest>()
            .eq(ChatFriendRequest::getToUserId, myUserId)
            .eq(ChatFriendRequest::getStatus, REQUEST_STATUS_PENDING)
            .eq(ChatFriendRequest::getDeleted, 0))
        .stream()
        .map(ChatFriendRequest::getFromUserId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    // 最小实现：复用 sys_user + sys_role 关联查询（在 mapper 中实现）
    List<Map<String, Object>> list = sysUserMapper.selectMaps(new LambdaQueryWrapper<SysUser>()
        .select(SysUser::getId, SysUser::getUsername, SysUser::getNickname, SysUser::getAvatarUrl)
        .eq(SysUser::getDeleted, 0)
        .eq(SysUser::getStatus, 1)
        .ne(SysUser::getId, myUserId)
        .and(kw != null && !kw.isEmpty(), w -> w.like(SysUser::getUsername, kw).or().like(SysUser::getNickname, kw))
        .orderByAsc(SysUser::getId));

    // 由于 BaseMapper.selectMaps 无法 join role，这里改为第二步补角色：按用户逐个查（数据量一般不大）。
    // 若后续需要优化，可在 SysUserMapper 增加 join SQL 专用方法。
    List<ChatUserRow> rows = new ArrayList<>();
    for (Map<String, Object> m : list) {
      Long uid = m.get("id") == null ? null : ((Number) m.get("id")).longValue();
      if (uid == null) {
        continue;
      }
      ChatUserRow r = new ChatUserRow();
      r.setUserId(uid);
      r.setUsername((String) m.get("username"));
      r.setNickname((String) m.get("nickname"));
      r.setAvatarUrl((String) m.get("avatar_url"));

      // 角色信息：借用管理端查询（已 join role）
      AdminUserRow adminRow = sysUserMapper.selectAdminUserRowById(uid);
      if (adminRow != null) {
        r.setRoleCode(adminRow.getRoleCode());
        r.setRoleName(adminRow.getRoleName());
      }

      r.setFriend(friendIds.contains(uid));
      r.setPendingOut(pendingOutIds.contains(uid));
      r.setPendingIn(pendingInIds.contains(uid));
      rows.add(r);
    }

    return rows;
  }

  public void sendFriendRequest(Long myUserId, Long toUserId) {
    if (myUserId == null) {
      throw new BusinessException(40100, "未登录");
    }
    if (toUserId == null) {
      throw new BusinessException(40040, "toUserId 不能为空");
    }
    if (Objects.equals(myUserId, toUserId)) {
      throw new BusinessException(40041, "不能添加自己");
    }

    SysUser to = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
        .eq(SysUser::getId, toUserId)
        .eq(SysUser::getDeleted, 0)
        .eq(SysUser::getStatus, 1)
        .last("LIMIT 1"));
    if (to == null) {
      throw new BusinessException(40400, "用户不存在");
    }

    Long friendCnt = chatFriendMapper.selectCount(new LambdaQueryWrapper<ChatFriend>()
        .eq(ChatFriend::getUserId, myUserId)
        .eq(ChatFriend::getFriendUserId, toUserId)
        .eq(ChatFriend::getDeleted, 0));
    if (friendCnt != null && friendCnt > 0) {
      throw new BusinessException(40042, "已经是好友了");
    }

    ChatFriendRequest existed = chatFriendRequestMapper.selectOne(new LambdaQueryWrapper<ChatFriendRequest>()
        .eq(ChatFriendRequest::getFromUserId, myUserId)
        .eq(ChatFriendRequest::getToUserId, toUserId)
        .eq(ChatFriendRequest::getStatus, REQUEST_STATUS_PENDING)
        .eq(ChatFriendRequest::getDeleted, 0)
        .last("LIMIT 1"));
    if (existed != null) {
      return; // 已申请，幂等
    }

    // 如果对方已经向我发起申请，则直接通过（更像微信的“互加自动成好友”体验）
    ChatFriendRequest reverse = chatFriendRequestMapper.selectOne(new LambdaQueryWrapper<ChatFriendRequest>()
        .eq(ChatFriendRequest::getFromUserId, toUserId)
        .eq(ChatFriendRequest::getToUserId, myUserId)
        .eq(ChatFriendRequest::getStatus, REQUEST_STATUS_PENDING)
        .eq(ChatFriendRequest::getDeleted, 0)
        .last("LIMIT 1"));
    if (reverse != null) {
      acceptRequestInternal(myUserId, reverse);
      return;
    }

    ChatFriendRequest req = new ChatFriendRequest();
    req.setFromUserId(myUserId);
    req.setToUserId(toUserId);
    req.setStatus(REQUEST_STATUS_PENDING);
    req.setCreatedAt(LocalDateTime.now());
    req.setDeleted(0);
    chatFriendRequestMapper.insert(req);
  }

  public List<FriendRequestRow> listIncomingRequests(Long myUserId) {
    if (myUserId == null) {
      throw new BusinessException(40100, "未登录");
    }

    List<ChatFriendRequest> list = chatFriendRequestMapper.selectList(new LambdaQueryWrapper<ChatFriendRequest>()
        .eq(ChatFriendRequest::getToUserId, myUserId)
        .eq(ChatFriendRequest::getStatus, REQUEST_STATUS_PENDING)
        .eq(ChatFriendRequest::getDeleted, 0)
        .orderByDesc(ChatFriendRequest::getId));
    if (list == null || list.isEmpty()) {
      return List.of();
    }

    List<FriendRequestRow> rows = new ArrayList<>();
    for (ChatFriendRequest r : list) {
      if (r == null || r.getFromUserId() == null) {
        continue;
      }
      SysUser from = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
          .eq(SysUser::getId, r.getFromUserId())
          .eq(SysUser::getDeleted, 0)
          .last("LIMIT 1"));
      if (from == null) {
        continue;
      }

      FriendRequestRow row = new FriendRequestRow();
      row.setId(r.getId());
      row.setFromUserId(from.getId());
      row.setFromNickname(from.getNickname());
      row.setFromAvatarUrl(from.getAvatarUrl());
      row.setCreatedAt(r.getCreatedAt());

      AdminUserRow adminRow = sysUserMapper.selectAdminUserRowById(from.getId());
      if (adminRow != null) {
        row.setFromRoleCode(adminRow.getRoleCode());
        row.setFromRoleName(adminRow.getRoleName());
      }

      rows.add(row);
    }

    return rows;
  }

  public void acceptRequest(Long myUserId, Long requestId) {
    if (myUserId == null) {
      throw new BusinessException(40100, "未登录");
    }
    if (requestId == null) {
      throw new BusinessException(40040, "requestId 不能为空");
    }

    ChatFriendRequest req = chatFriendRequestMapper.selectOne(new LambdaQueryWrapper<ChatFriendRequest>()
        .eq(ChatFriendRequest::getId, requestId)
        .eq(ChatFriendRequest::getDeleted, 0)
        .last("LIMIT 1"));
    if (req == null) {
      throw new BusinessException(40400, "申请不存在");
    }
    if (!Objects.equals(req.getToUserId(), myUserId)) {
      throw new BusinessException(40303, "无权处理该申请");
    }
    if (req.getStatus() == null || req.getStatus() != REQUEST_STATUS_PENDING) {
      return;
    }

    acceptRequestInternal(myUserId, req);
  }

  public void rejectRequest(Long myUserId, Long requestId) {
    if (myUserId == null) {
      throw new BusinessException(40100, "未登录");
    }
    if (requestId == null) {
      throw new BusinessException(40040, "requestId 不能为空");
    }

    ChatFriendRequest req = chatFriendRequestMapper.selectOne(new LambdaQueryWrapper<ChatFriendRequest>()
        .eq(ChatFriendRequest::getId, requestId)
        .eq(ChatFriendRequest::getDeleted, 0)
        .last("LIMIT 1"));
    if (req == null) {
      throw new BusinessException(40400, "申请不存在");
    }
    if (!Objects.equals(req.getToUserId(), myUserId)) {
      throw new BusinessException(40303, "无权处理该申请");
    }
    if (req.getStatus() == null || req.getStatus() != REQUEST_STATUS_PENDING) {
      return;
    }

    req.setStatus(REQUEST_STATUS_REJECTED);
    req.setHandledAt(LocalDateTime.now());
    chatFriendRequestMapper.updateById(req);
  }

  public void deleteFriend(Long myUserId, Long friendUserId) {
    if (myUserId == null) {
      throw new BusinessException(40100, "未登录");
    }
    if (friendUserId == null) {
      throw new BusinessException(40040, "friendUserId 不能为空");
    }

    List<ChatFriend> list = chatFriendMapper.selectList(new LambdaQueryWrapper<ChatFriend>()
        .eq(ChatFriend::getDeleted, 0)
        .and(w -> w.or(q -> q.eq(ChatFriend::getUserId, myUserId).eq(ChatFriend::getFriendUserId, friendUserId))
            .or(q -> q.eq(ChatFriend::getUserId, friendUserId).eq(ChatFriend::getFriendUserId, myUserId))));
    if (list == null || list.isEmpty()) {
      return;
    }

    for (ChatFriend f : list) {
      f.setDeleted(1);
      chatFriendMapper.updateById(f);
    }
  }

  private void acceptRequestInternal(Long myUserId, ChatFriendRequest req) {
    if (req == null) {
      return;
    }
    Long from = req.getFromUserId();
    Long to = req.getToUserId();
    if (from == null || to == null) {
      return;
    }

    req.setStatus(REQUEST_STATUS_ACCEPTED);
    req.setHandledAt(LocalDateTime.now());
    chatFriendRequestMapper.updateById(req);

    upsertFriend(from, to);
    upsertFriend(to, from);
  }

  private void upsertFriend(Long userId, Long friendUserId) {
    ChatFriend existed = chatFriendMapper.selectOne(new LambdaQueryWrapper<ChatFriend>()
        .eq(ChatFriend::getUserId, userId)
        .eq(ChatFriend::getFriendUserId, friendUserId)
        .last("LIMIT 1"));

    if (existed != null) {
      if (existed.getDeleted() != null && existed.getDeleted() != 0) {
        existed.setDeleted(0);
        chatFriendMapper.updateById(existed);
      }
      return;
    }

    ChatFriend f = new ChatFriend();
    f.setUserId(userId);
    f.setFriendUserId(friendUserId);
    f.setCreatedAt(LocalDateTime.now());
    f.setDeleted(0);
    chatFriendMapper.insert(f);
  }
}
