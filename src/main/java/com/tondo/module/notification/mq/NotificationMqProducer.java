package com.tondo.module.notification.mq;

import com.tondo.infrastructure.mq.RabbitMqProperties;
import com.tondo.module.notification.entity.vo.NotificationVO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "tondo.mq.notification-enabled", havingValue = "true", matchIfMissing = true)
public class NotificationMqProducer {

    private static final Logger log = LoggerFactory.getLogger(NotificationMqProducer.class);

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqProperties mqProperties;

    public void publish(Long userId, NotificationVO notification) {
        NotificationPushMessage message = new NotificationPushMessage(userId, notification);
        rabbitTemplate.convertAndSend(
                mqProperties.getNotificationExchange(),
                mqProperties.getNotificationRoutingKey(),
                message);
        log.info("Published notification to MQ type={} userId={}", notification.getType(), userId);
    }
}
