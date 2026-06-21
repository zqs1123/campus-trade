package org.zqs.campustrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.zqs.campustrade.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
