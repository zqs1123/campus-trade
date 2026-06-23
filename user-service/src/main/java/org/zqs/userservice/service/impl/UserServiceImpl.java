package org.zqs.userservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zqs.common.exception.BusinessException;
import org.zqs.common.utils.JwtUtil;
import org.zqs.common.utils.PasswordUtil;
import org.zqs.userservice.dto.LoginDTO;
import org.zqs.userservice.dto.RegisterDTO;
import org.zqs.userservice.dto.UserVO;
import org.zqs.userservice.entity.User;
import org.zqs.userservice.mapper.UserMapper;
import org.zqs.userservice.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserVO register(RegisterDTO dto) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", dto.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(PasswordUtil.encode(dto.getPassword()));
        user.setNickname(dto.getNickname() == null ? dto.getUsername() : dto.getNickname());
        userMapper.insert(user);

        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    @Override
    public String login(LoginDTO dto) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", dto.getUsername());
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!PasswordUtil.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        return JwtUtil.generateToken(user.getId(), user.getUsername());
    }

    @Override
    public UserVO getUserById(Integer id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}