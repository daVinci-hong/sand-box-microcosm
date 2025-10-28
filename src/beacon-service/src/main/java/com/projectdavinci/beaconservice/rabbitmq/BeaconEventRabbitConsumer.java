package com.projectdavinci.beaconservice.rabbitmq;

import com.projectdavinci.beaconservice.BeaconService;
import com.projectdavinci.common.events.BeaconEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("rabbitmq") // 僅在“兔子模式”下激活
public class BeaconEventRabbitConsumer {
    private final BeaconService beaconService;

    public BeaconEventRabbitConsumer(BeaconService beaconService) {
        this.beaconService = beaconService;
    }

    @RabbitListener(queues = "#{@beaconEventsQueue.name}")
    public void consumeBeaconEvent(BeaconEvent event) {
        // 將所有業務邏輯，委派給 Service 層
        beaconService.processEvent(event);
    }
}