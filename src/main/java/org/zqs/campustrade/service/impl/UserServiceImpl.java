package org.zqs.campustrade.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zqs.campustrade.dto.LoginDTO;
import org.zqs.campustrade.dto.RegisterDTO;
import org.zqs.campustrade.dto.UserVO;
import org.zqs.campustrade.entity.User;
import org.zqs.campustrade.mapper.UserMapper;
import org.zqs.campustrade.service.UserService;
import org.zqs.campustrade.utils.JwtUtil;
import org.zqs.campustrade.utils.PasswordUtil;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;


    @Override
    public UserVO register(RegisterDTO dto) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", dto.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("用户已存在");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(PasswordUtil.encode(dto.getPassword()));
        user.setNickname(dto.getNickname() == null ? dto.getUsername() : dto.getNickname());

        user.setStatus(1);
        userMapper.insert(user);

        String token = JwtUtil.generateToken(user.getId(), user.getUsername());

        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setToken(token);
        return vo;
    }

    @Override
    public UserVO login(LoginDTO dto) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", dto.getUsername());
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }


        if (!PasswordUtil.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }


        String token = JwtUtil.generateToken(user.getId(), user.getUsername());


        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setToken(token);
        return vo;
    }




}

