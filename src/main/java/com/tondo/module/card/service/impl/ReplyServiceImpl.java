package com.tondo.module.card.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tondo.common.exception.BusinessException;
import com.tondo.module.card.entity.Card;
import com.tondo.module.card.entity.Reply;
import com.tondo.module.card.entity.ThanksRecord;
import com.tondo.module.card.entity.dto.CreateReplyDTO;
import com.tondo.module.card.entity.vo.ReplyVO;
import com.tondo.module.card.mapper.CardMapper;
import com.tondo.module.card.mapper.ReplyMapper;
import com.tondo.module.card.mapper.ThanksRecordMapper;
import com.tondo.module.card.service.CardService;
import com.tondo.module.card.service.ReplyService;
import com.tondo.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {

    private final ReplyMapper replyMapper;
    private final ThanksRecordMapper thanksRecordMapper;
    private final CardMapper cardMapper;
    private final CardService cardService;
    private final UserService userService;

    @Override
    @Transactional
    public ReplyVO createReply(Long userId, Long cardId, CreateReplyDTO dto) {
        userService.assertUserActive(userId);
        Card card = cardMapper.selectById(cardId);
        if (card == null || "HIDDEN".equals(card.getStatus())) {
            throw new BusinessException("卡片不存在或不可回复");
        }

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

        cardService.incrementReplyCount(cardId);
        String nickname = userService.getNicknameMap(List.of(userId))
                .getOrDefault(userId, "用户" + userId);
        return ReplyVO.from(reply, nickname);
    }

    @Override
    public List<ReplyVO> getReplyVOsByCardId(Long cardId) {
        LambdaQueryWrapper<Reply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reply::getCardId, cardId)
                .eq(Reply::getIsHidden, 0)
                .orderByAsc(Reply::getCreatedAt);
        List<Reply> replies = replyMapper.selectList(wrapper);
        List<Long> userIds = replies.stream().map(Reply::getUserId).distinct().collect(Collectors.toList());
        Map<Long, String> nicknames = userService.getNicknameMap(userIds);
        return replies.stream()
                .map(reply -> ReplyVO.from(reply, nicknames.getOrDefault(reply.getUserId(), "用户" + reply.getUserId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void thankReply(Long userId, Long replyId) {
        userService.assertUserActive(userId);
        Reply reply = replyMapper.selectById(replyId);
        if (reply == null) {
            throw new BusinessException("回复不存在");
        }
        LambdaQueryWrapper<ThanksRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ThanksRecord::getUserId, userId)
                .eq(ThanksRecord::getTargetType, "REPLY")
                .eq(ThanksRecord::getTargetId, replyId);
        if (thanksRecordMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("已经感谢过了");
        }
        ThanksRecord record = new ThanksRecord();
        record.setUserId(userId);
        record.setTargetType("REPLY");
        record.setTargetId(replyId);
        thanksRecordMapper.insert(record);

        reply.setThanksCount(reply.getThanksCount() + 1);
        replyMapper.updateById(reply);
    }
}
