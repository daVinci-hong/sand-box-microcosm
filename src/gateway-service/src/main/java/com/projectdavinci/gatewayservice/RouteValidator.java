package com.projectdavinci.gatewayservice;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/auth/register",
            "/auth/login",
            "/eureka"
    );

    // =================================================================
    // == 【【【 唯一的、核心的修正 (1/3) 】】】 ==
    // =================================================================
    // 將 isSecured 從一個 public Predicate 屬性，重構為一個 public boolean 方法。
    // 這，是所有可測試性的根基。
    public boolean isSecured(ServerHttpRequest request) {
        return openApiEndpoints
                .stream()
                .noneMatch(uri -> request.getURI().getPath().contains(uri));
    }
}