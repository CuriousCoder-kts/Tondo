package com.tondo.module.card.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tondo.module.card.entity.Card;
import com.tondo.module.card.entity.dto.CreateCardDTO;
import com.tondo.module.card.entity.vo.CardVO;

public interface CardService {
    Card createCard(Long userId, CreateCardDTO dto);
    CardVO getCardVO(Long cardId);
    Page<CardVO> getCardVOs(int page, int size, String confusionTag, String sort);
    void resolveCard(Long userId, Long cardId, String resolutionContent);
    void incrementReplyCount(Long cardId);
    void incrementThanksCount(Long cardId);
    void recalculateHeatScore(Long cardId);
}