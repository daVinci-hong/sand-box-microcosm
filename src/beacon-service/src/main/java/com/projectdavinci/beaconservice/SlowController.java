package com.projectdavinci.beaconservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.TimeUnit;

@RestController
public class SlowController {

    @GetMapping("/slow-endpoint")
    public String handleSlowRequest() throws InterruptedException {
        // 模擬一個耗時 5 秒的作戰任務
        TimeUnit.SECONDS.sleep(5);
        return "Slow processing complete.";
    }
}