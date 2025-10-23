package com.projectdavinci.gatewayservice;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import org.slf4j.Logger; // 【【【 新增：日誌記錄器 】】】
import org.slf4j.LoggerFactory; // 【【【 新增：日誌記錄器 】】】
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class BulkheadGatewayFilterFactory extends AbstractGatewayFilterFactory<BulkheadGatewayFilterFactory.Config> {

    private static final Logger log = LoggerFactory.getLogger(BulkheadGatewayFilterFactory.class); // 【【【 新增：日誌實例 】】】

    private final BulkheadRegistry bulkheadRegistry;

    public BulkheadGatewayFilterFactory(BulkheadRegistry bulkheadRegistry) {
        super(Config.class);
        this.bulkheadRegistry = bulkheadRegistry;
    }

    @Override
    public GatewayFilter apply(Config config) {
        Bulkhead bulkhead = bulkheadRegistry.bulkhead(config.getName());
        return (exchange, chain) -> {
            if (bulkhead.tryAcquirePermission()) {
                return chain.filter(exchange)
                        .doOnSuccess(s -> bulkhead.onComplete())
                        .doOnError(t -> bulkhead.onComplete());
            } else {
                // =================================================================
                // == 【【【 唯一的、核心的修正：明確記錄拒絕事件 】】】 ==
                // =================================================================
                log.warn("Bulkhead '{}' rejected call due to max concurrent calls reached.", config.getName()); // 【【【 新增：明確日誌 】】】
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