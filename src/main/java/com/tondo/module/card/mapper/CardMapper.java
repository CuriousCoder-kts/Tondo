package com.tondo.module.card.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tondo.module.card.entity.Card;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CardMapper extends BaseMapper<Card> {
}