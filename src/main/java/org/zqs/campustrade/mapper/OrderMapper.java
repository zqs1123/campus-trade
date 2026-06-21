package org.zqs.campustrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.zqs.campustrade.entity.Order;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}