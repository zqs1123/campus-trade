package org.zqs.userservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zqs.common.entity.Result;
import org.zqs.userservice.dto.LoginDTO;
import org.zqs.userservice.dto.RegisterDTO;
import org.zqs.userservice.dto.UserVO;
import org.zqs.userservice.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody RegisterDTO dto) {
        UserVO vo = userService.register(dto);
        return Result.success(vo);
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO dto) {
        String token = userService.login(dto);
        return Result.success(token);
    }

    @GetMapping("/me")
    public Result<UserVO> getCurrentUser(@RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null) {
            return Result.error("未登录");
        }
        UserVO vo = userService.getUserById(userId);
        return Result.success(vo);
    }

    @GetMapping("/{id}")
    public Result<UserVO> getUserById(@PathVariable Integer id) {
        UserVO vo = userService.getUserById(id);
        return Result.success(vo);
    }
}