package com.tondo.module.companion.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePlanDTO {
    @NotBlank(message = "计划标题不能为空")
    @Size(max = 100)
    private String title;

    @NotBlank(message = "目标描述不能为空")
    @Size(min = 20, message = "至少20字描述你的目标")
    private String goalDescription;

    private String confusionTags = "[]";

    private Integer durationDays = 21;

    @NotBlank(message = "请选择打卡频率")
    private String checkinFrequency;  // DAILY / WEEKLY

    private String companionStylePreferred = "ANY";
}