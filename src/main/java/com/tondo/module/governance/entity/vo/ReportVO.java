package com.tondo.module.governance.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportVO {
    private Long id;
    private Long reporterId;
    private String reporterNickname;
    private String targetType;
    private Long targetId;
    private String targetSummary;
    private String reason;
    private String description;
    private String status;
    private Long handlerId;
    private String handleResult;
    private LocalDateTime createdAt;
}
