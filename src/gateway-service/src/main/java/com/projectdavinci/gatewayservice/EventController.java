package com.projectdavinci.gatewayservice;

import com.projectdavinci.common.events.BeaconEvent;
import com.projectdavinci.common.services.MessagingService; // 【【【 導入“抽象” 】】】
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
public class EventController {

    // =================================================================
    // == 【【【 唯一的、核心的修正：依賴“抽象”，而非“具體” 】】】 ==
    // =================================================================
    private final MessagingService messagingService;
    private static final String TOPIC = "topic.beacon.events";

    @Autowired
    public EventController(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @PostMapping("/trigger-beacon-event")
    public String triggerBeaconEvent() {
        String user = "gateway-service";
        BeaconEvent event = new BeaconEvent(
                UUID.randomUUID().toString(),
                user,
                Instant.now()
        );

        // 我們，將，通過“抽象”，去，發布事件
        messagingService.publishEvent(TOPIC, event);

        return "Beacon event triggered with ID: " + event.eventId();
    }
}