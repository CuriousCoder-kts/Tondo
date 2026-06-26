package com.tondo.module.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tondo.common.exception.BusinessException;
import com.tondo.common.response.PageResult;
import com.tondo.infrastructure.mq.RabbitMqProperties;
import com.tondo.module.companion.entity.CompanionRelation;
import com.tondo.module.companion.mapper.CompanionRelationMapper;
import com.tondo.module.message.entity.PrivateMessage;
import com.tondo.module.message.mapper.PrivateMessageMapper;
import com.tondo.module.notification.entity.NotificationRecord;
import com.tondo.module.notification.entity.vo.NotificationItemVO;
import com.tondo.module.notification.entity.vo.NotificationSummaryVO;
import com.tondo.module.notification.entity.vo.NotificationVO;
import com.tondo.module.notification.mapper.NotificationRecordMapper;
import com.tondo.module.notification.mq.NotificationMqProducer;
import com.tondo.module.notification.service.NotificationService;
import com.tondo.module.notification.service.NotificationWebSocketPusher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationWebSocketPusher webSocketPusher;
    private final ObjectProvider<NotificationMqProducer> mqProducerProvider;
    private final RabbitMqProperties mqProperties;
    private final NotificationRecordMapper notificationRecordMapper;
    private final CompanionRelationMapper relationMapper;
    private final PrivateMessageMapper messageMapper;

    @Override
    @Transactional
    public void send(Long userId, NotificationVO notification) {
        if (notification.getCreatedAt() == null) {
            notification.setCreatedAt(LocalDateTime.now());
        }

        NotificationRecord record = toRecord(userId, notification);
        notificationRecordMapper.insert(record);
        notification.setId(record.getId());

        if (mqProperties.isNotificationEnabled()) {
            NotificationMqProducer producer = mqProducerProvider.getIfAvailable();
            if (producer != null) {
                producer.publish(userId, notification);
                return;
            }
        }

        webSocketPusher.push(userId, notification);
    }

    @Override
    public NotificationSummaryVO getSummary(Long userId) {
        NotificationSummaryVO summary = new NotificationSummaryVO();

        LambdaQueryWrapper<CompanionRelation> inviteWrapper = new LambdaQueryWrapper<>();
        inviteWrapper.eq(CompanionRelation::getInviteeId, userId)
                .eq(CompanionRelation::getStatus, "PENDING");
        summary.setPendingInvitations(relationMapper.selectCount(inviteWrapper).intValue());

        LambdaQueryWrapper<PrivateMessage> msgWrapper = new LambdaQueryWrapper<>();
        msgWrapper.eq(PrivateMessage::getReceiverId, userId)
                .eq(PrivateMessage::getIsRead, 0)
                .eq(PrivateMessage::getIsRecalled, 0);
        summary.setUnreadMessages(messageMapper.selectCount(msgWrapper).intValue());

        LambdaQueryWrapper<NotificationRecord> inboxWrapper = new LambdaQueryWrapper<>();
        inboxWrapper.eq(NotificationRecord::getUserId, userId)
                .eq(NotificationRecord::getIsRead, 0);
        summary.setUnreadInbox(notificationRecordMapper.selectCount(inboxWrapper).intValue());

        return summary;
    }

    @Override
    public PageResult<NotificationItemVO> listInbox(Long userId, int page, int size) {
        Page<NotificationRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<NotificationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotificationRecord::getUserId, userId)
                .orderByDesc(NotificationRecord::getCreatedAt);
        Page<NotificationRecord> result = notificationRecordMapper.selectPage(pageParam, wrapper);
        List<NotificationItemVO> items = result.getRecords().stream()
                .map(NotificationItemVO::from)
                .toList();
        return PageResult.of(result.getCurrent(), result.getSize(), result.getTotal(), items);
    }

    @Override
    public void markRead(Long userId, Long notificationId) {
        NotificationRecord record = notificationRecordMapper.selectById(notificationId);
        if (record == null || !userId.equals(record.getUserId())) {
            throw new BusinessException("通知不存在");
        }
        if (record.getIsRead() != null && record.getIsRead() == 1) {
            return;
        }
        record.setIsRead(1);
        notificationRecordMapper.updateById(record);
    }

    @Override
    public void markAllRead(Long userId) {
        LambdaUpdateWrapper<NotificationRecord> update = new LambdaUpdateWrapper<>();
        update.eq(NotificationRecord::getUserId, userId)
                .eq(NotificationRecord::getIsRead, 0)
                .set(NotificationRecord::getIsRead, 1);
        notificationRecordMapper.update(null, update);
    }

    private NotificationRecord toRecord(Long userId, NotificationVO notification) {
        NotificationRecord record = new NotificationRecord();
        record.setUserId(userId);
        record.setType(notification.getType());
        record.setTitle(notification.getTitle());
        record.setContent(notification.getContent());
        record.setRelationId(notification.getRelationId());
        record.setPlanId(notification.getPlanId());
        record.setSenderId(notification.getSenderId());
        record.setIsRead(0);
        record.setCreatedAt(notification.getCreatedAt());
        return record;
    }
}
