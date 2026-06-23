package com.tondo.module.card.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tondo.module.card.entity.Card;
import com.tondo.module.card.entity.dto.CreateCardDTO;
import com.tondo.module.card.mapper.CardMapper;
import com.tondo.module.card.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardMapper cardMapper;

    @Override
    public Card createCard(Long userId, CreateCardDTO dto) {
        Card card = new Card();
        card.setUserId(userId);
        card.setTitle(dto.getTitle());
        card.setEventDescription(dto.getEventDescription());
        card.setEmotionTags(dto.getEmotionTags());
        card.setAttemptDescription(dto.getAttemptDescription());
        card.setNeedType(dto.getNeedType());
        card.setConfusionTags(dto.getConfusionTags());
        card.setStatus("PUBLISHED");
        card.setThanksCount(0);
        card.setReplyCount(0);
        card.setViewCount(0);
        card.setHeatScore(0.0);
        card.setIsPinned(0);
        cardMapper.insert(card);
        return card;
    }

    @Override
    public Card getCardById(Long cardId) {
        Card card = cardMapper.selectById(cardId);
        if (card == null) {
            throw new RuntimeException("卡片不存在");
        }
        return card;
    }

    @Override
    public Page<Card> getCards(int page, int size, String confusionTag, String sort) {
        Page<Card> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Card> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Card::getStatus, "PUBLISHED");

        // 按困惑领域标签筛选
        if (confusionTag != null && !confusionTag.isEmpty()) {
            // 虚拟列 confusion_tags_text 在 MyBatis-Plus 里通过 Card 实体字段 confusionTags 自动映射
            // 这里用 LIKE 模糊查询
            wrapper.like(Card::getConfusionTags, confusionTag);
        }

        // 排序：hot = 热度优先，new = 最新优先
        if ("hot".equals(sort)) {
            wrapper.orderByDesc(Card::getHeatScore);
        } else {
            wrapper.orderByDesc(Card::getCreatedAt);
        }

        return cardMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public void resolveCard(Long userId, Long cardId, String resolutionContent) {
        Card card = cardMapper.selectById(cardId);
        if (card == null) {
            throw new RuntimeException("卡片不存在");
        }
        if (!card.getUserId().equals(userId)) {
            throw new RuntimeException("只能标记自己的卡片");
        }
        card.setStatus("RESOLVED");
        card.setResolutionContent(resolutionContent);
        card.setResolvedAt(LocalDateTime.now());
        cardMapper.updateById(card);
    }

    @Override
    public void incrementReplyCount(Long cardId) {
        Card card = cardMapper.selectById(cardId);
        if (card != null) {
            card.setReplyCount(card.getReplyCount() + 1);
            cardMapper.updateById(card);
        }
    }

    @Override
    public void incrementThanksCount(Long cardId) {
        Card card = cardMapper.selectById(cardId);
        if (card != null) {
            card.setThanksCount(card.getThanksCount() + 1);
            cardMapper.updateById(card);
        }
    }
}