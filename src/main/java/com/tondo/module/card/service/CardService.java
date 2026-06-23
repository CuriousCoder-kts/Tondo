package com.tondo.module.card.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tondo.module.card.entity.Card;
import com.tondo.module.card.entity.dto.CreateCardDTO;

public interface CardService {
    Card createCard(Long userId, CreateCardDTO dto);
    Card getCardById(Long cardId);
    Page<Card> getCards(int page, int size, String confusionTag, String sort);
    void resolveCard(Long userId, Long cardId, String resolutionContent);
    void incrementReplyCount(Long cardId);
    void incrementThanksCount(Long cardId);
}