package com.tondo.module.governance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tondo.module.governance.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}
