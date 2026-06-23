package com.tondo.module.companion.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InviteDTO {
    @NotNull(message = "计划ID不能为空")
    private Long planId;

    @NotNull(message = "被邀请人ID不能为空")
    private Long inviteeId;
}