package com.tondo.module.governance.service;

import com.tondo.common.response.PageResult;
import com.tondo.module.governance.entity.vo.OperationLogVO;

public interface OperationLogService {

    void record(Long operatorId, String action, String targetType, Long targetId, String detail, String ip);

    PageResult<OperationLogVO> listLogs(int page, int size);
}
