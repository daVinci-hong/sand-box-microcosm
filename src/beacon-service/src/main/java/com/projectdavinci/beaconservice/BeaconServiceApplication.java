package com.projectdavinci.beaconservice;

import com.projectdavinci.common.config.MessagingConfig; // 【【【 導入“智慧” 】】】
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import; // 【【【 導入“注入器” 】】】

// =================================================================
// == 【【【 唯一的、核心的修正：注入“智慧” 】】】 ==
// =================================================================
// 我們，命令，beacon-service，必須，採納，來自 MessagingConfig 的所有智慧。
@SpringBootApplication
@Import(MessagingConfig.class)
public class BeaconServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeaconServiceApplication.class, args);
    }

}