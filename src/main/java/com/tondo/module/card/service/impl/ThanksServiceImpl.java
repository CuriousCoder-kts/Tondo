package com.tondo.module.card.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tondo.module.card.entity.Card;
import com.tondo.module.card.entity.ThanksRecord;
import com.tondo.module.card.mapper.CardMapper;
import com.tondo.module.card.mapper.ThanksRecordMapper;
import com.tondo.module.card.service.ThanksService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ThanksServiceImpl implements ThanksService {

    private final ThanksRecordMapper thanksRecordMapper;
    private final CardMapper cardMapper;

    @Override
    @Transactional
    public void thankCard(Long userId, Long cardId) {
        Card card = cardMapper.selectById(cardId);
        if (card == null) {
            throw new RuntimeException("卡片不存在");
        }
        // 防止重复感谢
        LambdaQueryWrapper<ThanksRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ThanksRecord::getUserId, userId)
                .eq(ThanksRecord::getTargetType, "CARD")
                .eq(ThanksRecord::getTargetId, cardId);
        if (thanksRecordMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("已经感谢过了");
        }
        ThanksRecord record = new ThanksRecord();
        record.setUserId(userId);
        record.setTargetType("CARD");
        record.setTargetId(cardId);
        thanksRecordMapper.insert(record);

        // 更新卡片感谢数
        card.setThanksCount(card.getThanksCount() + 1);
        cardMapper.updateById(card);
    }
}