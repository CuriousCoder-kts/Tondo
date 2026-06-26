package com.tondo.module.notification.service;

import com.tondo.module.notification.entity.vo.NotificationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * 将通知推送到用户 STOMP 订阅队列（由 MQ 消费者或同步降级路径调用）。
 */
@Component
@RequiredArgsConstructor
public class NotificationWebSocketPusher {

    private final SimpMessagingTemplate messagingTemplate;

    public void push(Long userId, NotificationVO notification) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                notification);
    }
}
