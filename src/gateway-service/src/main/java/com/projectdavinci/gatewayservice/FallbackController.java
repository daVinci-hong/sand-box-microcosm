package com.projectdavinci.gatewayservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

// 我們在此告知 IDE 的「巡檢員」，我們知道這個類在 Java 代碼中未被直接使用，
// 但它會被 Spring 框架在運行時動態調用。請忽略此警告。
@SuppressWarnings("unused")
@RestController
public class FallbackController {

    @GetMapping("/fallback/beacon")
    public Mono<String> beaconServiceFallback() {
        return Mono.just("Beacon Service is currently unavailable. Please try again later.");
    }
}