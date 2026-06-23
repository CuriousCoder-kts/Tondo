package com.tondo.module.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("user")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String phone;
    private String passwordHash;
    private String nickname;
    private String avatarUrl;
    private String statusLabel;
    private String confusionTags;
    private String companionStyle;
    private Integer trustLevel;
    private String role;
    private Integer isFrozen;
    private Integer signedCommunityRule;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private LocalDateTime createdAt;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private LocalDateTime updatedAt;
}