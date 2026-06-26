package com.tondo.module.notification.mq;

import com.tondo.module.notification.service.NotificationWebSocketPusher;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "tondo.mq.notification-enabled", havingValue = "true", matchIfMissing = true)
public class NotificationMqConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationMqConsumer.class);

    private final NotificationWebSocketPusher webSocketPusher;

    @RabbitListener(queues = "${tondo.mq.notification-queue}")
    public void onNotificationPush(NotificationPushMessage message) {
        if (message == null || message.getUserId() == null || message.getNotification() == null) {
            log.warn("Skip invalid notification MQ message: {}", message);
            return;
        }
        webSocketPusher.push(message.getUserId(), message.getNotification());
        log.info("Delivered notification via WS type={} userId={}",
                message.getNotification().getType(), message.getUserId());
    }
}
