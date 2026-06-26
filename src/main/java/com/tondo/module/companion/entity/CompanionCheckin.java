package com.tondo.module.companion.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("companion_checkin")
public class CompanionCheckin {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long relationId;
    private Long userId;
    private LocalDate checkinDate;
    private String note;
    private LocalDateTime createdAt;
}
