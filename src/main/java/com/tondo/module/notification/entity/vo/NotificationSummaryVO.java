package com.tondo.module.notification.entity.vo;

import lombok.Data;

@Data
public class NotificationSummaryVO {
    private int pendingInvitations;
    private int unreadMessages;
    private int unreadInbox;

    public int getTotalUnread() {
        return pendingInvitations + unreadMessages + unreadInbox;
    }
}
