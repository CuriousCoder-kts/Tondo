package com.tondo.module.card.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("confusion_card")
public class Card {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String eventDescription;
    private String emotionTags;       // JSON 字符串
    private String attemptDescription;
    private String needType;          // EMPATHY / ADVICE / COMPANION
    private String confusionTags;     // JSON 字符串
    private String status;            // DRAFT / PUBLISHED / RESOLVED / HIDDEN
    private String resolutionContent;
    private LocalDateTime resolvedAt;
    private Integer thanksCount;
    private Integer replyCount;
    private Integer viewCount;
    private Double heatScore;
    private Integer isPinned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}