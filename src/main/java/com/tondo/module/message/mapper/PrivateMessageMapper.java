package com.tondo.module.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tondo.module.message.entity.PrivateMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PrivateMessageMapper extends BaseMapper<PrivateMessage> {
}