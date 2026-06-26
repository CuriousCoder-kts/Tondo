package com.tondo.module.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tondo.common.exception.BusinessException;
import com.tondo.common.response.PageResult;
import com.tondo.module.companion.entity.CompanionRelation;
import com.tondo.module.companion.mapper.CompanionRelationMapper;
import com.tondo.module.message.entity.PrivateMessage;
import com.tondo.module.message.mapper.PrivateMessageMapper;
import com.tondo.module.message.service.PrivateMessageService;
import com.tondo.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivateMessageServiceImpl implements PrivateMessageService {

    private final PrivateMessageMapper messageMapper;
    private final CompanionRelationMapper relationMapper;
    private final UserService userService;

    @Override
    @Transactional
    public PrivateMessage sendMessage(Long senderId, Long relationId, String content) {
        userService.assertUserActive(senderId);
        CompanionRelation relation = assertAcceptedRelation(relationId);
        assertParticipant(senderId, relation);

        if (content == null || content.isBlank()) {
            throw new BusinessException("消息不能为空");
        }
        content = content.trim();
        if (content.length() > 2000) {
            throw new BusinessException("消息过长");
        }

        Long receiverId = senderId.equals(relation.getInviterId()) ?
                relation.getInviteeId() : relation.getInviterId();

        PrivateMessage msg = new PrivateMessage();
        msg.setRelationId(relationId);
        msg.setSenderId(senderId);
        msg.setReceiverId(receiverId);
        msg.setContent(content);
        msg.setContentType("TEXT");
        msg.setIsRead(0);
        msg.setIsRecalled(0);
        messageMapper.insert(msg);
        return msg;
    }

    @Override
    public List<PrivateMessage> getMessagesByRelationId(Long userId, Long relationId, int page, int size) {
        return getMessagesPage(userId, relationId, page, size).getRecords();
    }

    @Override
    public PageResult<PrivateMessage> getMessagesPage(Long userId, Long relationId, int page, int size) {
        userService.assertUserActive(userId);
        CompanionRelation relation = relationMapper.selectById(relationId);
        if (relation == null) {
            throw new BusinessException("陪伴关系不存在");
        }
        assertParticipant(userId, relation);
        if (!"ACCEPTED".equals(relation.getStatus()) && !"COMPLETED".equals(relation.getStatus())) {
            throw new BusinessException("仅进行中的陪伴关系可查看消息");
        }

        Page<PrivateMessage> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<PrivateMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PrivateMessage::getRelationId, relationId)
                .eq(PrivateMessage::getIsRecalled, 0)
                .orderByDesc(PrivateMessage::getCreatedAt);
        Page<PrivateMessage> result = messageMapper.selectPage(pageParam, wrapper);
        markMessagesAsRead(userId, relationId);
        return PageResult.of(result.getCurrent(), result.getSize(), result.getTotal(), result.getRecords());
    }

    @Override
    public void markMessagesAsRead(Long userId, Long relationId) {
        assertRelationParticipant(userId, relationId);
        LambdaUpdateWrapper<PrivateMessage> update = new LambdaUpdateWrapper<>();
        update.eq(PrivateMessage::getRelationId, relationId)
                .eq(PrivateMessage::getReceiverId, userId)
                .eq(PrivateMessage::getIsRead, 0)
                .set(PrivateMessage::getIsRead, 1);
        messageMapper.update(null, update);
    }

    @Override
    public void assertRelationParticipant(Long userId, Long relationId) {
        CompanionRelation relation = relationMapper.selectById(relationId);
        if (relation == null) {
            throw new BusinessException("陪伴关系不存在");
        }
        assertParticipant(userId, relation);
    }

    private CompanionRelation assertAcceptedRelation(Long relationId) {
        CompanionRelation relation = relationMapper.selectById(relationId);
        if (relation == null || !"ACCEPTED".equals(relation.getStatus())) {
            throw new BusinessException("陪伴关系不存在或已结束");
        }
        return relation;
    }

    private void assertParticipant(Long userId, CompanionRelation relation) {
        if (!userId.equals(relation.getInviterId()) && !userId.equals(relation.getInviteeId())) {
            throw new BusinessException(403, "无权访问该会话");
        }
    }
}
