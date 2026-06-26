package com.tondo.module.governance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tondo.common.response.PageResult;
import com.tondo.module.governance.entity.OperationLog;
import com.tondo.module.governance.entity.vo.OperationLogVO;
import com.tondo.module.governance.mapper.OperationLogMapper;
import com.tondo.module.governance.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;

    @Override
    public void record(Long operatorId, String action, String targetType, Long targetId, String detail, String ip) {
        OperationLog log = new OperationLog();
        log.setOperatorId(operatorId);
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetail(detail);
        log.setIp(ip);
        log.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(log);
    }

    @Override
    public PageResult<OperationLogVO> listLogs(int page, int size) {
        Page<OperationLog> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(OperationLog::getCreatedAt);
        Page<OperationLog> result = operationLogMapper.selectPage(pageParam, wrapper);
        List<OperationLogVO> items = result.getRecords().stream().map(OperationLogVO::from).toList();
        return PageResult.of(result.getCurrent(), result.getSize(), result.getTotal(), items);
    }
}
