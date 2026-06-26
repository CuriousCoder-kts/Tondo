package com.tondo.module.companion.service;

import com.tondo.module.companion.entity.CompanionPlan;
import com.tondo.module.companion.entity.CompanionRelation;
import com.tondo.module.companion.entity.dto.CreatePlanDTO;
import com.tondo.module.companion.entity.dto.InviteDTO;
import com.tondo.module.companion.entity.vo.CheckinResultVO;
import com.tondo.module.companion.entity.vo.CheckinStatusVO;
import com.tondo.module.companion.entity.vo.CompanionPlanVO;
import com.tondo.module.companion.entity.vo.CompanionRelationVO;
import com.tondo.module.companion.entity.vo.MatchCandidateVO;

import java.util.List;

public interface CompanionService {
    CompanionPlan createPlan(Long userId, CreatePlanDTO dto);
    List<CompanionPlanVO> getSeekingPlans(String confusionTag);
    CompanionRelation invite(Long inviterId, InviteDTO dto);
    CompanionRelation respondToInvitation(Long userId, Long relationId, boolean accept);
    List<CompanionPlan> getMyPlans(Long userId);
    List<CompanionRelationVO> getMyRelations(Long userId);
    List<CompanionRelationVO> getPendingInvitations(Long userId);
    List<MatchCandidateVO> matchCandidates(Long userId, Long planId);

    CheckinResultVO checkin(Long userId, Long relationId, String note);

    CheckinStatusVO getCheckinStatus(Long userId, Long relationId);
}