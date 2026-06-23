package com.tondo.module.card.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCardDTO {
    @Size(max = 100, message = "标题不超过100字")
    private String title;

    @NotBlank(message = "事件描述不能为空")
    @Size(min = 30, message = "事件描述至少30字")
    private String eventDescription;

    @NotBlank(message = "情绪标签不能为空")
    private String emotionTags;       // 前端传 JSON 字符串："[\"焦虑\",\"迷茫\"]"

    @NotBlank(message = "尝试描述不能为空")
    @Size(min = 20, message = "请至少写20字描述你尝试过的方法")
    private String attemptDescription;

    @NotBlank(message = "请选择你需要什么")
    private String needType;          // EMPATHY / ADVICE / COMPANION

    @NotBlank(message = "困惑领域不能为空")
    private String confusionTags;     // 前端传 JSON 字符串
}