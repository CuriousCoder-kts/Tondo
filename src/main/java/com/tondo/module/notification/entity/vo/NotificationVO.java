package com.tondo.module.notification.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationVO {
    private String type;
    private String title;
    private String content;
    private Long relationId;
    private Long planId;
    private Long senderId;
    private LocalDateTime createdAt;
    /** 持久化后的通知 ID，供前端标记已读 */
    private Long id;
}
