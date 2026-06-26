package com.tondo.module.governance.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HandleReportDTO {
    @NotBlank(message = "处理动作不能为空")
    private String action;

    private String handleResult;
}
