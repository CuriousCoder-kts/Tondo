package com.tondo.module.companion.service;

import com.tondo.module.companion.entity.CompanionPlan;
import com.tondo.module.companion.entity.CompanionRelation;
import com.tondo.module.companion.entity.dto.CreatePlanDTO;
import com.tondo.module.companion.entity.dto.InviteDTO;

import java.util.List;

public interface CompanionService {
    CompanionPlan createPlan(Long userId, CreatePlanDTO dto);
    List<CompanionPlan> getSeekingPlans(String confusionTag);
    CompanionRelation invite(Long inviterId, InviteDTO dto);
    CompanionRelation respondToInvitation(Long userId, Long relationId, boolean accept);
    List<CompanionPlan> getMyPlans(Long userId);
}