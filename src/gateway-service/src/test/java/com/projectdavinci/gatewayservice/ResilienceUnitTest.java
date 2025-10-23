package com.projectdavinci.gatewayservice;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class ResilienceUnitTest {

    @Test
    void circuitBreakerLogicTest() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(4)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .build();
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        CircuitBreaker circuitBreaker = registry.circuitBreaker("test-breaker");

        circuitBreaker.onSuccess(0, TimeUnit.NANOSECONDS);
        circuitBreaker.onSuccess(0, TimeUnit.NANOSECONDS);
        circuitBreaker.onError(0, TimeUnit.NANOSECONDS, new RuntimeException());
        circuitBreaker.onError(0, TimeUnit.NANOSECONDS, new RuntimeException());

        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    // =================================================================
    // == 【【【 唯一的、核心的修正：補完演習劇本 】】】 ==
    // =================================================================
    @Test
    void rateLimiterLogicTest() {
        // 1. 鑄造“韌性裝甲”
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(2) // 每個週期，只允許 2 次請求
                .limitRefreshPeriod(Duration.ofSeconds(10))
                .timeoutDuration(Duration.ZERO)
                .build();
        RateLimiterRegistry registry = RateLimiterRegistry.of(config);
        RateLimiter rateLimiter = registry.rateLimiter("test-limiter");

        // 2. 演習
        boolean firstAttempt = rateLimiter.acquirePermission();
        boolean secondAttempt = rateLimiter.acquirePermission();
        boolean thirdAttempt = rateLimiter.acquirePermission();

        // 3. 斷言
        assertThat(firstAttempt).isTrue();
        assertThat(secondAttempt).isTrue();
        assertThat(thirdAttempt).isFalse(); // 第三次，必須失敗
    }

    @Test
    void bulkheadLogicTest() {
        // 1. 鑄造“韌性裝甲”
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(2) // 最大併發調用數為 2
                .maxWaitDuration(Duration.ZERO)
                .build();
        BulkheadRegistry registry = BulkheadRegistry.of(config);
        Bulkhead bulkhead = registry.bulkhead("test-bulkhead");

        // 2. 演習
        boolean firstAttempt = bulkhead.tryAcquirePermission();
        boolean secondAttempt = bulkhead.tryAcquirePermission();
        boolean thirdAttempt = bulkhead.tryAcquirePermission();

        // 3. 斷言
        assertThat(firstAttempt).isTrue();
        assertThat(secondAttempt).isTrue();
        assertThat(thirdAttempt).isFalse(); // 第三次併發，必須失敗

        // 4. 演習（釋放）
        bulkhead.onComplete(); // 模擬一次調用完成
        boolean fourthAttempt = bulkhead.tryAcquirePermission();

        // 5. 斷言
        assertThat(fourthAttempt).isTrue(); // 在釋放一個許可後，第四次，必須成功
    }
}