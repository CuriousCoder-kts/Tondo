package com.tondo.module.companion.service.impl;

import com.tondo.common.exception.BusinessException;
import com.tondo.module.companion.entity.CompanionPlan;
import com.tondo.module.companion.entity.CompanionRelation;
import com.tondo.module.companion.entity.dto.InviteDTO;
import com.tondo.module.companion.mapper.CompanionCheckinMapper;
import com.tondo.module.companion.mapper.CompanionPlanMapper;
import com.tondo.module.companion.mapper.CompanionRelationMapper;
import com.tondo.module.notification.service.NotificationService;
import com.tondo.module.user.mapper.UserMapper;
import com.tondo.module.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanionServiceImplTest {

    @Mock
    private CompanionPlanMapper planMapper;
    @Mock
    private CompanionRelationMapper relationMapper;
    @Mock
    private CompanionCheckinMapper checkinMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserService userService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CompanionServiceImpl companionService;

    @Test
    void invite_rejectsWhenPlanAlreadyHasCompanion() {
        Long inviterId = 1L;
        InviteDTO dto = new InviteDTO();
        dto.setPlanId(10L);
        dto.setInviteeId(2L);

        CompanionPlan plan = new CompanionPlan();
        plan.setId(10L);
        plan.setCreatorId(inviterId);
        plan.setStatus("SEEKING");
        plan.setTitle("测试计划");

        when(planMapper.selectById(10L)).thenReturn(plan);
        when(relationMapper.selectCount(any())).thenReturn(1L);

        assertThrows(BusinessException.class, () -> companionService.invite(inviterId, dto));
    }

    @Test
    void respond_rejectsSecondAcceptForSamePlan() {
        Long inviteeId = 2L;
        Long relationId = 100L;

        CompanionRelation relation = new CompanionRelation();
        relation.setId(relationId);
        relation.setPlanId(10L);
        relation.setInviterId(1L);
        relation.setInviteeId(inviteeId);
        relation.setStatus("PENDING");

        when(relationMapper.selectById(relationId)).thenReturn(relation);
        when(relationMapper.selectCount(any())).thenReturn(1L);

        assertThrows(BusinessException.class,
                () -> companionService.respondToInvitation(inviteeId, relationId, true));
    }
}
