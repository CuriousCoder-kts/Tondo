package com.tondo.module.companion.entity.vo;

import com.tondo.module.companion.entity.CompanionPlan;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompanionPlanVO {
    private Long id;
    private Long creatorId;
    private String creatorNickname;
    private String title;
    private String goalDescription;
    private String confusionTags;
    private Integer durationDays;
    private String checkinFrequency;
    private String companionStylePreferred;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CompanionPlanVO from(CompanionPlan plan, String creatorNickname) {
        CompanionPlanVO vo = new CompanionPlanVO();
        vo.setId(plan.getId());
        vo.setCreatorId(plan.getCreatorId());
        vo.setCreatorNickname(creatorNickname);
        vo.setTitle(plan.getTitle());
        vo.setGoalDescription(plan.getGoalDescription());
        vo.setConfusionTags(plan.getConfusionTags());
        vo.setDurationDays(plan.getDurationDays());
        vo.setCheckinFrequency(plan.getCheckinFrequency());
        vo.setCompanionStylePreferred(plan.getCompanionStylePreferred());
        vo.setStatus(plan.getStatus());
        vo.setCreatedAt(plan.getCreatedAt());
        vo.setUpdatedAt(plan.getUpdatedAt());
        return vo;
    }
}
