package com.tondo.module.user.entity.dto;

import lombok.Data;

@Data
public class UpdateProfileDTO {
    private String statusLabel;
    private String confusionTags;
    private String companionStyle;
    private String avatarUrl;
}
