package com.tondo.module.notification.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("notification_record")
public class NotificationRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String type;

    private String title;

    private String content;

    private Long relationId;

    private Long planId;

    private Long senderId;

    private Integer isRead;

    private LocalDateTime createdAt;
}
