package com.tondo.infrastructure.mq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tondo.mq")
public class RabbitMqProperties {

    /** 是否通过 RabbitMQ 异步推送通知（测试环境可关闭） */
    private boolean notificationEnabled = true;

    private String notificationExchange = "tondo.notification.exchange";

    private String notificationQueue = "tondo.notification.push";

    private String notificationRoutingKey = "notification.push";

    private String notificationDlq = "tondo.notification.dlq";
}
