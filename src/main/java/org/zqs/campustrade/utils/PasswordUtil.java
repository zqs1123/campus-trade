package org.zqs.campustrade.utils;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();


    //加密密码，存库时调用
    public static String encode(String rawPassword){
        return ENCODER.encode(rawPassword);
    }

    //校验密码，登录时调用
    public static boolean matches(String rawPassword, String encodePassword){
        return ENCODER.matches(rawPassword, encodePassword);
    }
}
