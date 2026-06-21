package org.zqs.campustrade.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zqs.campustrade.dto.LoginDTO;
import org.zqs.campustrade.dto.RegisterDTO;
import org.zqs.campustrade.dto.UserVO;
import org.zqs.campustrade.service.UserService;

@RestController
@RequestMapping("/api/user")
public class Usercontroller {

    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public UserVO register(@RequestBody RegisterDTO dto) {
        return userService.register(dto);
    }

    @PostMapping("/login")
    public UserVO login(@RequestBody LoginDTO dto) {
        return userService.login(dto);
    }
}
