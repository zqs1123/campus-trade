package org.zqs.campustrade.service;

import org.zqs.campustrade.dto.LoginDTO;
import org.zqs.campustrade.dto.RegisterDTO;
import org.zqs.campustrade.dto.UserVO;

public interface UserService {
    UserVO register(RegisterDTO dto);
    UserVO login(LoginDTO dto);

}
