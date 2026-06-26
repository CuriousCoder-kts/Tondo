package com.tondo.module.card.entity.vo;

import com.tondo.module.card.entity.Card;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CardVO {
    private Long id;
    private Long userId;
    private String authorNickname;
    private String title;
    private String eventDescription;
    private String emotionTags;
    private String attemptDescription;
    private String needType;
    private String confusionTags;
    private String status;
    private String resolutionContent;
    private LocalDateTime resolvedAt;
    private Integer thanksCount;
    private Integer replyCount;
    private Integer viewCount;
    private Double heatScore;
    private Integer isPinned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CardVO from(Card card, String authorNickname) {
        CardVO vo = new CardVO();
        vo.setId(card.getId());
        vo.setUserId(card.getUserId());
        vo.setAuthorNickname(authorNickname);
        vo.setTitle(card.getTitle());
        vo.setEventDescription(card.getEventDescription());
        vo.setEmotionTags(card.getEmotionTags());
        vo.setAttemptDescription(card.getAttemptDescription());
        vo.setNeedType(card.getNeedType());
        vo.setConfusionTags(card.getConfusionTags());
        vo.setStatus(card.getStatus());
        vo.setResolutionContent(card.getResolutionContent());
        vo.setResolvedAt(card.getResolvedAt());
        vo.setThanksCount(card.getThanksCount());
        vo.setReplyCount(card.getReplyCount());
        vo.setViewCount(card.getViewCount());
        vo.setHeatScore(card.getHeatScore());
        vo.setIsPinned(card.getIsPinned());
        vo.setCreatedAt(card.getCreatedAt());
        vo.setUpdatedAt(card.getUpdatedAt());
        return vo;
    }
}
