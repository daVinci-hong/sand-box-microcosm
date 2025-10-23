package com.projectdavinci.beaconservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class DisasterController {

    @GetMapping("/disaster")
    public ResponseEntity<String> triggerDisaster() throws InterruptedException {
        // =================================================================
        // == 【【【 唯一的、核心的修正：模擬“網絡層的沉默” 】】】 ==
        // =================================================================
        // 我們，將不再，返回“503”。
        // 我們，將，沉默 8 秒。
        // 這，必然，會，觸發，我們在 gateway-service 中，所定義的 6 秒超時。
        TimeUnit.SECONDS.sleep(8);
        return ResponseEntity.ok("This response will never be sent.");
    }
}