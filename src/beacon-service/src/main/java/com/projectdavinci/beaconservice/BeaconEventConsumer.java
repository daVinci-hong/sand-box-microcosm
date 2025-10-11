package com.projectdavinci.beaconservice;

import com.projectdavinci.common.events.BeaconEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BeaconEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(BeaconEventConsumer.class);

    @KafkaListener(topics = "topic.beacon.events", groupId = "beacon_service_group")
    public void consumeBeaconEvent(BeaconEvent event) {
        log.info("==> [Beacon Service] Received BeaconEvent <==");
        log.info("  - Event ID: {}", event.eventId());
        log.info("  - Triggered By: {}", event.triggeredBy());
        log.info("  - Timestamp: {}", event.timestamp());

        // 在此處執行核心業務邏輯...
        // 例如：更新數據庫、調用其他服務等。
        log.info("Event processed successfully.");
    }
}