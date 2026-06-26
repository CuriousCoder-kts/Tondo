package com.tondo.module.notification.entity.vo;

import com.tondo.module.notification.entity.NotificationRecord;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationItemVO {
    private Long id;
    private String type;
    private String title;
    private String content;
    private Long relationId;
    private Long planId;
    private Long senderId;
    private Boolean read;
    private LocalDateTime createdAt;

    public static NotificationItemVO from(NotificationRecord record) {
        NotificationItemVO vo = new NotificationItemVO();
        vo.setId(record.getId());
        vo.setType(record.getType());
        vo.setTitle(record.getTitle());
        vo.setContent(record.getContent());
        vo.setRelationId(record.getRelationId());
        vo.setPlanId(record.getPlanId());
        vo.setSenderId(record.getSenderId());
        vo.setRead(record.getIsRead() != null && record.getIsRead() == 1);
        vo.setCreatedAt(record.getCreatedAt());
        return vo;
    }
}
