package org.zqs.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;

public class JwtUtil {
    private static final String SECRET = "campus_trade_secret_key_2024";
    private static final long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000;

    public static String generateToken(Integer userId, String username) {
        return JWT.create()
                .withClaim("userId", userId)
                .withClaim("username", username)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .sign(Algorithm.HMAC256(SECRET));
    }

    public static Integer getUserId(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET))
                .build()
                .verify(token)
                .getClaim("userId")
                .asInt();
    }

    public static String getUsername(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET))
                .build()
                .verify(token)
                .getClaim("username")
                .asString();
    }
}