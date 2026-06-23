package org.zqs.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.zqs.userservice.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}