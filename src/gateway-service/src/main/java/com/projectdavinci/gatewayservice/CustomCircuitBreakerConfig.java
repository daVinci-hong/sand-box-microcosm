package com.projectdavinci.gatewayservice;

import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CustomCircuitBreakerConfig {

    private static final Logger log = LoggerFactory.getLogger(CustomCircuitBreakerConfig.class);

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> {
            // =================================================================
            // == 【【【 唯一的、核心的修正：光復“情報廣播” 】】】 ==
            // =================================================================
            // 我們，將，重新，為我們的“熔斷器”，安裝“狀態變更竊聽器”。
            factory.addCircuitBreakerCustomizer(circuitBreaker -> {
                circuitBreaker.getEventPublisher()
                        .onStateTransition(event -> {
                            log.warn("!!! CircuitBreaker '{}' state changed from {} to {} !!!",
                                    event.getCircuitBreakerName(),
                                    event.getStateTransition().getFromState(),
                                    event.getStateTransition().getToState()
                            );
                        });
            }, "beacon-service-cb");

            factory.configureDefault(id -> {
                TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(3))
                        .build();

                return new Resilience4JConfigBuilder(id)
                        .timeLimiterConfig(timeLimiterConfig)
                        .build();
            });
        };
    }
}