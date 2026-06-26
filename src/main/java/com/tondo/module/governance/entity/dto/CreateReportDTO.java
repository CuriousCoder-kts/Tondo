package com.tondo.module.governance.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReportDTO {
    @NotBlank(message = "目标类型不能为空")
    private String targetType;

    @NotNull(message = "目标ID不能为空")
    private Long targetId;

    @NotBlank(message = "请选择举报原因")
    private String reason;

    private String description;
}
