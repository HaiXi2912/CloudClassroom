package com.cloudclassroom.modules.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudclassroom.modules.chat.entity.ChatConversationMember;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会话成员 Mapper。
 */
@Mapper
public interface ChatConversationMemberMapper extends BaseMapper<ChatConversationMember> {
}
