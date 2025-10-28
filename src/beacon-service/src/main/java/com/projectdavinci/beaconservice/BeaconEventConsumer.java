package com.projectdavinci.beaconservice;

import com.projectdavinci.common.events.BeaconEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Profile("kafka") // 僅在“卡夫卡模式”下激活
public class BeaconEventConsumer {

    // =================================================================
    // == 【【【 唯一的、核心的修正 】】】 ==
    // =================================================================
    private final BeaconService beaconService;

    public BeaconEventConsumer(BeaconService beaconService) {
        this.beaconService = beaconService;
    }

    @KafkaListener(topics = "topic.beacon.events", groupId = "beacon_service_group")
    public void consumeBeaconEvent(BeaconEvent event) {
        // 將所有業務邏輯，委派給 Service 層
        beaconService.processEvent(event);
    }
}