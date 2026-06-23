package com.tondo.module.card.service;

import com.tondo.module.card.entity.Reply;
import com.tondo.module.card.entity.dto.CreateReplyDTO;
import java.util.List;

public interface ReplyService {
    Reply createReply(Long userId, Long cardId, CreateReplyDTO dto);
    List<Reply> getRepliesByCardId(Long cardId);
    void thankReply(Long userId, Long replyId);
}