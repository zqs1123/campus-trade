package org.zqs.userservice.dto;

import lombok.Data;

@Data
public class UserVO {
    private Integer id;
    private String username;
    private String nickname;
    private String avatar;
    private String phone;
}