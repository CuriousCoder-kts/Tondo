package com.tondo.module.card.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateReplyDTO {
    @NotBlank(message = "请描述你当时的情况")
    @Size(min = 10, message = "至少10字")
    private String experienceSituation;

    @NotBlank(message = "请描述你的行动")
    @Size(min = 10, message = "至少10字")
    private String experienceAction;

    @NotBlank(message = "请描述结果与反思")
    @Size(min = 10, message = "至少10字")
    private String experienceResult;

    private String replyType = "EXPERIENCE"; // EXPERIENCE / SUPPORT
}