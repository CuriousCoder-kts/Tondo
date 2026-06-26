package com.tondo.module.message.service;

import com.tondo.common.response.PageResult;
import com.tondo.module.message.entity.PrivateMessage;

import java.util.List;

public interface PrivateMessageService {
    PrivateMessage sendMessage(Long senderId, Long relationId, String content);

    List<PrivateMessage> getMessagesByRelationId(Long userId, Long relationId, int page, int size);

    PageResult<PrivateMessage> getMessagesPage(Long userId, Long relationId, int page, int size);

    void markMessagesAsRead(Long userId, Long relationId);

    void assertRelationParticipant(Long userId, Long relationId);
}
