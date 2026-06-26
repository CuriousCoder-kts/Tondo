package com.tondo.module.card.service;

import com.tondo.module.card.entity.Reply;
import com.tondo.module.card.entity.dto.CreateReplyDTO;
import com.tondo.module.card.entity.vo.ReplyVO;

import java.util.List;

public interface ReplyService {
    ReplyVO createReply(Long userId, Long cardId, CreateReplyDTO dto);
    List<ReplyVO> getReplyVOsByCardId(Long cardId);
    void thankReply(Long userId, Long replyId);
}