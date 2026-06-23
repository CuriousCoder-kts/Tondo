package com.tondo.module.card.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("reply")
public class Reply {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long cardId;
    private Long userId;
    private Long parentId;
    private String experienceSituation;
    private String experienceAction;
    private String experienceResult;
    private String replyType;      // EXPERIENCE / SUPPORT
    private Integer thanksCount;
    private Integer isHidden;
    private LocalDateTime createdAt;
}