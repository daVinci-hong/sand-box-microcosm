package com.projectdavinci.gatewayservice;


import com.projectdavinci.common.events.BeaconEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

import com.projectdavinci.common.events.BeaconEvent;

@RestController
public class EventController {

    @Autowired
    private KafkaTemplate<String, BeaconEvent> kafkaTemplate;

    private static final String TOPIC = "topic.beacon.events";

    @PostMapping("/trigger-beacon-event")
    public String triggerBeaconEvent() {
        // 從認證上下文中獲取用戶信息 (此處為模擬)
        String user = "gateway-service";

        BeaconEvent event = new BeaconEvent(
                UUID.randomUUID().toString(),
                user,
                Instant.now()
        );

        // 將事件異步發送到 Kafka 神經中樞
        kafkaTemplate.send(TOPIC, event);

        // 立即返回響應，無需等待下游處理
        return "Beacon event triggered with ID: " + event.eventId();
    }
}