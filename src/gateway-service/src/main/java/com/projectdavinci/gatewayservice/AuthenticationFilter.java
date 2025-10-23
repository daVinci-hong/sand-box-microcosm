package com.projectdavinci.gatewayservice;

import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    // 核心修正：移除 @Autowired，聲明為 final，以確保依賴的絕對可靠性
    private final RouteValidator routeValidator;
    private final JwtUtil jwtUtil;

    // 核心修正：使用構造函數注入，這是所有可測試性的根基
    public AuthenticationFilter(RouteValidator routeValidator, JwtUtil jwtUtil) {
        super(Config.class);
        this.routeValidator = routeValidator;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 1. 豁免權檢查：首先，檢查此路由是否為一個無需認證的公開路由。
            if (!routeValidator.isSecured(request)) {
                // 如果是，則立即放行，跳過所有後續的 JWT 驗證。
                return chain.filter(exchange);
            }

            // 2. 檢查 Authorization 頭是否存在
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return this.onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            String token = null;

            // 3. 驗證是否為 Bearer 方案
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            } else {
                return this.onError(exchange, "Invalid Authorization header format", HttpStatus.UNAUTHORIZED);
            }

            // 4. 驗證令牌的有效性
            if (!jwtUtil.validateToken(token)) {
                return this.onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
            }

            // 5. 驗證成功，向下游傳遞身份信息
            Claims claims = jwtUtil.getAllClaimsFromToken(token);
            String subject = claims.getSubject();
            List<String> roles = claims.get("roles", List.class);

            ServerHttpRequest newRequest = request.mutate()
                    .header("X-User-Subject", subject)
                    .header("X-User-Roles", String.join(",", roles))
                    .build();

            return chain.filter(exchange.mutate().request(newRequest).build());
        };
    }

    // 核心修正：不再依賴 exchange.getResponse()，而是直接在響應式流中，廣播一個標準的錯誤信號。
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        return Mono.error(new ResponseStatusException(httpStatus, err));
    }

    public static class Config {
        // 可在此處添加配置屬性
    }
}