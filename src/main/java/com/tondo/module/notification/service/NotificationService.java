package com.tondo.module.notification.service;

import com.tondo.common.response.PageResult;
import com.tondo.module.notification.entity.vo.NotificationItemVO;
import com.tondo.module.notification.entity.vo.NotificationSummaryVO;
import com.tondo.module.notification.entity.vo.NotificationVO;

public interface NotificationService {

    void send(Long userId, NotificationVO notification);

    NotificationSummaryVO getSummary(Long userId);

    PageResult<NotificationItemVO> listInbox(Long userId, int page, int size);

    void markRead(Long userId, Long notificationId);

    void markAllRead(Long userId);
}
