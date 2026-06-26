package com.tondo.module.governance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("report_record")
public class ReportRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long reporterId;
    private String targetType;
    private Long targetId;
    private String reason;
    private String description;
    private String status;
    private Long handlerId;
    private String handleResult;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private LocalDateTime createdAt;
}
