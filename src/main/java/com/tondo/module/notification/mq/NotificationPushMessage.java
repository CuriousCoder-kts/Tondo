package com.tondo.module.notification.mq;

import com.tondo.module.notification.entity.vo.NotificationVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RabbitMQ 通知推送消息体：解耦业务线程与 WebSocket 推送。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPushMessage {

    private Long userId;

    private NotificationVO notification;
}
