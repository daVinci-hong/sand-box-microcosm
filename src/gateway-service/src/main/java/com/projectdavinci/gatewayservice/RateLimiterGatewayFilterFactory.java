package com.projectdavinci.gatewayservice;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.slf4j.Logger; // 【【【 新增：情報官的接口 】】】
import org.slf4j.LoggerFactory; // 【【【 新增：情報官的工廠 】】】
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RateLimiterGatewayFilterFactory extends AbstractGatewayFilterFactory<RateLimiterGatewayFilterFactory.Config> {

    // 【【【 新增：任命“戰地記錄官” 】】】
    private static final Logger log = LoggerFactory.getLogger(RateLimiterGatewayFilterFactory.class);

    private final RateLimiterRegistry rateLimiterRegistry;

    public RateLimiterGatewayFilterFactory(RateLimiterRegistry rateLimiterRegistry) {
        super(Config.class);
        this.rateLimiterRegistry = rateLimiterRegistry;
    }

    @Override
    public GatewayFilter apply(Config config) {
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter(config.getName());
        return (exchange, chain) -> {
            if (rateLimiter.acquirePermission()) {
                return chain.filter(exchange);
            } else {
                // =================================================================
                // == 【【【 唯一的、核心的修正：記錄“拒絕”事件 】】】 ==
                // =================================================================
                log.warn("RateLimiter '{}' does not permit further calls.", config.getName()); // 【【【 新增：記錄戰報 】】】
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                return exchange.getResponse().setComplete();
            }
        };
    }

    public static class Config {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}