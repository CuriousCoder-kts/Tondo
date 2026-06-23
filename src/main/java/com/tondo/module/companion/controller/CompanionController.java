package com.tondo.module.companion.controller;

import com.tondo.common.response.Result;
import com.tondo.module.companion.entity.CompanionPlan;
import com.tondo.module.companion.entity.CompanionRelation;
import com.tondo.module.companion.entity.dto.CreatePlanDTO;
import com.tondo.module.companion.entity.dto.InviteDTO;
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
    public Result<List<CompanionPlan>> getSeekingPlans(
            @RequestParam(required = false) String confusionTag) {
        List<CompanionPlan> plans = companionService.getSeekingPlans(confusionTag);
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
}