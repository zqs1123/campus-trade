package org.zqs.userservice.service;

import org.zqs.userservice.dto.LoginDTO;
import org.zqs.userservice.dto.RegisterDTO;
import org.zqs.userservice.dto.UserVO;

public interface UserService {
    UserVO register(RegisterDTO dto);
    String login(LoginDTO dto);
    UserVO getUserById(Integer id);
}