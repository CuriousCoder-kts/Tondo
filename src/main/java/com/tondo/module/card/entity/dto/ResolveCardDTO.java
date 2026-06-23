package com.tondo.module.card.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResolveCardDTO {
    @NotBlank(message = "请填写你的复盘内容")
    @Size(min = 20, message = "复盘内容至少20字")
    private String resolutionContent;
}