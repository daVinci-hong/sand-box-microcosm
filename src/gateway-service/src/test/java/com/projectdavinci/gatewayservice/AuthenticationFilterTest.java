package com.projectdavinci.gatewayservice;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {

    @Mock
    private RouteValidator routeValidator;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private GatewayFilterChain filterChain;
    @Mock
    private Claims claims;

    private AuthenticationFilter authenticationFilter;

    @BeforeEach
    void setUp() {
        // 核心修正：我們親手，將“間諜”，通過構造函數，注入到一個全新的“作戰單位”之中。
        authenticationFilter = new AuthenticationFilter(routeValidator, jwtUtil);
    }

    @Test
    void whenTokenIsValid_thenFilterProceeds() {
        // 1. 準備 (Arrange)
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/beacon/some-secured-endpoint")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // 為所有需要被調用的方法，提供精確的劇本
        when(routeValidator.isSecured(request)).thenReturn(true);
        when(jwtUtil.validateToken("valid-token")).thenReturn(true);
        when(jwtUtil.getAllClaimsFromToken("valid-token")).thenReturn(claims);
        when(claims.getSubject()).thenReturn("test-user");
        when(claims.get("roles", List.class)).thenReturn(List.of("USER"));
        // 只在此測試中，為 filterChain 提供劇本
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        // 2. 行動 (Act)
        Mono<Void> result = authenticationFilter.apply(new AuthenticationFilter.Config()).filter(exchange, filterChain);

        // 3. 斷言 (Assert)
        StepVerifier.create(result).verifyComplete();
        verify(filterChain, times(1)).filter(any(ServerWebExchange.class));
    }

    @Test
    void whenTokenIsMissing_thenRequestIsRejected() {
        // 1. 準備
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/beacon/some-secured-endpoint").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(routeValidator.isSecured(request)).thenReturn(true);

        // 2. 行動
        Mono<Void> result = authenticationFilter.apply(new AuthenticationFilter.Config()).filter(exchange, filterChain);

        // 3. 斷言：精確地，驗證我們期望收到的“錯誤信號”
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof ResponseStatusException
                        && ((ResponseStatusException) throwable).getStatusCode() == HttpStatus.UNAUTHORIZED)
                .verify();

        verify(filterChain, never()).filter(any(ServerWebExchange.class));
    }

    @Test
    void whenTokenIsInvalid_thenRequestIsRejected() {
        // 1. 準備
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/beacon/some-secured-endpoint")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(routeValidator.isSecured(request)).thenReturn(true);
        when(jwtUtil.validateToken("invalid-token")).thenReturn(false);

        // 2. 行動
        Mono<Void> result = authenticationFilter.apply(new AuthenticationFilter.Config()).filter(exchange, filterChain);

        // 3. 斷言
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof ResponseStatusException
                        && ((ResponseStatusException) throwable).getStatusCode() == HttpStatus.UNAUTHORIZED)
                .verify();

        verify(filterChain, never()).filter(any(ServerWebExchange.class));
    }

    @Test
    void whenRouteIsUnsecured_thenFilterIsSkipped() {
        // 1. 準備
        MockServerHttpRequest request = MockServerHttpRequest.get("/auth/login").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(routeValidator.isSecured(request)).thenReturn(false);
        // 只在此測試中，為 filterChain 提供劇本
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        // 2. 行動
        Mono<Void> result = authenticationFilter.apply(new AuthenticationFilter.Config()).filter(exchange, filterChain);

        // 3. 斷言
        StepVerifier.create(result).verifyComplete();
        verify(filterChain, times(1)).filter(any(ServerWebExchange.class));
        verify(jwtUtil, never()).validateToken(anyString());
    }
}