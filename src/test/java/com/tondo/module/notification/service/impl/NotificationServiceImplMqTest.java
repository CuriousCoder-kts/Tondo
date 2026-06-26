package com.tondo.module.notification.service.impl;

import com.tondo.infrastructure.mq.RabbitMqProperties;
import com.tondo.module.companion.mapper.CompanionRelationMapper;
import com.tondo.module.message.mapper.PrivateMessageMapper;
import com.tondo.module.notification.entity.vo.NotificationVO;
import com.tondo.module.notification.mapper.NotificationRecordMapper;
import com.tondo.module.notification.mq.NotificationMqProducer;
import com.tondo.module.notification.service.NotificationWebSocketPusher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * 纯单元测试，不依赖 Spring 容器与 IDEA JUnit5 代理（避免部分 IDE 版本 ClassCircularityError）。
 */
class NotificationServiceImplMqTest {

    private NotificationWebSocketPusher webSocketPusher;
    private ObjectProvider<NotificationMqProducer> mqProducerProvider;
    private RabbitMqProperties mqProperties;
    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        webSocketPusher = mock(NotificationWebSocketPusher.class);
        mqProducerProvider = mock(ObjectProvider.class);
        mqProperties = new RabbitMqProperties();
        notificationService = new NotificationServiceImpl(
                webSocketPusher,
                mqProducerProvider,
                mqProperties,
                mock(NotificationRecordMapper.class),
                mock(CompanionRelationMapper.class),
                mock(PrivateMessageMapper.class));
    }

    @Test
    void send_usesMqWhenEnabled() {
        NotificationMqProducer producer = mock(NotificationMqProducer.class);
        mqProperties.setNotificationEnabled(true);
        when(mqProducerProvider.getIfAvailable()).thenReturn(producer);

        NotificationVO vo = new NotificationVO();
        vo.setType("INVITE_RECEIVED");
        vo.setTitle("邀请");

        notificationService.send(2L, vo);

        verify(producer).publish(eq(2L), eq(vo));
        verifyNoInteractions(webSocketPusher);
    }

    @Test
    void send_fallsBackToWebSocketWhenMqDisabled() {
        mqProperties.setNotificationEnabled(false);

        NotificationVO vo = new NotificationVO();
        vo.setType("NEW_MESSAGE");

        notificationService.send(3L, vo);

        verify(webSocketPusher).push(eq(3L), eq(vo));
        verifyNoInteractions(mqProducerProvider);
    }
}
