package org.zqs.gateway.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static final String SECRET = "campus_trade_secret_key_2024";

    private static final List<String> WHITE_LIST = Arrays.asList(
            "/api/user/register",
            "/api/user/login",
            "/api/product/list",
            "/api/category/list"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 白名单放行
        if (isWhiteList(path)) {
            return chain.filter(exchange);
        }

        // 获取 token
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("未登录");
        }

        String token = authHeader.substring(7);
        try {
            Integer userId = JWT.require(Algorithm.HMAC256(SECRET))
                    .build()
                    .verify(token)
                    .getClaim("userId")
                    .asInt();

            // 将 userId 放入请求头，传递给下游服务
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .build();
            return chain.filter(exchange.mutate().request(request).build());
        } catch (Exception e) {
            throw new RuntimeException("Token 无效");
        }
    }

    private boolean isWhiteList(String path) {
        for (String pattern : WHITE_LIST) {
            if (path.startsWith(pattern)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return -100;
    }
}