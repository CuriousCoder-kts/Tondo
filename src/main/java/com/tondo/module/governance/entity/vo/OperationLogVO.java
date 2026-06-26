package com.tondo.module.governance.entity.vo;

import com.tondo.module.governance.entity.OperationLog;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationLogVO {
    private Long id;
    private Long operatorId;
    private String action;
    private String targetType;
    private Long targetId;
    private String detail;
    private String ip;
    private LocalDateTime createdAt;

    public static OperationLogVO from(OperationLog log) {
        OperationLogVO vo = new OperationLogVO();
        vo.setId(log.getId());
        vo.setOperatorId(log.getOperatorId());
        vo.setAction(log.getAction());
        vo.setTargetType(log.getTargetType());
        vo.setTargetId(log.getTargetId());
        vo.setDetail(log.getDetail());
        vo.setIp(log.getIp());
        vo.setCreatedAt(log.getCreatedAt());
        return vo;
    }
}
