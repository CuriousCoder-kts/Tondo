package com.tondo.module.card.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tondo.module.card.entity.Reply;
import com.tondo.module.card.entity.ThanksRecord;
import com.tondo.module.card.entity.dto.CreateReplyDTO;
import com.tondo.module.card.mapper.ReplyMapper;
import com.tondo.module.card.mapper.ThanksRecordMapper;
import com.tondo.module.card.service.CardService;
import com.tondo.module.card.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {

    private final ReplyMapper replyMapper;
    private final ThanksRecordMapper thanksRecordMapper;
    private final CardService cardService;

    @Override
    @Transactional
    public Reply createReply(Long userId, Long cardId, CreateReplyDTO dto) {
        Reply reply = new Reply();
        reply.setCardId(cardId);
        reply.setUserId(userId);
        reply.setExperienceSituation(dto.getExperienceSituation());
        reply.setExperienceAction(dto.getExperienceAction());
        reply.setExperienceResult(dto.getExperienceResult());
        reply.setReplyType(dto.getReplyType());
        reply.setThanksCount(0);
        reply.setIsHidden(0);
        replyMapper.insert(reply);

        // 更新卡片的回复数
        cardService.incrementReplyCount(cardId);
        return reply;
    }

    @Override
    public List<Reply> getRepliesByCardId(Long cardId) {
        LambdaQueryWrapper<Reply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reply::getCardId, cardId)
                .eq(Reply::getIsHidden, 0)
                .orderByAsc(Reply::getCreatedAt);
        return replyMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void thankReply(Long userId, Long replyId) {
        Reply reply = replyMapper.selectById(replyId);
        if (reply == null) {
            throw new RuntimeException("回复不存在");
        }
        // 检查是否已感谢
        LambdaQueryWrapper<ThanksRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ThanksRecord::getUserId, userId)
                .eq(ThanksRecord::getTargetType, "REPLY")
                .eq(ThanksRecord::getTargetId, replyId);
        if (thanksRecordMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("已经感谢过了");
        }
        // 插入感谢记录
        ThanksRecord record = new ThanksRecord();
        record.setUserId(userId);
        record.setTargetType("REPLY");
        record.setTargetId(replyId);
        thanksRecordMapper.insert(record);

        // 更新回复的感谢数
        reply.setThanksCount(reply.getThanksCount() + 1);
        replyMapper.updateById(reply);
    }
}