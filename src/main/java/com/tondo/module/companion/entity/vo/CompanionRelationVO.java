package com.tondo.module.companion.entity.vo;

import com.tondo.module.companion.entity.CompanionRelation;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompanionRelationVO {
    private Long id;
    private Long planId;
    private String planTitle;
    private Long inviterId;
    private String inviterNickname;
    private Long inviteeId;
    private String inviteeNickname;
    private Long partnerId;
    private String partnerNickname;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Integer dailyCheckinCount;
    private Integer durationDays;
    private Integer daysRemaining;
    private Boolean checkedInToday;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CompanionRelationVO from(
            CompanionRelation relation,
            String planTitle,
            Integer durationDays,
            Integer daysRemaining,
            Boolean checkedInToday,
            String inviterNickname,
            String inviteeNickname,
            Long currentUserId) {
        CompanionRelationVO vo = new CompanionRelationVO();
        vo.setId(relation.getId());
        vo.setPlanId(relation.getPlanId());
        vo.setPlanTitle(planTitle);
        vo.setInviterId(relation.getInviterId());
        vo.setInviterNickname(inviterNickname);
        vo.setInviteeId(relation.getInviteeId());
        vo.setInviteeNickname(inviteeNickname);
        vo.setStatus(relation.getStatus());
        vo.setStartedAt(relation.getStartedAt());
        vo.setEndedAt(relation.getEndedAt());
        vo.setDailyCheckinCount(relation.getDailyCheckinCount());
        vo.setDurationDays(durationDays);
        vo.setDaysRemaining(daysRemaining);
        vo.setCheckedInToday(checkedInToday);
        vo.setCreatedAt(relation.getCreatedAt());
        vo.setUpdatedAt(relation.getUpdatedAt());

        if (currentUserId.equals(relation.getInviterId())) {
            vo.setPartnerId(relation.getInviteeId());
            vo.setPartnerNickname(inviteeNickname);
        } else {
            vo.setPartnerId(relation.getInviterId());
            vo.setPartnerNickname(inviterNickname);
        }
        return vo;
    }
}
