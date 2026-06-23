package com.tondo.module.message.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("private_message")
public class PrivateMessage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long relationId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private String contentType;   // TEXT / IMAGE
    private Integer isRead;
    private Integer isRecalled;
    private LocalDateTime createdAt;
}