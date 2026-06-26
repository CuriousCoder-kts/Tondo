package com.tondo.module.user.entity.vo;

import lombok.Data;

@Data
public class UserBriefVO {
    private Long id;
    private String nickname;
    private String avatarUrl;
    private String statusLabel;
    private String confusionTags;
    private String companionStyle;
    private Integer trustLevel;
    private String role;
    private Integer isFrozen;
}
