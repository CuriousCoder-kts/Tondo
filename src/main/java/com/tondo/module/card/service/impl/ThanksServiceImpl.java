package com.tondo.module.card.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tondo.common.exception.BusinessException;
import com.tondo.module.card.entity.Card;
import com.tondo.module.card.entity.ThanksRecord;
import com.tondo.module.card.mapper.CardMapper;
import com.tondo.module.card.mapper.ThanksRecordMapper;
import com.tondo.module.card.service.CardService;
import com.tondo.module.card.service.ThanksService;
import com.tondo.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ThanksServiceImpl implements ThanksService {

    private final ThanksRecordMapper thanksRecordMapper;
    private final CardMapper cardMapper;
    private final CardService cardService;
    private final UserService userService;

    @Override
    @Transactional
    public void thankCard(Long userId, Long cardId) {
        userService.assertUserActive(userId);
        Card card = cardMapper.selectById(cardId);
        if (card == null || "HIDDEN".equals(card.getStatus())) {
            throw new BusinessException("卡片不存在");
        }
        LambdaQueryWrapper<ThanksRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ThanksRecord::getUserId, userId)
                .eq(ThanksRecord::getTargetType, "CARD")
                .eq(ThanksRecord::getTargetId, cardId);
        if (thanksRecordMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("已经感谢过了");
        }
        ThanksRecord record = new ThanksRecord();
        record.setUserId(userId);
        record.setTargetType("CARD");
        record.setTargetId(cardId);
        thanksRecordMapper.insert(record);

        card.setThanksCount(card.getThanksCount() + 1);
        cardMapper.updateById(card);
        cardService.recalculateHeatScore(cardId);
    }
}
