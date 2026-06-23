package com.tondo.module.companion.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("companion_relation")
public class CompanionRelation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long planId;
    private Long inviterId;
    private Long inviteeId;
    private String status;    // PENDING / ACCEPTED / COMPLETED / CANCELLED / REJECTED
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Integer dailyCheckinCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}