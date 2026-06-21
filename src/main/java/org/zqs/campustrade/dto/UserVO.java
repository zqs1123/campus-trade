package org.zqs.campustrade.dto;


import lombok.Data;

@Data
public class UserVO {
    private Integer id;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String token;
}
