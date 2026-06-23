package com.tondo.module.companion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tondo.module.companion.entity.CompanionPlan;
import com.tondo.module.companion.entity.CompanionRelation;
import com.tondo.module.companion.entity.dto.CreatePlanDTO;
import com.tondo.module.companion.entity.dto.InviteDTO;
import com.tondo.module.companion.mapper.CompanionPlanMapper;
import com.tondo.module.companion.mapper.CompanionRelationMapper;
import com.tondo.module.companion.service.CompanionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanionServiceImpl implements CompanionService {

    private final CompanionPlanMapper planMapper;
    private final CompanionRelationMapper relationMapper;

    @Override
    public CompanionPlan createPlan(Long userId, CreatePlanDTO dto) {
        CompanionPlan plan = new CompanionPlan();
        plan.setCreatorId(userId);
        plan.setTitle(dto.getTitle());
        plan.setGoalDescription(dto.getGoalDescription());
        plan.setConfusionTags(dto.getConfusionTags());
        plan.setDurationDays(dto.getDurationDays());
        plan.setCheckinFrequency(dto.getCheckinFrequency());
        plan.setCompanionStylePreferred(dto.getCompanionStylePreferred());
        plan.setStatus("SEEKING");
        planMapper.insert(plan);
        return plan;
    }

    @Override
    public List<CompanionPlan> getSeekingPlans(String confusionTag) {
        LambdaQueryWrapper<CompanionPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CompanionPlan::getStatus, "SEEKING");
        if (confusionTag != null && !confusionTag.isEmpty()) {
            wrapper.like(CompanionPlan::getConfusionTags, confusionTag);
        }
        wrapper.orderByDesc(CompanionPlan::getCreatedAt);
        return planMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public CompanionRelation invite(Long inviterId, InviteDTO dto) {
        CompanionPlan plan = planMapper.selectById(dto.getPlanId());
        if (plan == null) {
            throw new RuntimeException("陪伴计划不存在");
        }
        if (!plan.getCreatorId().equals(inviterId)) {
            throw new RuntimeException("只有计划创建者可以邀请");
        }
        if (!"SEEKING".equals(plan.getStatus()) && !"IN_PROGRESS".equals(plan.getStatus())) {
            throw new RuntimeException("该计划当前不可邀请");
        }
        // 检查是否已有待处理或进行中的关系
        LambdaQueryWrapper<CompanionRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CompanionRelation::getPlanId, dto.getPlanId())
                .eq(CompanionRelation::getInviteeId, dto.getInviteeId())
                .in(CompanionRelation::getStatus, "PENDING", "ACCEPTED");
        if (relationMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("已经对该用户发出过邀请或已是陪伴关系");
        }

        CompanionRelation relation = new CompanionRelation();
        relation.setPlanId(dto.getPlanId());
        relation.setInviterId(inviterId);
        relation.setInviteeId(dto.getInviteeId());
        relation.setStatus("PENDING");
        relationMapper.insert(relation);
        return relation;
    }

    @Override
    @Transactional
    public CompanionRelation respondToInvitation(Long userId, Long relationId, boolean accept) {
        CompanionRelation relation = relationMapper.selectById(relationId);
        if (relation == null) {
            throw new RuntimeException("邀请不存在");
        }
        if (!relation.getInviteeId().equals(userId)) {
            throw new RuntimeException("只有被邀请人可以处理此邀请");
        }
        if (!"PENDING".equals(relation.getStatus())) {
            throw new RuntimeException("该邀请已被处理");
        }

        if (accept) {
            relation.setStatus("ACCEPTED");
            relation.setStartedAt(LocalDateTime.now());
            // 更新计划状态
            CompanionPlan plan = planMapper.selectById(relation.getPlanId());
            if (plan != null) {
                plan.setStatus("IN_PROGRESS");
                planMapper.updateById(plan);
            }
        } else {
            relation.setStatus("REJECTED");
        }
        relationMapper.updateById(relation);
        return relation;
    }

    @Override
    public List<CompanionPlan> getMyPlans(Long userId) {
        LambdaQueryWrapper<CompanionPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CompanionPlan::getCreatorId, userId)
                .orderByDesc(CompanionPlan::getUpdatedAt);
        return planMapper.selectList(wrapper);
    }
}