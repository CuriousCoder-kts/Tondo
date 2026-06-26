package com.tondo.module.companion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tondo.common.exception.BusinessException;
import com.tondo.common.util.TagUtil;
import com.tondo.module.companion.entity.CompanionCheckin;
import com.tondo.module.companion.entity.CompanionPlan;
import com.tondo.module.companion.entity.CompanionRelation;
import com.tondo.module.companion.entity.dto.CreatePlanDTO;
import com.tondo.module.companion.entity.dto.InviteDTO;
import com.tondo.module.companion.entity.vo.CheckinResultVO;
import com.tondo.module.companion.entity.vo.CheckinStatusVO;
import com.tondo.module.companion.entity.vo.CompanionPlanVO;
import com.tondo.module.companion.entity.vo.CompanionRelationVO;
import com.tondo.module.companion.entity.vo.MatchCandidateVO;
import com.tondo.module.companion.mapper.CompanionCheckinMapper;
import com.tondo.module.companion.mapper.CompanionPlanMapper;
import com.tondo.module.companion.mapper.CompanionRelationMapper;
import com.tondo.module.notification.entity.vo.NotificationVO;
import com.tondo.module.notification.service.NotificationService;
import com.tondo.module.companion.service.CompanionService;
import com.tondo.module.user.entity.User;
import com.tondo.module.user.mapper.UserMapper;
import com.tondo.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanionServiceImpl implements CompanionService {

    private final CompanionPlanMapper planMapper;
    private final CompanionRelationMapper relationMapper;
    private final CompanionCheckinMapper checkinMapper;
    private final UserMapper userMapper;
    private final UserService userService;
    private final NotificationService notificationService;

    @Override
    @CacheEvict(cacheNames = "seekingPlans", allEntries = true)
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
    @Cacheable(cacheNames = "seekingPlans", key = "#confusionTag == null ? 'all' : #confusionTag")
    public List<CompanionPlanVO> getSeekingPlans(String confusionTag) {
        LambdaQueryWrapper<CompanionPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CompanionPlan::getStatus, "SEEKING");
        if (confusionTag != null && !confusionTag.isEmpty()) {
            wrapper.like(CompanionPlan::getConfusionTags, confusionTag);
        }
        wrapper.orderByDesc(CompanionPlan::getCreatedAt);
        List<CompanionPlan> plans = planMapper.selectList(wrapper);
        if (plans.isEmpty()) {
            return List.of();
        }
        Set<Long> creatorIds = plans.stream().map(CompanionPlan::getCreatorId).collect(Collectors.toSet());
        Map<Long, String> nicknames = userService.getNicknameMap(creatorIds);
        return plans.stream()
                .map(plan -> CompanionPlanVO.from(
                        plan,
                        nicknames.getOrDefault(plan.getCreatorId(), "用户" + plan.getCreatorId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CompanionRelation invite(Long inviterId, InviteDTO dto) {
        if (dto.getInviteeId().equals(inviterId)) {
            throw new BusinessException("不能邀请自己");
        }

        CompanionPlan plan = planMapper.selectById(dto.getPlanId());
        if (plan == null) {
            throw new BusinessException("陪伴计划不存在");
        }
        if (!plan.getCreatorId().equals(inviterId)) {
            throw new BusinessException("只有计划创建者可以邀请");
        }
        if (!"SEEKING".equals(plan.getStatus())) {
            throw new BusinessException("该计划当前不可邀请");
        }

        LambdaQueryWrapper<CompanionRelation> acceptedWrapper = new LambdaQueryWrapper<>();
        acceptedWrapper.eq(CompanionRelation::getPlanId, dto.getPlanId())
                .eq(CompanionRelation::getStatus, "ACCEPTED");
        if (relationMapper.selectCount(acceptedWrapper) > 0) {
            throw new BusinessException("该计划已有陪伴者");
        }

        userService.getUserBrief(dto.getInviteeId(), inviterId);

        LambdaQueryWrapper<CompanionRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CompanionRelation::getPlanId, dto.getPlanId())
                .eq(CompanionRelation::getInviteeId, dto.getInviteeId())
                .in(CompanionRelation::getStatus, "PENDING", "ACCEPTED");
        if (relationMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("已经对该用户发出过邀请或已是陪伴关系");
        }

        CompanionRelation relation = new CompanionRelation();
        relation.setPlanId(dto.getPlanId());
        relation.setInviterId(inviterId);
        relation.setInviteeId(dto.getInviteeId());
        relation.setStatus("PENDING");
        relationMapper.insert(relation);

        String inviterName = userService.getNicknameMap(List.of(inviterId))
                .getOrDefault(inviterId, "用户" + inviterId);
        NotificationVO notification = new NotificationVO();
        notification.setType("INVITE_RECEIVED");
        notification.setTitle("收到陪伴邀请");
        notification.setContent(inviterName + " 邀请你加入「" + plan.getTitle() + "」");
        notification.setRelationId(relation.getId());
        notification.setPlanId(plan.getId());
        notification.setSenderId(inviterId);
        notificationService.send(dto.getInviteeId(), notification);

        return relation;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "seekingPlans", allEntries = true)
    public CompanionRelation respondToInvitation(Long userId, Long relationId, boolean accept) {
        CompanionRelation relation = relationMapper.selectById(relationId);
        if (relation == null) {
            throw new BusinessException("邀请不存在");
        }
        if (!relation.getInviteeId().equals(userId)) {
            throw new BusinessException("只有被邀请人可以处理此邀请");
        }
        if (!"PENDING".equals(relation.getStatus())) {
            throw new BusinessException("该邀请已被处理");
        }

        if (accept) {
            LambdaQueryWrapper<CompanionRelation> acceptedWrapper = new LambdaQueryWrapper<>();
            acceptedWrapper.eq(CompanionRelation::getPlanId, relation.getPlanId())
                    .eq(CompanionRelation::getStatus, "ACCEPTED");
            if (relationMapper.selectCount(acceptedWrapper) > 0) {
                throw new BusinessException("该计划已有陪伴者");
            }

            LambdaQueryWrapper<CompanionRelation> pendingOthers = new LambdaQueryWrapper<>();
            pendingOthers.eq(CompanionRelation::getPlanId, relation.getPlanId())
                    .eq(CompanionRelation::getStatus, "PENDING")
                    .ne(CompanionRelation::getId, relationId);
            for (CompanionRelation other : relationMapper.selectList(pendingOthers)) {
                other.setStatus("REJECTED");
                relationMapper.updateById(other);
            }

            relation.setStatus("ACCEPTED");
            relation.setStartedAt(LocalDateTime.now());
            CompanionPlan plan = planMapper.selectById(relation.getPlanId());
            if (plan != null) {
                plan.setStatus("IN_PROGRESS");
                planMapper.updateById(plan);
            }

            String inviteeName = userService.getNicknameMap(List.of(userId))
                    .getOrDefault(userId, "用户" + userId);
            NotificationVO notification = new NotificationVO();
            notification.setType("INVITE_ACCEPTED");
            notification.setTitle("邀请已被接受");
            notification.setContent(inviteeName + " 接受了你的陪伴邀请，可以开始私聊了");
            notification.setRelationId(relation.getId());
            notification.setPlanId(relation.getPlanId());
            notification.setSenderId(userId);
            notificationService.send(relation.getInviterId(), notification);
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

    @Override
    public List<CompanionRelationVO> getMyRelations(Long userId) {
        LambdaQueryWrapper<CompanionRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(CompanionRelation::getInviterId, userId)
                        .or()
                        .eq(CompanionRelation::getInviteeId, userId))
                .orderByDesc(CompanionRelation::getUpdatedAt);
        List<CompanionRelation> relations = relationMapper.selectList(wrapper);

        for (CompanionRelation relation : relations) {
            if ("ACCEPTED".equals(relation.getStatus())) {
                CompanionPlan plan = planMapper.selectById(relation.getPlanId());
                if (plan != null) {
                    completeIfExpired(relation, plan);
                }
            }
        }

        return toRelationVOList(relations, userId);
    }

    @Override
    public List<CompanionRelationVO> getPendingInvitations(Long userId) {
        LambdaQueryWrapper<CompanionRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CompanionRelation::getInviteeId, userId)
                .eq(CompanionRelation::getStatus, "PENDING")
                .orderByDesc(CompanionRelation::getCreatedAt);
        return toRelationVOList(relationMapper.selectList(wrapper), userId);
    }

    @Override
    public List<MatchCandidateVO> matchCandidates(Long userId, Long planId) {
        CompanionPlan plan = planMapper.selectById(planId);
        if (plan == null) {
            throw new BusinessException("陪伴计划不存在");
        }
        if (!plan.getCreatorId().equals(userId)) {
            throw new BusinessException("只有计划创建者可以查看匹配推荐");
        }

        Set<String> planTags = TagUtil.parseTagSet(plan.getConfusionTags());
        Set<Long> excludedUserIds = getExcludedUserIds(planId, userId);

        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.ne(User::getId, userId);
        userWrapper.and(w -> w.eq(User::getIsFrozen, 0).or().isNull(User::getIsFrozen));
        List<User> candidates = userMapper.selectList(userWrapper);

        List<MatchCandidateVO> results = new ArrayList<>();
        for (User candidate : candidates) {
            if (excludedUserIds.contains(candidate.getId())) {
                continue;
            }
            Set<String> userTags = TagUtil.parseTagSet(candidate.getConfusionTags());
            double tagScore = TagUtil.overlapScore(planTags, userTags);
            double styleScore = styleMatch(plan.getCompanionStylePreferred(), candidate.getCompanionStyle());
            double trustScore = Math.min(candidate.getTrustLevel() / 5.0, 1.0);
            double totalScore = tagScore * 0.6 + styleScore * 0.3 + trustScore * 0.1;

            MatchCandidateVO vo = new MatchCandidateVO();
            vo.setUserId(candidate.getId());
            vo.setNickname(candidate.getNickname());
            vo.setStatusLabel(candidate.getStatusLabel());
            vo.setConfusionTags(candidate.getConfusionTags());
            vo.setCompanionStyle(candidate.getCompanionStyle());
            vo.setTrustLevel(candidate.getTrustLevel());
            vo.setMatchScore(Math.round(totalScore * 100.0) / 100.0);
            vo.setSharedTags(planTags.stream().filter(userTags::contains).collect(Collectors.toList()));
            results.add(vo);
        }

        return results.stream()
                .sorted(Comparator.comparing(MatchCandidateVO::getMatchScore).reversed())
                .limit(20)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CheckinResultVO checkin(Long userId, Long relationId, String note) {
        CompanionRelation relation = getRelationForParticipant(relationId, userId);
        CompanionPlan plan = planMapper.selectById(relation.getPlanId());
        if (plan == null) {
            throw new BusinessException("陪伴计划不存在");
        }

        completeIfExpired(relation, plan);
        if (!"ACCEPTED".equals(relation.getStatus())) {
            throw new BusinessException("陪伴已结束，无法打卡");
        }

        LocalDate today = LocalDate.now();
        assertCanCheckinOnDate(relationId, userId, plan.getCheckinFrequency(), today);

        CompanionCheckin checkin = new CompanionCheckin();
        checkin.setRelationId(relationId);
        checkin.setUserId(userId);
        checkin.setCheckinDate(today);
        checkin.setNote(note != null && !note.isBlank() ? note.trim() : null);
        checkinMapper.insert(checkin);

        int total = relation.getDailyCheckinCount() != null ? relation.getDailyCheckinCount() : 0;
        relation.setDailyCheckinCount(total + 1);
        relationMapper.updateById(relation);

        Long partnerId = getPartnerId(relation, userId);
        CheckinResultVO vo = new CheckinResultVO();
        vo.setCheckinDate(today);
        vo.setMyStreak(calculateStreak(relationId, userId, plan.getCheckinFrequency()));
        vo.setMyTotalCheckins(countUserCheckins(relationId, userId));
        vo.setRelationTotalCheckins(relation.getDailyCheckinCount());
        vo.setPartnerCheckedInToday(hasCheckinOnDate(relationId, partnerId, today));
        vo.setDaysRemaining(computeDaysRemaining(relation, plan));
        return vo;
    }

    @Override
    public CheckinStatusVO getCheckinStatus(Long userId, Long relationId) {
        CompanionRelation relation = getRelationForParticipant(relationId, userId);
        CompanionPlan plan = planMapper.selectById(relation.getPlanId());
        if (plan == null) {
            throw new BusinessException("陪伴计划不存在");
        }

        completeIfExpired(relation, plan);

        Long partnerId = getPartnerId(relation, userId);
        LocalDate today = LocalDate.now();

        CheckinStatusVO vo = new CheckinStatusVO();
        vo.setRelationId(relationId);
        vo.setDurationDays(plan.getDurationDays());
        vo.setDaysRemaining(computeDaysRemaining(relation, plan));
        vo.setDaysElapsed(computeDaysElapsed(relation));
        vo.setStartedAt(relation.getStartedAt());
        vo.setExpectedEndAt(computeExpectedEndAt(relation, plan));
        vo.setCheckinFrequency(plan.getCheckinFrequency());
        vo.setCheckedInToday(hasCheckinOnDate(relationId, userId, today));
        vo.setPartnerCheckedInToday(hasCheckinOnDate(relationId, partnerId, today));
        vo.setMyStreak(calculateStreak(relationId, userId, plan.getCheckinFrequency()));
        vo.setMyTotalCheckins(countUserCheckins(relationId, userId));
        vo.setPartnerTotalCheckins(countUserCheckins(relationId, partnerId));
        vo.setMyCheckinDates(listCheckinDates(relationId, userId));
        vo.setPartnerCheckinDates(listCheckinDates(relationId, partnerId));
        return vo;
    }

    private CompanionRelation getRelationForParticipant(Long relationId, Long userId) {
        CompanionRelation relation = relationMapper.selectById(relationId);
        if (relation == null) {
            throw new BusinessException("陪伴关系不存在");
        }
        if (!userId.equals(relation.getInviterId()) && !userId.equals(relation.getInviteeId())) {
            throw new BusinessException(403, "无权访问该陪伴关系");
        }
        return relation;
    }

    private Long getPartnerId(CompanionRelation relation, Long userId) {
        return userId.equals(relation.getInviterId()) ? relation.getInviteeId() : relation.getInviterId();
    }

    private void completeIfExpired(CompanionRelation relation, CompanionPlan plan) {
        if (!"ACCEPTED".equals(relation.getStatus()) || relation.getStartedAt() == null) {
            return;
        }
        if (computeDaysRemaining(relation, plan) > 0) {
            return;
        }

        relation.setStatus("COMPLETED");
        relation.setEndedAt(LocalDateTime.now());
        relationMapper.updateById(relation);

        if ("IN_PROGRESS".equals(plan.getStatus())) {
            plan.setStatus("COMPLETED");
            planMapper.updateById(plan);
        }
    }

    private int computeDaysRemaining(CompanionRelation relation, CompanionPlan plan) {
        int duration = plan.getDurationDays() != null ? plan.getDurationDays() : 21;
        if (relation.getStartedAt() == null) {
            return duration;
        }
        long elapsed = ChronoUnit.DAYS.between(relation.getStartedAt().toLocalDate(), LocalDate.now());
        return (int) Math.max(0, duration - elapsed);
    }

    private int computeDaysElapsed(CompanionRelation relation) {
        if (relation.getStartedAt() == null) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(relation.getStartedAt().toLocalDate(), LocalDate.now()) + 1;
    }

    private LocalDateTime computeExpectedEndAt(CompanionRelation relation, CompanionPlan plan) {
        if (relation.getStartedAt() == null) {
            return null;
        }
        int duration = plan.getDurationDays() != null ? plan.getDurationDays() : 21;
        return relation.getStartedAt().plusDays(duration);
    }

    private void assertCanCheckinOnDate(Long relationId, Long userId, String frequency, LocalDate date) {
        if ("WEEKLY".equals(frequency)) {
            LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate weekEnd = weekStart.plusDays(6);
            LambdaQueryWrapper<CompanionCheckin> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CompanionCheckin::getRelationId, relationId)
                    .eq(CompanionCheckin::getUserId, userId)
                    .between(CompanionCheckin::getCheckinDate, weekStart, weekEnd);
            if (checkinMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("本周已打卡");
            }
            return;
        }

        if (hasCheckinOnDate(relationId, userId, date)) {
            throw new BusinessException("今日已打卡");
        }
    }

    private boolean hasCheckinOnDate(Long relationId, Long userId, LocalDate date) {
        LambdaQueryWrapper<CompanionCheckin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CompanionCheckin::getRelationId, relationId)
                .eq(CompanionCheckin::getUserId, userId)
                .eq(CompanionCheckin::getCheckinDate, date);
        return checkinMapper.selectCount(wrapper) > 0;
    }

    private int countUserCheckins(Long relationId, Long userId) {
        LambdaQueryWrapper<CompanionCheckin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CompanionCheckin::getRelationId, relationId)
                .eq(CompanionCheckin::getUserId, userId);
        return checkinMapper.selectCount(wrapper).intValue();
    }

    private List<LocalDate> listCheckinDates(Long relationId, Long userId) {
        LambdaQueryWrapper<CompanionCheckin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CompanionCheckin::getRelationId, relationId)
                .eq(CompanionCheckin::getUserId, userId)
                .orderByAsc(CompanionCheckin::getCheckinDate);
        return checkinMapper.selectList(wrapper).stream()
                .map(CompanionCheckin::getCheckinDate)
                .collect(Collectors.toList());
    }

    private int calculateStreak(Long relationId, Long userId, String frequency) {
        List<LocalDate> dates = listCheckinDates(relationId, userId);
        if (dates.isEmpty()) {
            return 0;
        }

        if ("WEEKLY".equals(frequency)) {
            Set<LocalDate> weekStarts = dates.stream()
                    .map(d -> d.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))
                    .collect(Collectors.toSet());
            LocalDate week = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            if (!weekStarts.contains(week)) {
                week = week.minusWeeks(1);
            }
            int streak = 0;
            while (weekStarts.contains(week)) {
                streak++;
                week = week.minusWeeks(1);
            }
            return streak;
        }

        Set<LocalDate> dateSet = new HashSet<>(dates);
        LocalDate day = LocalDate.now();
        if (!dateSet.contains(day)) {
            day = day.minusDays(1);
        }

        int streak = 0;
        while (dateSet.contains(day)) {
            streak++;
            day = day.minusDays(1);
        }
        return streak;
    }

    private Set<Long> findCheckedInTodayRelationIds(Long userId, List<Long> relationIds) {
        if (relationIds.isEmpty()) {
            return Set.of();
        }
        LambdaQueryWrapper<CompanionCheckin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CompanionCheckin::getUserId, userId)
                .eq(CompanionCheckin::getCheckinDate, LocalDate.now())
                .in(CompanionCheckin::getRelationId, relationIds);
        return checkinMapper.selectList(wrapper).stream()
                .map(CompanionCheckin::getRelationId)
                .collect(Collectors.toSet());
    }

    private Set<Long> getExcludedUserIds(Long planId, Long creatorId) {
        Set<Long> excluded = new HashSet<>();
        excluded.add(creatorId);
        LambdaQueryWrapper<CompanionRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CompanionRelation::getPlanId, planId)
                .in(CompanionRelation::getStatus, "PENDING", "ACCEPTED");
        for (CompanionRelation relation : relationMapper.selectList(wrapper)) {
            excluded.add(relation.getInviteeId());
        }
        return excluded;
    }

    private double styleMatch(String preferred, String userStyle) {
        if (preferred == null || "ANY".equals(preferred)) {
            return 1.0;
        }
        if (userStyle == null || userStyle.isBlank() || "ANY".equals(userStyle)) {
            return 0.8;
        }
        return preferred.equals(userStyle) ? 1.0 : 0.3;
    }

    private List<CompanionRelationVO> toRelationVOList(List<CompanionRelation> relations, Long currentUserId) {
        if (relations.isEmpty()) {
            return List.of();
        }

        Set<Long> userIds = new HashSet<>();
        Set<Long> planIds = new HashSet<>();
        for (CompanionRelation relation : relations) {
            userIds.add(relation.getInviterId());
            userIds.add(relation.getInviteeId());
            planIds.add(relation.getPlanId());
        }

        Map<Long, String> nicknames = userService.getNicknameMap(userIds);
        Map<Long, CompanionPlan> planMap = planMapper.selectBatchIds(planIds).stream()
                .collect(Collectors.toMap(CompanionPlan::getId, p -> p));

        List<Long> acceptedRelationIds = relations.stream()
                .filter(r -> "ACCEPTED".equals(r.getStatus()))
                .map(CompanionRelation::getId)
                .collect(Collectors.toList());
        Set<Long> checkedInTodayIds = findCheckedInTodayRelationIds(currentUserId, acceptedRelationIds);

        return relations.stream()
                .map(relation -> {
                    CompanionPlan plan = planMap.get(relation.getPlanId());
                    String planTitle = plan != null ? plan.getTitle() : "未知计划";
                    Integer durationDays = plan != null ? plan.getDurationDays() : null;
                    Integer daysRemaining = plan != null ? computeDaysRemaining(relation, plan) : null;
                    Boolean checkedInToday = checkedInTodayIds.contains(relation.getId());
                    return CompanionRelationVO.from(
                            relation,
                            planTitle,
                            durationDays,
                            daysRemaining,
                            checkedInToday,
                            nicknames.getOrDefault(relation.getInviterId(), "用户" + relation.getInviterId()),
                            nicknames.getOrDefault(relation.getInviteeId(), "用户" + relation.getInviteeId()),
                            currentUserId);
                })
                .collect(Collectors.toList());
    }
}
