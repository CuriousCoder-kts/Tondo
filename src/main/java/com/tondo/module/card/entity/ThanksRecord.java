package com.tondo.module.card.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("thanks_record")
public class ThanksRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String targetType;   // CARD / REPLY
    private Long targetId;
    private LocalDateTime createdAt;
}