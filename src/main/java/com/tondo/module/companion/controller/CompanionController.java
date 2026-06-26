package com.tondo.module.companion.controller;

import com.tondo.common.response.Result;
import com.tondo.module.companion.entity.CompanionPlan;
import com.tondo.module.companion.entity.CompanionRelation;
import com.tondo.module.companion.entity.dto.CheckinDTO;
import com.tondo.module.companion.entity.dto.CreatePlanDTO;
import com.tondo.module.companion.entity.dto.InviteDTO;
import com.tondo.module.companion.entity.vo.CheckinResultVO;
import com.tondo.module.companion.entity.vo.CheckinStatusVO;
import com.tondo.module.companion.entity.vo.CompanionPlanVO;
import com.tondo.module.companion.entity.vo.CompanionRelationVO;
import com.tondo.module.companion.entity.vo.MatchCandidateVO;
import com.tondo.module.companion.service.CompanionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companion")
@RequiredArgsConstructor
public class CompanionController {

    private final CompanionService companionService;

    // 创建陪伴计划
    @PostMapping("/plans")
    public Result<CompanionPlan> createPlan(@RequestAttribute("userId") Long userId,
                                            @Valid @RequestBody CreatePlanDTO dto) {
        CompanionPlan plan = companionService.createPlan(userId, dto);
        return Result.success(plan);
    }

    // 获取可加入的陪伴计划列表
    @GetMapping("/plans")
    public Result<List<CompanionPlanVO>> getSeekingPlans(
            @RequestParam(required = false) String confusionTag) {
        List<CompanionPlanVO> plans = companionService.getSeekingPlans(confusionTag);
        return Result.success(plans);
    }

    // 获取我创建的陪伴计划
    @GetMapping("/my-plans")
    public Result<List<CompanionPlan>> getMyPlans(@RequestAttribute("userId") Long userId) {
        List<CompanionPlan> plans = companionService.getMyPlans(userId);
        return Result.success(plans);
    }

    // 邀请某人加入陪伴计划
    @PostMapping("/invite")
    public Result<CompanionRelation> invite(@RequestAttribute("userId") Long userId,
                                            @Valid @RequestBody InviteDTO dto) {
        CompanionRelation relation = companionService.invite(userId, dto);
        return Result.success(relation);
    }

    // 响应邀请（接受/拒绝）
    @PutMapping("/invitations/{relationId}")
    public Result<CompanionRelation> respondToInvitation(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long relationId,
            @RequestParam boolean accept) {
        CompanionRelation relation = companionService.respondToInvitation(userId, relationId, accept);
        return Result.success(relation);
    }

    // 我的全部陪伴关系
    @GetMapping("/relations")
    public Result<List<CompanionRelationVO>> getMyRelations(@RequestAttribute("userId") Long userId) {
        return Result.success(companionService.getMyRelations(userId));
    }

    // 待处理的邀请（我是被邀请人）
    @GetMapping("/invitations/pending")
    public Result<List<CompanionRelationVO>> getPendingInvitations(@RequestAttribute("userId") Long userId) {
        return Result.success(companionService.getPendingInvitations(userId));
    }

    // 匹配推荐
    @GetMapping("/match")
    public Result<List<MatchCandidateVO>> matchCandidates(@RequestAttribute("userId") Long userId,
                                                          @RequestParam Long planId) {
        return Result.success(companionService.matchCandidates(userId, planId));
    }

    // 每日打卡
    @PostMapping("/relations/{relationId}/checkin")
    public Result<CheckinResultVO> checkin(@RequestAttribute("userId") Long userId,
                                           @PathVariable Long relationId,
                                           @RequestBody(required = false) CheckinDTO dto) {
        String note = dto != null ? dto.getNote() : null;
        return Result.success(companionService.checkin(userId, relationId, note));
    }

    // 打卡进度与日历
    @GetMapping("/relations/{relationId}/checkin-status")
    public Result<CheckinStatusVO> getCheckinStatus(@RequestAttribute("userId") Long userId,
                                                    @PathVariable Long relationId) {
        return Result.success(companionService.getCheckinStatus(userId, relationId));
    }
}