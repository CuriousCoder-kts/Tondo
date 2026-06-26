package com.tondo.module.companion.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class MatchCandidateVO {
    private Long userId;
    private String nickname;
    private String statusLabel;
    private String confusionTags;
    private String companionStyle;
    private Integer trustLevel;
    private Double matchScore;
    private List<String> sharedTags;
}
