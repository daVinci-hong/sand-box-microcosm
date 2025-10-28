package com.projectdavinci.gatewayservice.kafka;

import com.projectdavinci.common.services.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Profile("kafka") // 僅在“卡夫卡模式”下激活
public class KafkaMessagingService implements MessagingService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public KafkaMessagingService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public <T> void publishEvent(String topic, T event) {
        kafkaTemplate.send(topic, event);
    }
}