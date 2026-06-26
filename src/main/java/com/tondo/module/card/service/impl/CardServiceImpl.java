package com.tondo.module.card.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tondo.common.exception.BusinessException;
import com.tondo.module.card.entity.Card;
import com.tondo.module.card.entity.dto.CreateCardDTO;
import com.tondo.module.card.entity.vo.CardVO;
import com.tondo.module.card.mapper.CardMapper;
import com.tondo.module.card.service.CardService;
import com.tondo.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardMapper cardMapper;
    private final UserService userService;

    @Override
    public Card createCard(Long userId, CreateCardDTO dto) {
        userService.assertUserActive(userId);
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
    public CardVO getCardVO(Long cardId) {
        Card card = cardMapper.selectById(cardId);
        if (card == null) {
            throw new BusinessException("卡片不存在");
        }
        if ("HIDDEN".equals(card.getStatus())) {
            throw new BusinessException("卡片不可见");
        }
        card.setViewCount((card.getViewCount() != null ? card.getViewCount() : 0) + 1);
        cardMapper.updateById(card);
        String nickname = userService.getNicknameMap(List.of(card.getUserId()))
                .getOrDefault(card.getUserId(), "用户" + card.getUserId());
        return CardVO.from(card, nickname);
    }

    @Override
    public Page<CardVO> getCardVOs(int page, int size, String confusionTag, String sort) {
        Page<Card> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Card> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Card::getStatus, "PUBLISHED", "RESOLVED");

        if (confusionTag != null && !confusionTag.isEmpty()) {
            wrapper.like(Card::getConfusionTags, confusionTag);
        }

        if ("hot".equals(sort)) {
            wrapper.orderByDesc(Card::getHeatScore);
        } else {
            wrapper.orderByDesc(Card::getCreatedAt);
        }

        Page<Card> cardPage = cardMapper.selectPage(pageParam, wrapper);
        List<Long> userIds = cardPage.getRecords().stream().map(Card::getUserId).distinct().collect(Collectors.toList());
        Map<Long, String> nicknames = userService.getNicknameMap(userIds);

        Page<CardVO> voPage = new Page<>(cardPage.getCurrent(), cardPage.getSize(), cardPage.getTotal());
        voPage.setRecords(cardPage.getRecords().stream()
                .map(card -> CardVO.from(card, nicknames.getOrDefault(card.getUserId(), "用户" + card.getUserId())))
                .collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public void resolveCard(Long userId, Long cardId, String resolutionContent) {
        userService.assertUserActive(userId);
        Card card = cardMapper.selectById(cardId);
        if (card == null) {
            throw new BusinessException("卡片不存在");
        }
        if (!card.getUserId().equals(userId)) {
            throw new BusinessException("只能标记自己的卡片");
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

    @Override
    public void recalculateHeatScore(Long cardId) {
        Card card = cardMapper.selectById(cardId);
        if (card == null || card.getCreatedAt() == null) {
            return;
        }
        long hours = Math.max(Duration.between(card.getCreatedAt(), LocalDateTime.now()).toHours(), 0);
        double score = card.getThanksCount() / (hours + 2.0);
        card.setHeatScore(Math.round(score * 100.0) / 100.0);
        cardMapper.updateById(card);
    }
}
