package com.tondo.module.companion.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("companion_plan")
public class CompanionPlan {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long creatorId;
    private String title;
    private String goalDescription;
    private String confusionTags;       // JSON
    private Integer durationDays;
    private String checkinFrequency;    // DAILY / WEEKLY / CUSTOM
    private String companionStylePreferred; // STRICT / ENCOURAGING / QUIET / ANY
    private String status;              // SEEKING / IN_PROGRESS / COMPLETED / CANCELLED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}